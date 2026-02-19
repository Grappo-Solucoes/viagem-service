package br.com.busco.viagem.ocorrencia.domain.exceptions;

public final class NaoPossivelIniciarAnaliseOcorrenciaQueNaoSejaPendente extends IllegalStateException {
    public NaoPossivelIniciarAnaliseOcorrenciaQueNaoSejaPendente() {
        super("A analise so pode ser iniciada a partir do status PENDENTE.");
    }
}
