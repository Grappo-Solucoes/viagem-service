package br.com.busco.viagem.ocorrencia.domain.exceptions;

public final class NaoPossivelCriarEmergenciaSemMotivo extends IllegalStateException {
    public NaoPossivelCriarEmergenciaSemMotivo() {
        super("Emergencia nao pode ser criada sem motivo.");
    }
}
