package com.thyago.taskflow_api.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusTarefaTest {

    @Test
    void pendentePodeIrParaEmAndamentoConcluidaOuCancelada() {
        assertTrue(StatusTarefa.PENDENTE.podeIrPara(StatusTarefa.EM_ANDAMENTO));
        assertTrue(StatusTarefa.PENDENTE.podeIrPara(StatusTarefa.CONCLUIDA));
        assertTrue(StatusTarefa.PENDENTE.podeIrPara(StatusTarefa.CANCELADA));
    }

    @Test
    void emAndamentoPodeVoltarParaPendente() {
        assertTrue(StatusTarefa.EM_ANDAMENTO.podeIrPara(StatusTarefa.PENDENTE));
    }

    @Test
    void concluidaNaoPodeMudarDeStatus() {
        assertFalse(StatusTarefa.CONCLUIDA.podeIrPara(StatusTarefa.PENDENTE));
        assertFalse(StatusTarefa.CONCLUIDA.podeIrPara(StatusTarefa.EM_ANDAMENTO));
        assertFalse(StatusTarefa.CONCLUIDA.podeIrPara(StatusTarefa.CANCELADA));
    }

    @Test
    void canceladaNaoPodeMudarDeStatus() {
        assertFalse(StatusTarefa.CANCELADA.podeIrPara(StatusTarefa.PENDENTE));
        assertFalse(StatusTarefa.CANCELADA.podeIrPara(StatusTarefa.EM_ANDAMENTO));
    }
}