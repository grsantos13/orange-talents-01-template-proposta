package br.com.zup.propostas.cartao.biometria;

import javax.validation.constraints.NotBlank;

public class NovaBiometriaRequest {

    @NotBlank
    private String digital;

    public String getDigital() {
        return digital;
    }
}
