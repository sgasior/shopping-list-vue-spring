package pl.edu.kopalniakodu.domain.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = HexColorValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HexColor {
    String message() default "Hex color code is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
