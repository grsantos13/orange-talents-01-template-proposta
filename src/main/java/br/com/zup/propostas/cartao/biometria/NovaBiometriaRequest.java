package br.com.zup.propostas.cartao.biometria;

import br.com.zup.propostas.compartilhado.validacao.base64.Base64;

import javax.validation.constraints.NotBlank;

public class NovaBiometriaRequest {

    @NotBlank
    @Base64
    private String digital;

    @Deprecated
    public NovaBiometriaRequest() {
    }

    public NovaBiometriaRequest(@NotBlank String digital) {
        this.digital = digital;
    }

    public String getDigital() {
        return digital;
    }
}
