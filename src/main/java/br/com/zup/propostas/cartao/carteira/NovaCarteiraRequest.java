package br.com.zup.propostas.cartao.carteira;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class NovaCarteiraRequest {

    @NotBlank
    private String email;

    @NotNull
    private TipoCarteira carteira;

    public NovaCarteiraRequest(@NotBlank String email, @NotBlank String carteira) {
        this.email = email;
        this.carteira = TipoCarteira.valueOf(carteira);
    }

    public String getEmail() {
        return email;
    }

    public String getCarteira() {
        return carteira.getDescription();
    }
}
