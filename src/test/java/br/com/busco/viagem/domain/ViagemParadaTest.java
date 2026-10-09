package br.com.busco.viagem.domain;

import br.com.busco.viagem.domain.events.AtrasoCritico;
import br.com.busco.viagem.domain.events.ChegadaParada;
import br.com.busco.viagem.domain.events.SaidaParada;
import br.com.busco.viagem.sk.ids.RotaId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Task 3.2: paradas com GPS e regras de atraso, sem listas de
 * ausência/ocorrência.
 */
class ViagemParadaTest {

    private Viagem viagem;
    private LocalDateTime previsto;

    @BeforeEach
    void setUp() {
        previsto = LocalDateTime.now().plusHours(1);
        viagem = Viagem.builder()
                .data(LocalDateTime.now().plusDays(1))
                .rota(RotaId.randomId())
                .motoristaId(UUID.randomUUID())
                .monitorId(UUID.randomUUID())
                .veiculoId(UUID.randomUUID())
                .periodoViagem(PeriodoViagem.of(previsto.minusHours(1), previsto.plusHours(1)))
                .build();
        viagem.iniciar(LocalDateTime.now(), List.of(
                ParadaPrevista.builder().ordem(1).horarioPrevisto(previsto).quantidadeAlunos(2).build()));
    }

    @Test
    void deveRegistrarChegadaESaidaComGps() {
        viagem.registrarChegadaParada(1, previsto, -23.55, -46.63);

        ParadaExecucao parada = viagem.getParadas().get(0);
        assertThat(parada.getStatus()).isEqualTo(StatusParada.CHEGADA_REGISTRADA);
        assertThat(parada.getLatitudeChegada()).isEqualTo(-23.55);
        assertThat(parada.getLongitudeChegada()).isEqualTo(-46.63);

        viagem.registrarSaidaParada(1, previsto.plusMinutes(2), -23.56, -46.64);
        assertThat(parada.getStatus()).isEqualTo(StatusParada.CONCLUIDA);
        assertThat(parada.getLatitudeSaida()).isEqualTo(-23.56);

        assertThat(eventosDe(viagem)).hasAtLeastOneElementOfType(ChegadaParada.class);
        assertThat(eventosDe(viagem)).hasAtLeastOneElementOfType(SaidaParada.class);
    }

    @Test
    void devePublicarAtrasoCriticoQuandoChegadaPassaDe15Min() {
        viagem.registrarChegadaParada(1, previsto.plusMinutes(20), null, null);

        assertThat(viagem.getAtrasoAcumulado().toMinutes()).isEqualTo(20);
        assertThat(viagem.isAtrasada()).isTrue();
        assertThat(eventosDe(viagem)).hasAtLeastOneElementOfType(AtrasoCritico.class);
    }

    @Test
    void naoDevePublicarAtrasoCriticoQuandoNoHorario() {
        viagem.registrarChegadaParada(1, previsto.plusMinutes(5), null, null);

        assertThat(viagem.getAtrasoAcumulado().toMinutes()).isEqualTo(5);
        assertThat(eventosDe(viagem)).filteredOn(AtrasoCritico.class::isInstance).isEmpty();
    }

    @Test
    void naoDeveRegistrarParadaComViagemPausada() {
        viagem.pausar();

        assertThatThrownBy(() -> viagem.registrarChegadaParada(1, previsto, null, null))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void viagemNaoTemListasDeAusenciaNemOcorrencia() {
        assertThat(viagem.getClass().getDeclaredFields())
                .noneMatch(f -> f.getName().equalsIgnoreCase("ausencias")
                        || f.getName().equalsIgnoreCase("ocorrencias"));
    }

    private static Collection<Object> eventosDe(Viagem viagem) {
        try {
            Method metodo = viagem.getClass().getSuperclass().getDeclaredMethod("domainEvents");
            metodo.setAccessible(true);
            @SuppressWarnings("unchecked")
            Collection<Object> eventos = (Collection<Object>) metodo.invoke(viagem);
            return eventos;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
