package br.com.busco.viagem.viagem.domain.exceptions;

public final class RealizacaoPeriodoSemDatasPrevistas extends IllegalStateException {
    public RealizacaoPeriodoSemDatasPrevistas() {
        super("Realizacao do periodo está sem datas");
    }

}
