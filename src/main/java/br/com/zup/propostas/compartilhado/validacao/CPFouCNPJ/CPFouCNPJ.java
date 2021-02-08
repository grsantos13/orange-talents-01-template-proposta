package br.com.zup.propostas.compartilhado.validacao.CPFouCNPJ;

import org.hibernate.validator.constraints.CompositionType;
import org.hibernate.validator.constraints.ConstraintComposition;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@CPF
@CNPJ
@ConstraintComposition(CompositionType.OR)
public @interface CPFouCNPJ {
    String message() default "{br.com.zup.propostas.cpfoucnpj}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
