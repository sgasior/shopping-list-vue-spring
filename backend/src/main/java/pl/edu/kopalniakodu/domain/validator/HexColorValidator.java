package pl.edu.kopalniakodu.domain.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class HexColorValidator implements ConstraintValidator<HexColor, String> {

    @Override
    public void initialize(HexColor constraintAnnotation) {

    }

    @Override
    public boolean isValid(String color, ConstraintValidatorContext context) {

        if (color == null) {
            return true;
        }
        return color.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
    }
}
