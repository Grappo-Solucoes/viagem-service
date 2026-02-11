package br.com.busco.viagem.viagem.app;

import br.com.busco.viagem.sk.ids.MotoristaId;

public interface DisponibilidadeMotoristaPolicy {
    boolean isDisponivel(MotoristaId motorista);
}
