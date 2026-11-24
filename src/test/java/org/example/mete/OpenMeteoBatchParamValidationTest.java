package org.example.mete;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link OpenMeteoBatchParam} 参数校验测试。
 *
 * @author windy
 */
class OpenMeteoBatchParamValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldRejectMismatchedCoordinateSize() {
        OpenMeteoBatchParam param = OpenMeteoBatchParam.builder()
                .latitude(List.of(39.9, 31.2))
                .longitude(List.of(116.4))
                .build();

        Set<ConstraintViolation<OpenMeteoBatchParam>> violations = validator.validate(param);

        assertThat(violations).anyMatch(violation -> violation.getMessage().contains("数量必须一致"));
    }

    @Test
    void shouldRejectOutOfRangeLatitude() {
        OpenMeteoBatchParam param = OpenMeteoBatchParam.builder()
                .latitude(List.of(100.0))
                .longitude(List.of(116.4))
                .build();

        Set<ConstraintViolation<OpenMeteoBatchParam>> violations = validator.validate(param);

        assertThat(violations).anyMatch(violation -> violation.getMessage().contains("纬度必须在"));
    }

    @Test
    void shouldAcceptValidBatchParam() {
        OpenMeteoBatchParam param = OpenMeteoBatchParam.builder()
                .latitude(List.of(39.9, 31.2))
                .longitude(List.of(116.4, 121.5))
                .build();

        assertThat(validator.validate(param)).isEmpty();
    }
}
