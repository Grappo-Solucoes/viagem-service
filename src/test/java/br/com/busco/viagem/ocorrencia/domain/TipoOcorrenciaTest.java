package br.com.busco.viagem.ocorrencia.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TipoOcorrenciaTest {

    @Test
    void deveCriarTipoOcorrencia() {
        TipoOcorrencia tipoOcorrencia = TipoOcorrencia.of("MECANICA");

        assertThat(tipoOcorrencia.getId()).isNotNull();
        assertThat(tipoOcorrencia.getTipoOcorrencia()).isEqualTo("MECANICA");
    }

    @Test
    void deveAlterarTipoOcorrencia() {
        TipoOcorrencia tipoOcorrencia = TipoOcorrencia.of("MECANICA");

        tipoOcorrencia.alterarTipoOcorrencia("DISCIPLINAR");

        assertThat(tipoOcorrencia.getTipoOcorrencia()).isEqualTo("DISCIPLINAR");
    }
}
