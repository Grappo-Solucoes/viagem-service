package br.com.busco.viagem.viagem.app;

import br.com.busco.viagem.sk.ids.MotoristaId;
import br.com.busco.viagem.sk.ids.ViagemId;
import br.com.busco.viagem.viagem.domain.PeriodoPlanejado;

public interface DisponibilidadeMotoristaPolicy {
    boolean isDisponivel(MotoristaId motorista, PeriodoPlanejado periodo);
}
