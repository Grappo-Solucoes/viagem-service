package br.com.busco.viagem.ocorrencia.domain;

import br.com.busco.viagem.ocorrencia.domain.exceptions.NaoPossivelFinalizarOcorrenciaQueNaoSejaTratativas;
import br.com.busco.viagem.ocorrencia.domain.exceptions.NaoPossivelIniciarAnaliseOcorrenciaQueNaoSejaPendente;
import br.com.busco.viagem.ocorrencia.domain.exceptions.NaoPossivelIniciarTratativasOcorrenciaQueNaoSejaEmAndamento;
import br.com.busco.viagem.sk.ids.TipoOcorrenciaId;
import br.com.busco.viagem.sk.ids.UserId;
import br.com.busco.viagem.sk.ids.ViagemId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OcorrenciaTest {

    @Test
    void deveCriarOcorrenciaComStatusPendente() {
        Ocorrencia ocorrencia = Ocorrencia.builder()
                .viagem(ViagemId.fromString(UUID.randomUUID().toString()))
                .tipoOcorrencia(TipoOcorrenciaId.fromString(UUID.randomUUID().toString()))
                .motivo("Pneu furado")
                .userId(UserId.randomId())
                .setorResponsavel(SetorResponsavel.of("Operacao"))
                .responsavelTratativas(UserId.randomId())
                .build();

        assertThat(ocorrencia.getId()).isNotNull();
        assertThat(ocorrencia.getStatusOcorrencia()).isEqualTo(StatusOcorrencia.PENDENTE);
        assertThat(ocorrencia.getData()).isNotNull();
        assertThat(ocorrencia.getDataInicioAnalise()).isNull();
        assertThat(ocorrencia.getDataInicioTratativas()).isNull();
        assertThat(ocorrencia.getDataFinalizada()).isNull();
        assertThat(ocorrencia.getSetorResponsavel()).isEqualTo(SetorResponsavel.of("Operacao"));
        assertThat(ocorrencia.getResponsavelTratativas()).isNotEqualTo(UserId.VAZIO);
    }

    @Test
    void deveIniciarAnaliseQuandoOcorrenciaPendente() {
        Ocorrencia ocorrencia = novaOcorrencia();

        ocorrencia.iniciarAnalise();

        assertThat(ocorrencia.getStatusOcorrencia()).isEqualTo(StatusOcorrencia.EM_ANDAMENTO);
        assertThat(ocorrencia.getDataInicioAnalise()).isNotNull();
    }

    @Test
    void naoDeveIniciarAnaliseQuandoOcorrenciaNaoPendente() {
        Ocorrencia ocorrencia = novaOcorrencia();
        ocorrencia.iniciarAnalise();

        assertThatThrownBy(ocorrencia::iniciarAnalise)
                .isInstanceOf(NaoPossivelIniciarAnaliseOcorrenciaQueNaoSejaPendente.class)
                .hasMessage("A analise so pode ser iniciada a partir do status PENDENTE.");
    }

    @Test
    void deveIniciarTratativasQuandoOcorrenciaEmAndamento() {
        Ocorrencia ocorrencia = novaOcorrencia();
        ocorrencia.iniciarAnalise();

        ocorrencia.iniciarTratativas();

        assertThat(ocorrencia.getStatusOcorrencia()).isEqualTo(StatusOcorrencia.TRATATIVAS);
        assertThat(ocorrencia.getDataInicioTratativas()).isNotNull();
    }

    @Test
    void naoDeveIniciarTratativasQuandoOcorrenciaNaoEstaEmAndamento() {
        Ocorrencia ocorrencia = novaOcorrencia();

        assertThatThrownBy(ocorrencia::iniciarTratativas)
                .isInstanceOf(NaoPossivelIniciarTratativasOcorrenciaQueNaoSejaEmAndamento.class)
                .hasMessage("As tratativas so podem ser iniciadas a partir do status EM_ANDAMENTO.");
    }

    @Test
    void deveFinalizarQuandoOcorrenciaEmTratativas() {
        Ocorrencia ocorrencia = novaOcorrencia();
        ocorrencia.iniciarAnalise();
        ocorrencia.iniciarTratativas();

        ocorrencia.finalizar();

        assertThat(ocorrencia.getStatusOcorrencia()).isEqualTo(StatusOcorrencia.FINALIZADA);
        assertThat(ocorrencia.getDataFinalizada()).isNotNull();
    }

    @Test
    void naoDeveFinalizarQuandoOcorrenciaNaoEstaEmTratativas() {
        Ocorrencia ocorrencia = novaOcorrencia();

        assertThatThrownBy(ocorrencia::finalizar)
                .isInstanceOf(NaoPossivelFinalizarOcorrenciaQueNaoSejaTratativas.class)
                .hasMessage("A ocorrencia so pode ser finalizada a partir do status TRATATIVAS.");
    }

    private static Ocorrencia novaOcorrencia() {
        return Ocorrencia.builder()
                .viagem(ViagemId.fromString(UUID.randomUUID().toString()))
                .tipoOcorrencia(TipoOcorrenciaId.fromString(UUID.randomUUID().toString()))
                .motivo("Quebra de veiculo")
                .userId(UserId.randomId())
                .build();
    }
}
