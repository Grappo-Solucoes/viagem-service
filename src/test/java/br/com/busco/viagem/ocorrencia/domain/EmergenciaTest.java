package br.com.busco.viagem.ocorrencia.domain;

import br.com.busco.viagem.ocorrencia.domain.exceptions.NaoPossivelCriarEmergenciaSemMotivo;
import br.com.busco.viagem.sk.ids.UserId;
import br.com.busco.viagem.sk.ids.ViagemId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmergenciaTest {

    @Test
    void deveCriarEmergencia() {
        Emergencia emergencia = Emergencia.builder()
                .viagem(ViagemId.fromString(UUID.randomUUID().toString()))
                .motivo("Aluno passou mal")
                .tipoEmergencia(TipoEmergencia.DESMAIO)
                .userId(UserId.randomId())
                .build();

        assertThat(emergencia.getId()).isNotNull();
        assertThat(emergencia.getMotivo()).isEqualTo("Aluno passou mal");
        assertThat(emergencia.getTipoEmergencia()).isEqualTo(TipoEmergencia.DESMAIO);
        assertThat(emergencia.getData()).isNotNull();
    }

    @Test
    void naoDeveCriarEmergenciaSemMotivo() {
        assertThatThrownBy(() -> Emergencia.builder()
                .viagem(ViagemId.fromString(UUID.randomUUID().toString()))
                .motivo("   ")
                .tipoEmergencia(TipoEmergencia.DESMAIO)
                .userId(UserId.randomId())
                .build())
                .isInstanceOf(NaoPossivelCriarEmergenciaSemMotivo.class)
                .hasMessage("Emergencia nao pode ser criada sem motivo.");
    }
}
