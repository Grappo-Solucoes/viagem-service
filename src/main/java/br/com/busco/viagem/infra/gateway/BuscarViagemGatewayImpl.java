package br.com.busco.viagem.infra.gateway;

import br.com.busco.viagem.ocorrencia.app.BuscarViagemGateway;
import br.com.busco.viagem.sk.ids.ViagemId;
import br.com.busco.viagem.viagem.domain.ViagemRepository;
import org.springframework.stereotype.Component;

@Component
public class BuscarViagemGatewayImpl implements BuscarViagemGateway {

    private final ViagemRepository viagemRepository;

    public BuscarViagemGatewayImpl(ViagemRepository viagemRepository) {
        this.viagemRepository = viagemRepository;
    }

    @Override
    public boolean existsById(ViagemId id) {
        return viagemRepository.findById(id).isPresent();
    }
}
