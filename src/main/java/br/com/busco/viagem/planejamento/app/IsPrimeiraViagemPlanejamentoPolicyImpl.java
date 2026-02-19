package br.com.busco.viagem.planejamento.app;

import br.com.busco.viagem.sk.ids.ViagemId;
import br.com.busco.viagem.viagem.domain.ViagemRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Log
@Component
@AllArgsConstructor
public class IsPrimeiraViagemPlanejamentoPolicyImpl implements IsPrimeiraViagemPlanejamentoPolicy {
    private final ViagemRepository repository;

    @Override
    public boolean isPrimeiraViagemDoPlanejamento(ViagemId id) {
        return Boolean.TRUE.equals(repository.isPrimeiraViagemDoPlanejamento(id));
    }
}
