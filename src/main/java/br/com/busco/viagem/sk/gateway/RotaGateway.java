package br.com.busco.viagem.sk.gateway;

import br.com.busco.viagem.sk.vo.Rota;

import java.util.UUID;

public interface RotaGateway {
    Rota buscarRotaPorId(UUID id);
}
