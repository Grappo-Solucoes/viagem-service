package br.com.busco.viagem.infra.gateway;

import br.com.busco.viagem.ocorrencia.app.BuscarViagemGateway;
import br.com.busco.viagem.sk.ids.ViagemId;
import br.com.busco.viagem.viagem.domain.ViagemRepository;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BuscarViagemGatewayImpl implements BuscarViagemGateway {

    private final ViagemRepository viagemRepository;


    @Override
    public boolean existsById(ViagemId id) {
        return viagemRepository.findById(id).isPresent();
    }
}
