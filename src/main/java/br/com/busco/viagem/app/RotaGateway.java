package br.com.busco.viagem.app;

import br.com.busco.viagem.domain.ParadaPrevista;
import br.com.busco.viagem.sk.ids.RotaId;

import java.time.LocalTime;
import java.util.List;

public interface RotaGateway {

    List<ParadaPrevista> calcularParadasComHorarios(
             RotaId rotaId,
             LocalTime horarioPartida
    );

}
