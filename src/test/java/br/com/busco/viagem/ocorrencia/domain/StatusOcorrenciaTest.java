package br.com.busco.viagem.ocorrencia.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StatusOcorrenciaTest {

    @Test
    void deveConverterStatusPorStringSemCaseSensitive() {
        assertThat(StatusOcorrencia.fromString("pendente")).isEqualTo(StatusOcorrencia.PENDENTE);
        assertThat(StatusOcorrencia.fromString("EM_ANDAMENTO")).isEqualTo(StatusOcorrencia.EM_ANDAMENTO);
        assertThat(StatusOcorrencia.fromString("tratativas")).isEqualTo(StatusOcorrencia.TRATATIVAS);
        assertThat(StatusOcorrencia.fromString("finalizada")).isEqualTo(StatusOcorrencia.FINALIZADA);
    }

    @Test
    void deveLancarExcecaoQuandoStatusInvalido() {
        assertThatThrownBy(() -> StatusOcorrencia.fromString("desconhecido"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Status de Ocorrencia invalido: desconhecido");
    }
}
