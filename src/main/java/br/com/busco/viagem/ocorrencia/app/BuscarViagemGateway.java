package br.com.busco.viagem.ocorrencia.app;

import br.com.busco.viagem.sk.ids.ViagemId;

public interface BuscarViagemGateway {

    boolean existsById(ViagemId id);
}
