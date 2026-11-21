package org.example.windy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.windy.client.WindyApiClient;
import org.example.windy.config.WindyProperties;
import org.example.windy.exception.WindyException;
import org.example.windy.model.WindyLoginParam;
import org.example.windy.model.WindySession;
import org.example.windy.model.WindyTokenClaims;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

/**
 * Windy 会话管理：缓存 token，过期前自动重新预登录 / 登录。
 * <p>
 * token 有效期约 2 天，这里提前 {@code refreshBefore} 刷新，避免请求到一半过期。
 *
 * @author windy
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WindySessionManager {

    private final WindyApiClient windyApiClient;

    private final WindyProperties properties;

    private final ObjectMapper objectMapper;

    private volatile WindySession cachedSession;

    public WindySession getSession() {
        return getSession(false);
    }

    /**
     * 获取可用会话。
     *
     * @param forceRefresh 是否强制刷新
     * @return 会话
     */
    public synchronized WindySession getSession(boolean forceRefresh) {
        Instant now = Instant.now();
        if (!forceRefresh && cachedSession != null && cachedSession.getTokenExpiresAt() != null
                && cachedSession.getTokenExpiresAt().isAfter(now.plus(properties.getRefreshBefore()))) {
            return cachedSession;
        }

        WindySession guestSession = windyApiClient.preLogin();
        WindySession session = guestSession;
        if (properties.isLoginEnabled()
                && StringUtils.hasText(properties.getEmail())
                && StringUtils.hasText(properties.getPassword())) {
            try {
                session = windyApiClient.login(guestSession.getUid(), guestSession.getToken(),
                        WindyLoginParam.builder()
                                .email(properties.getEmail())
                                .password(properties.getPassword())
                                .build());
            } catch (WindyException exception) {
                log.warn("Windy 登录失败，回退游客 token（游客只能拿到 3 小时步长）: {}",
                        exception.getMessage());
            }
        }

        enrich(session);
        cachedSession = session;
        log.info("Windy 会话已刷新: uid={}, auth={}, premium={}, expiresAt={}",
                session.getUid(), session.getAuth(), session.getPremium(), session.getTokenExpiresAt());
        return session;
    }

    /**
     * 解析 JWT payload，补齐过期时间、premium 标记与 userID。
     */
    private void enrich(WindySession session) {
        WindyTokenClaims claims = parseClaims(session.getToken());
        if (claims != null) {
            if (claims.getExp() != null) {
                session.setTokenExpiresAt(Instant.ofEpochSecond(claims.getExp()));
            }
            session.setPremium(Optional.ofNullable(claims.getSubscriptionTiers())
                    .orElseGet(List::of)
                    .stream()
                    .anyMatch("premium"::equalsIgnoreCase));
            if (claims.getUserId() != null) {
                session.setUserId(claims.getUserId());
            }
        }
        if (session.getTokenExpiresAt() == null) {
            session.setTokenExpiresAt(Instant.now().plus(Duration.ofHours(1)));
        }
        if (session.getPremium() == null) {
            session.setPremium(false);
        }
    }

    private WindyTokenClaims parseClaims(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            return null;
        }
        try {
            byte[] payload = Base64.getUrlDecoder().decode(parts[1]);
            return objectMapper.readValue(payload, WindyTokenClaims.class);
        } catch (Exception exception) {
            log.debug("解析 Windy token 失败: {}", exception.getMessage());
            return null;
        }
    }
}
