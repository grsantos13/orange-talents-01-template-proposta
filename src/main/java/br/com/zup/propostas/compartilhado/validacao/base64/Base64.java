package br.com.zup.propostas.compartilhado.validacao.base64;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = Base64Validator.class)
public @interface Base64 {

    String message() default "{br.com.zup.propostas.base64}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
