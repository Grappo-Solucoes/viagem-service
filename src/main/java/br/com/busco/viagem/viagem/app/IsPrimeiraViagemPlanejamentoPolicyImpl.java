package br.com.busco.viagem.viagem.app;

import br.com.busco.viagem.planejamento.app.IsPrimeiraViagemPlanejamentoPolicy;
import br.com.busco.viagem.sk.ids.PlanejamentoId;
import br.com.busco.viagem.sk.ids.ViagemId;
import br.com.busco.viagem.viagem.domain.Viagem;
import br.com.busco.viagem.viagem.domain.ViagemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Log
@Component
@AllArgsConstructor
public class IsPrimeiraViagemPlanejamentoPolicyImpl implements IsPrimeiraViagemPlanejamentoPolicy {
    private final ViagemRepository repository;

    @Override
    public boolean isPrimeiraViagemDoPlanejamento(ViagemId id) {
        Viagem viagem = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(format("Not found any Viagem with code %s.", id.toUUID())));
        PlanejamentoId planejamento = viagem.getPlanejamento();
        return repository.isPrimeiraViagemDoPlanejamento(id, planejamento);
    }
}
