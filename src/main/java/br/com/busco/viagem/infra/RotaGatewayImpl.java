package br.com.busco.viagem.infra;

import br.com.busco.viagem.app.RotaGateway;
import br.com.busco.viagem.domain.ParadaPrevista;
import br.com.busco.viagem.sk.ids.RotaId;
import org.springframework.stereotype.Component;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Component
public class RotaGatewayImpl implements RotaGateway {

    @Override
    public List<ParadaPrevista> calcularParadasComHorarios(RotaId rotaId, LocalTime horarioPartida) {
        return Collections.emptyList();
    }
}
