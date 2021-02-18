package br.com.zup.propostas.cartao.biometria;

import br.com.zup.propostas.compartilhado.validacao.base64.Base64;

import javax.validation.constraints.NotNull;

public class NovaBiometriaRequest {

    @NotNull
    @Base64
    private byte[] digital;

    @Deprecated
    public NovaBiometriaRequest() {
    }

    public NovaBiometriaRequest(@NotNull byte[] digital) {
        this.digital = digital;
    }

    public byte[] getDigital() {
        return digital;
    }
}
