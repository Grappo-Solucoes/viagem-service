package br.com.busco.viagem.viagem.app;

import br.com.busco.viagem.sk.ids.MotoristaId;
import br.com.busco.viagem.sk.ids.ViagemId;
import br.com.busco.viagem.viagem.domain.PeriodoPlanejado;
import br.com.busco.viagem.viagem.domain.ViagemRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Log
@Component
@AllArgsConstructor
public class DisponibilidadeMotoristaPolicyImpl implements DisponibilidadeMotoristaPolicy {
    private final ViagemRepository repository;

    @Override
    public boolean isDisponivel(MotoristaId motorista, PeriodoPlanejado periodo) {
        return repository.isMotoristaDisponivel(motorista, periodo.getPartida(), periodo.getChegada());
    }
}
