package br.com.busco.viagem.viagem.domain.exceptions;

public final class ViagemIncompleta extends IllegalStateException {
    public ViagemIncompleta() {
        super("Viagem incompleta");
    }

}
