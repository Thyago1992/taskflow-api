package com.thyago.taskflow_api.enums;

public enum StatusTarefa {
    PENDENTE,
    EM_ANDAMENTO,
    CONCLUIDA,
    CANCELADA;

    public boolean podeIrPara(StatusTarefa destino) {
        return switch (this) {
            case PENDENTE -> destino != PENDENTE;
            case EM_ANDAMENTO -> destino != EM_ANDAMENTO;
            case CONCLUIDA, CANCELADA -> false;
        };
    }
}