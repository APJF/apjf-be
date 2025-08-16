package fu.sep.apjf.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation validation cho ID
 * Đảm bảo ID chỉ chứa chữ cái, số, dấu gạch ngang và gạch dưới
 */
@Documented
@Constraint(validatedBy = {})
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Pattern(regexp = "^[-a-zA-Z0-9_]+$", message = "ID chỉ được chứa chữ cái, số, dấu gạch ngang (-) và dấu gạch dưới (_)")
public @interface ValidId {
    String message() default "ID không hợp lệ";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
