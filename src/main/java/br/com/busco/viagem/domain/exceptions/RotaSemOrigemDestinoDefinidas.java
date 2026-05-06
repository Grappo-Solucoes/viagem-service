package br.com.busco.viagem.domain.exceptions;

public final class RotaSemOrigemDestinoDefinidas extends IllegalStateException {
    public RotaSemOrigemDestinoDefinidas() {
        super("Rota esta sem definicao de Origem e Destino definidas");
    }

}
