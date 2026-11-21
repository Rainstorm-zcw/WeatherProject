package org.example.mete;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link OpenMeteoParam} 参数校验测试。
 *
 * @author windy
 */
class OpenMeteoParamValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldRejectLatitudeOutOfRange() {
        OpenMeteoParam param = OpenMeteoParam.builder().latitude(100.0).build();

        Set<ConstraintViolation<OpenMeteoParam>> violations = validator.validate(param);

        assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString().equals("latitude"));
    }

    @Test
    void shouldRejectOneSidedDateRange() {
        OpenMeteoParam param = OpenMeteoParam.builder()
                .startDate(LocalDate.of(2026, 11, 20))
                .build();

        Set<ConstraintViolation<OpenMeteoParam>> violations = validator.validate(param);

        assertThat(violations).anyMatch(violation -> violation.getMessage().contains("必须同时指定"));
    }

    @Test
    void shouldRejectReversedDateRange() {
        OpenMeteoParam param = OpenMeteoParam.builder()
                .startDate(LocalDate.of(2026, 11, 21))
                .endDate(LocalDate.of(2026, 11, 20))
                .build();

        Set<ConstraintViolation<OpenMeteoParam>> violations = validator.validate(param);

        assertThat(violations).anyMatch(violation -> violation.getMessage().contains("不能早于"));
    }

    @Test
    void shouldRejectInvalidModels() {
        OpenMeteoParam param = OpenMeteoParam.builder()
                .models("icon_global;drop table")
                .build();

        Set<ConstraintViolation<OpenMeteoParam>> violations = validator.validate(param);

        assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString().equals("models"));
    }

    @Test
    void shouldAcceptDefaultParam() {
        assertThat(validator.validate(OpenMeteoParam.builder().build())).isEmpty();
    }
}
