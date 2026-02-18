package br.com.busco.viagem.ocorrencia.app;

import br.com.busco.viagem.ocorrencia.app.cmd.AlterarTipoDeOcorrencia;
import br.com.busco.viagem.ocorrencia.app.cmd.CriarEmergencia;
import br.com.busco.viagem.ocorrencia.app.cmd.CriarOcorrencia;
import br.com.busco.viagem.ocorrencia.app.cmd.CriarTipoDeOcorrencia;
import br.com.busco.viagem.ocorrencia.domain.Emergencia;
import br.com.busco.viagem.ocorrencia.domain.EmergenciaRepository;
import br.com.busco.viagem.ocorrencia.domain.Ocorrencia;
import br.com.busco.viagem.ocorrencia.domain.OcorrenciaRepository;
import br.com.busco.viagem.ocorrencia.domain.StatusOcorrencia;
import br.com.busco.viagem.ocorrencia.domain.TipoOcorrencia;
import br.com.busco.viagem.ocorrencia.domain.TipoEmergencia;
import br.com.busco.viagem.ocorrencia.domain.TipoOcorrenciaRepository;
import br.com.busco.viagem.sk.ids.EmergenciaId;
import br.com.busco.viagem.sk.ids.OcorrenciaId;
import br.com.busco.viagem.sk.ids.TipoOcorrenciaId;
import br.com.busco.viagem.sk.ids.ViagemId;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OcorrenciaServiceTest {

    private OcorrenciaService service;
    private InMemoryOcorrenciaRepository ocorrenciaRepository;
    private InMemoryEmergenciaRepository emergenciaRepository;
    private InMemoryTipoOcorrenciaRepository tipoOcorrenciaRepository;
    private InMemoryBuscarViagemGateway buscarViagemGateway;
    private FixedUsuarioAutenticadoGateway usuarioAutenticadoGateway;

    @BeforeEach
    void setUp() {
        ocorrenciaRepository = new InMemoryOcorrenciaRepository();
        emergenciaRepository = new InMemoryEmergenciaRepository();
        tipoOcorrenciaRepository = new InMemoryTipoOcorrenciaRepository();
        buscarViagemGateway = new InMemoryBuscarViagemGateway();
        usuarioAutenticadoGateway = new FixedUsuarioAutenticadoGateway(UUID.randomUUID());
        service = new OcorrenciaService(
                ocorrenciaRepository,
                emergenciaRepository,
                tipoOcorrenciaRepository,
                buscarViagemGateway,
                usuarioAutenticadoGateway
        );
    }

    @Test
    void deveCriarTipoOcorrencia() {
        CriarTipoDeOcorrencia cmd = CriarTipoDeOcorrencia.builder()
                .tipoOcorrencia("MECANICA")
                .build();

        TipoOcorrenciaId id = service.handle(cmd);

        assertThat(id).isNotNull();
        Optional<TipoOcorrencia> salvo = tipoOcorrenciaRepository.findById(id);
        assertThat(salvo).isPresent();
        assertThat(salvo.get().getTipoOcorrencia()).isEqualTo("MECANICA");
    }

    @Test
    void deveAlterarTipoOcorrencia() {
        TipoOcorrencia tipo = TipoOcorrencia.of("MECANICA");
        tipoOcorrenciaRepository.save(tipo);

        AlterarTipoDeOcorrencia cmd = AlterarTipoDeOcorrencia.builder()
                .id(UUID.fromString(tipo.getId().toUUID()))
                .tipoOcorrencia("DISCIPLINAR")
                .build();

        TipoOcorrenciaId id = service.handle(cmd);

        assertThat(id).isEqualTo(tipo.getId());
        assertThat(tipoOcorrenciaRepository.findById(id)).isPresent();
        assertThat(tipoOcorrenciaRepository.findById(id).get().getTipoOcorrencia()).isEqualTo("DISCIPLINAR");
    }

    @Test
    void naoDeveAlterarTipoOcorrenciaInexistente() {
        AlterarTipoDeOcorrencia cmd = AlterarTipoDeOcorrencia.builder()
                .id(UUID.randomUUID())
                .tipoOcorrencia("DISCIPLINAR")
                .build();

        assertThatThrownBy(() -> service.handle(cmd))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Tipo de ocorrencia nao encontrado.");
    }

    @Test
    void deveCriarOcorrencia() {
        TipoOcorrencia tipo = TipoOcorrencia.of("MECANICA");
        tipoOcorrenciaRepository.save(tipo);

        CriarOcorrencia cmd = CriarOcorrencia.builder()
                .viagem(viagemExistente())
                .tipoOcorrencia(UUID.fromString(tipo.getId().toUUID()))
                .motivo("Pane no motor")
                .setorResponsavel("Operacao")
                .responsavelTratativas("Maria")
                .build();

        OcorrenciaId id = service.handle(cmd);

        Ocorrencia ocorrencia = ocorrenciaRepository.findById(id).orElseThrow();
        assertThat(ocorrencia.getMotivo()).isEqualTo("Pane no motor");
        assertThat(ocorrencia.getStatusOcorrencia()).isEqualTo(StatusOcorrencia.PENDENTE);
        assertThat(ocorrencia.getSetorResponsavel()).isEqualTo("Operacao");
        assertThat(ocorrencia.getResponsavelTratativas()).isEqualTo("Maria");
        assertThat(ocorrencia.getUserId()).isEqualTo(usuarioAutenticadoGateway.getUserId());
    }

    @Test
    void naoDeveCriarOcorrenciaSemTipoExistente() {
        CriarOcorrencia cmd = CriarOcorrencia.builder()
                .viagem(viagemExistente())
                .tipoOcorrencia(UUID.randomUUID())
                .motivo("Pane no motor")
                .build();

        assertThatThrownBy(() -> service.handle(cmd))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Tipo de ocorrencia nao encontrado.");
    }

    @Test
    void naoDeveCriarOcorrenciaSemViagemExistente() {
        TipoOcorrencia tipo = TipoOcorrencia.of("MECANICA");
        tipoOcorrenciaRepository.save(tipo);

        CriarOcorrencia cmd = CriarOcorrencia.builder()
                .viagem(UUID.randomUUID())
                .tipoOcorrencia(UUID.fromString(tipo.getId().toUUID()))
                .motivo("Pane no motor")
                .build();

        assertThatThrownBy(() -> service.handle(cmd))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Viagem nao encontrada.");
    }

    @Test
    void deveCriarEmergencia() {
        CriarEmergencia cmd = CriarEmergencia.builder()
                .viagem(viagemExistente())
                .motivo("Acidente na via")
                .tipoEmergencia(TipoEmergencia.ACIDENTE)
                .build();

        EmergenciaId id = service.handle(cmd);

        Emergencia emergencia = emergenciaRepository.findById(id).orElseThrow();
        assertThat(emergencia.getMotivo()).isEqualTo("Acidente na via");
        assertThat(emergencia.getTipoEmergencia()).isEqualTo(TipoEmergencia.ACIDENTE);
        assertThat(emergencia.getData()).isNotNull();
        assertThat(emergencia.getUserId()).isEqualTo(usuarioAutenticadoGateway.getUserId());
    }

    @Test
    void naoDeveCriarEmergenciaSemViagemExistente() {
        CriarEmergencia cmd = CriarEmergencia.builder()
                .viagem(UUID.randomUUID())
                .motivo("Acidente na via")
                .tipoEmergencia(TipoEmergencia.ACIDENTE)
                .build();

        assertThatThrownBy(() -> service.handle(cmd))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Viagem nao encontrada.");
    }

    @Test
    void deveIniciarAnalise() {
        Ocorrencia ocorrencia = salvarOcorrenciaBase();

        service.iniciarAnalise(ocorrencia.getId());

        Ocorrencia atualizada = ocorrenciaRepository.findById(ocorrencia.getId()).orElseThrow();
        assertThat(atualizada.getStatusOcorrencia()).isEqualTo(StatusOcorrencia.EM_ANDAMENTO);
        assertThat(atualizada.getDataInicioAnalise()).isNotNull();
    }

    @Test
    void deveIniciarTratativas() {
        Ocorrencia ocorrencia = salvarOcorrenciaBase();
        service.iniciarAnalise(ocorrencia.getId());

        service.iniciarTratativas(ocorrencia.getId());

        Ocorrencia atualizada = ocorrenciaRepository.findById(ocorrencia.getId()).orElseThrow();
        assertThat(atualizada.getStatusOcorrencia()).isEqualTo(StatusOcorrencia.TRATATIVAS);
        assertThat(atualizada.getDataInicioTratativas()).isNotNull();
    }

    @Test
    void deveFinalizarOcorrencia() {
        Ocorrencia ocorrencia = salvarOcorrenciaBase();
        service.iniciarAnalise(ocorrencia.getId());
        service.iniciarTratativas(ocorrencia.getId());

        service.finalizar(ocorrencia.getId());

        Ocorrencia atualizada = ocorrenciaRepository.findById(ocorrencia.getId()).orElseThrow();
        assertThat(atualizada.getStatusOcorrencia()).isEqualTo(StatusOcorrencia.FINALIZADA);
        assertThat(atualizada.getDataFinalizada()).isNotNull();
    }

    @Test
    void naoDeveIniciarAnaliseQuandoOcorrenciaNaoExiste() {
        assertThatThrownBy(() -> service.iniciarAnalise(OcorrenciaId.fromString(UUID.randomUUID().toString())))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Ocorrencia nao encontrada.");
    }

    @Test
    void deveBuscarOcorrenciaPorId() {
        Ocorrencia ocorrencia = salvarOcorrenciaBase();

        Ocorrencia encontrada = service.buscarPorId(ocorrencia.getId());

        assertThat(encontrada.getId()).isEqualTo(ocorrencia.getId());
    }

    @Test
    void naoDeveBuscarOcorrenciaInexistente() {
        assertThatThrownBy(() -> service.buscarPorId(OcorrenciaId.fromString(UUID.randomUUID().toString())))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Ocorrencia nao encontrada.");
    }

    private Ocorrencia salvarOcorrenciaBase() {
        TipoOcorrencia tipo = TipoOcorrencia.of("MECANICA");
        tipoOcorrenciaRepository.save(tipo);
        UUID viagemExistente = viagemExistente();

        Ocorrencia ocorrencia = Ocorrencia.builder()
                .viagem(ViagemId.fromString(viagemExistente.toString()))
                .tipoOcorrencia(tipo.getId())
                .motivo("Fumaça no motor")
                .userId(usuarioAutenticadoGateway.getUserId())
                .build();

        return ocorrenciaRepository.save(ocorrencia);
    }

    private UUID viagemExistente() {
        UUID viagem = UUID.randomUUID();
        buscarViagemGateway.addViagem(viagem);
        return viagem;
    }

    private static class InMemoryOcorrenciaRepository implements OcorrenciaRepository {
        private final Map<OcorrenciaId, Ocorrencia> storage = new HashMap<>();

        @Override
        public Ocorrencia save(Ocorrencia ocorrencia) {
            storage.put(ocorrencia.getId(), ocorrencia);
            return ocorrencia;
        }

        @Override
        public Optional<Ocorrencia> findById(OcorrenciaId id) {
            return Optional.ofNullable(storage.get(id));
        }
    }

    private static class InMemoryTipoOcorrenciaRepository implements TipoOcorrenciaRepository {
        private final Map<TipoOcorrenciaId, TipoOcorrencia> storage = new HashMap<>();

        @Override
        public TipoOcorrencia save(TipoOcorrencia tipoOcorrencia) {
            storage.put(tipoOcorrencia.getId(), tipoOcorrencia);
            return tipoOcorrencia;
        }

        @Override
        public Optional<TipoOcorrencia> findById(TipoOcorrenciaId id) {
            return Optional.ofNullable(storage.get(id));
        }
    }

    private static class InMemoryEmergenciaRepository implements EmergenciaRepository {
        private final Map<EmergenciaId, Emergencia> storage = new HashMap<>();

        @Override
        public Emergencia save(Emergencia emergencia) {
            storage.put(emergencia.getId(), emergencia);
            return emergencia;
        }

        @Override
        public Optional<Emergencia> findById(EmergenciaId id) {
            return Optional.ofNullable(storage.get(id));
        }
    }

    private static class InMemoryBuscarViagemGateway implements BuscarViagemGateway {
        private final Map<ViagemId, Boolean> viagens = new HashMap<>();

        @Override
        public boolean existsById(ViagemId id) {
            return viagens.getOrDefault(id, false);
        }

        public void addViagem(UUID viagemId) {
            viagens.put(ViagemId.fromString(viagemId.toString()), true);
        }
    }

    private record FixedUsuarioAutenticadoGateway(UUID userId) implements UsuarioAutenticadoGateway {
        @Override
        public UUID getUserId() {
            return userId;
        }
    }
}
