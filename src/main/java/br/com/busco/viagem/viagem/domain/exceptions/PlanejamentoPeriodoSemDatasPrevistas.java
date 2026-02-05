package br.com.busco.viagem.viagem.domain.exceptions;

public final class PlanejamentoPeriodoSemDatasPrevistas extends IllegalStateException {
    public PlanejamentoPeriodoSemDatasPrevistas() {
        super("Planejamento do periodo está sem previsao de datas");
    }

}
