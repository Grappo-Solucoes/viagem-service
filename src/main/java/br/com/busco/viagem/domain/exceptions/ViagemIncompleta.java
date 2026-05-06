package br.com.busco.viagem.domain.exceptions;

public final class ViagemIncompleta extends IllegalStateException {
    public ViagemIncompleta() {
        super("Viagem incompleta");
    }

}
