package br.com.zup.propostas.cartao.biometria;

import javax.validation.constraints.NotNull;

public class NovaBiometriaRequest {

    @NotNull
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
