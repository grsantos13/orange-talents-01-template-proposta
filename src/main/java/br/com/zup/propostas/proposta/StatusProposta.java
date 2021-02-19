package br.com.zup.propostas.proposta;

public enum StatusProposta {
    ELEGIVEL("SEM_RESTRICAO"),
    NAO_ELEGIVEL("COM_RESTRICAO"),
    CARTAO_ASSOCIADO("ASSOCIADO");

    private String descricao;

    StatusProposta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static StatusProposta toEnum(String resultadoAnalise){
        if (resultadoAnalise == null)
            return null;

        for (StatusProposta status : StatusProposta.values()) {
            if (status.getDescricao().equals(resultadoAnalise))
                return status;
        }

        throw new IllegalArgumentException("Nenhuma descriçao de status foi entrada para " + resultadoAnalise);
    }
}
