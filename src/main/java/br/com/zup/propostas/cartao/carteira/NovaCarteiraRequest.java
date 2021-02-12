package br.com.zup.propostas.cartao.carteira;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class NovaCarteiraRequest {

    @Email
    @NotBlank
    private String email;

    @NotNull
    private TipoCarteira carteira;

    public NovaCarteiraRequest(@NotBlank String email, @NotNull TipoCarteira carteira) {
        this.email = email;
        this.carteira = carteira;
    }

    public String getEmail() {
        return email;
    }

    public String getCarteira() {
            return carteira.name();
    }
}
