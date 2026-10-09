package br.com.busco.viagem.ui.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Task 4.1: as 11 escritas com a semântica do monólito; conceitos fora do
 * core (ausência, métrica, ocorrência) respondem 404.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ViagemControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private org.springframework.context.ApplicationContext context;

    private UUID rotaId;
    private UUID motoristaId;
    private UUID monitorId;
    private UUID veiculoId;

    @BeforeEach
    void setUp() throws Exception {
        SecurityContextHolder.clearContext();
        motoristaId = UUID.randomUUID();
        monitorId = UUID.randomUUID();
        veiculoId = UUID.randomUUID();
        rotaId = criarRota();
    }

    private UUID criarRota() throws Exception {
        Map<String, Object> body = Map.of(
                "numeroRota", "R-" + UUID.randomUUID().toString().substring(0, 8),
                "nome", "Rota teste",
                "enderecos", List.of(Map.of(
                        "logradouro", "Rua A", "numero", 100, "bairro", "Centro",
                        "cidade", "Cidade", "estado", "SP", "cep", "01001000")),
                "tempoEstimado", "40min",
                "distancia", "12km");
        MvcResult result = mvc.perform(post("/api/viagem/rota")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn();
        String location = result.getResponse().getHeader("Location");
        assertThat(location).isNotNull();
        String id = location.substring(location.lastIndexOf('/') + 1);
        return UUID.fromString(id);
    }

    private UUID criarViagem() throws Exception {
        return criarViagem(Map.of(
                "data", LocalDateTime.now().plusDays(1).toString(),
                "rota", rotaId.toString(),
                "alunos", List.of(UUID.randomUUID().toString()),
                "motorista", motoristaId.toString(),
                "monitor", monitorId.toString(),
                "veiculo", veiculoId.toString()));
    }

    private UUID criarViagem(Map<String, Object> body) throws Exception {
        MvcResult result = mvc.perform(post("/api/viagem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn();
        String location = result.getResponse().getHeader("Location");
        assertThat(location).isNotNull();
        return UUID.fromString(location.substring(location.lastIndexOf('/') + 1));
    }

    private void comoMonitor(UUID id) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(id.toString(), null,
                        List.of(new SimpleGrantedAuthority("ROLE_MONITOR"))));
    }

    @Test
    void deveCriarViagemEmPlanejada() throws Exception {
        UUID id = criarViagem();
        assertThat(id).isNotNull();
    }

    @Test
    void deveRecusarCriacaoSemReferenciaObrigatoria() throws Exception {
        mvc.perform(post("/api/viagem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"data":"2030-01-01T10:00:00","rota":"%s","monitor":"%s","veiculo":"%s"}"""
                                .formatted(rotaId, monitorId, veiculoId)))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/api/viagem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"data":"2030-01-01T10:00:00","rota":"%s","motorista":"%s","monitor":"%s","veiculo":"%s"}"""
                                .formatted(UUID.randomUUID(), motoristaId, monitorId, veiculoId)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveEditarViagemParcial() throws Exception {
        UUID id = criarViagem();
        UUID novoMotorista = UUID.randomUUID();

        mvc.perform(put("/api/viagem/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"motorista":"%s"}""".formatted(novoMotorista)))
                .andExpect(status().isCreated());
    }

    @Test
    void deveRecusarEdicaoComMotoristaOcupado() throws Exception {
        UUID id1 = criarViagem();
        String data = LocalDateTime.now().plusDays(2).withNano(0).toString();
        UUID id2 = criarViagem(Map.of(
                "data", data,
                "rota", rotaId.toString(),
                "motorista", UUID.randomUUID().toString(),
                "monitor", UUID.randomUUID().toString(),
                "veiculo", UUID.randomUUID().toString()));
        // tenta colocar o motorista da viagem 2 na viagem 1, na mesma data da 2
        mvc.perform(put("/api/viagem/" + id1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "data", data,
                                "motorista", motoristaId.toString()))))
                .andExpect(status().isCreated());

        // agora a viagem 2 tenta usar o mesmo motorista na mesma data → 409
        mvc.perform(put("/api/viagem/" + id2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "motorista", motoristaId.toString()))))
                .andExpect(status().isConflict());
    }

    @Test
    void deveIniciarFinalizarECancelar() throws Exception {
        UUID id = criarViagem();

        mvc.perform(patch("/api/viagem/" + id + "/iniciar"))
                .andExpect(status().isCreated());
        mvc.perform(patch("/api/viagem/" + id + "/iniciar"))
                .andExpect(status().isBadRequest());

        mvc.perform(patch("/api/viagem/" + id + "/finalizar"))
                .andExpect(status().isCreated());

        UUID outra = criarViagem();
        mvc.perform(patch("/api/viagem/" + outra + "/finalizar"))
                .andExpect(status().isBadRequest());

        UUID terceira = criarViagem();
        mvc.perform(patch("/api/viagem/" + terceira + "/cancelar"))
                .andExpect(status().isCreated());

        mvc.perform(patch("/api/viagem/" + id + "/cancelar"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAssumirComMonitorAutenticado() throws Exception {
        UUID id = criarViagem();
        UUID novoMonitor = UUID.randomUUID();
        comoMonitor(novoMonitor);

        mvc.perform(patch("/api/viagem/" + id + "/assumir"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRecusarAssumirSemMonitor() throws Exception {
        UUID id = criarViagem();

        mvc.perform(patch("/api/viagem/" + id + "/assumir"))
                .andExpect(status().isForbidden());

        comoMonitor(UUID.randomUUID());
        mvc.perform(patch("/api/viagem/00000000-0000-0000-0000-000000000000/assumir"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRecusarAssumirViagemIniciada() throws Exception {
        UUID id = criarViagem();
        mvc.perform(patch("/api/viagem/" + id + "/iniciar"))
                .andExpect(status().isCreated());

        comoMonitor(UUID.randomUUID());
        mvc.perform(patch("/api/viagem/" + id + "/assumir"))
                .andExpect(status().isConflict());
    }

    @Test
    void deveEmbarcarEDesembarcar() throws Exception {
        UUID aluno = UUID.randomUUID();
        UUID id = criarViagem(Map.of(
                "data", LocalDateTime.now().plusDays(1).toString(),
                "rota", rotaId.toString(),
                "alunos", List.of(aluno.toString()),
                "motorista", motoristaId.toString(),
                "monitor", monitorId.toString(),
                "veiculo", veiculoId.toString()));
        mvc.perform(patch("/api/viagem/" + id + "/iniciar"))
                .andExpect(status().isCreated());

        mvc.perform(patch("/api/viagem/" + id + "/embarcar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"aluno":"%s","latitude":-23.5,"longitude":-46.6}""".formatted(aluno)))
                .andExpect(status().isOk());

        mvc.perform(patch("/api/viagem/" + id + "/embarcar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"aluno":"%s"}""".formatted(aluno)))
                .andExpect(status().isOk());

        mvc.perform(patch("/api/viagem/" + id + "/embarcar-todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        mvc.perform(patch("/api/viagem/" + id + "/desembarcar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"aluno":"%s","latitude":-23.5,"longitude":-46.6}""".formatted(aluno)))
                .andExpect(status().isOk());

        mvc.perform(patch("/api/viagem/" + id + "/desembarcar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"aluno":"%s"}""".formatted(aluno)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveExigirJustificativaParaNaoPlanejado() throws Exception {
        UUID id = criarViagem();
        String estranho = UUID.randomUUID().toString();

        mvc.perform(patch("/api/viagem/" + id + "/embarcar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"aluno":"%s"}""".formatted(estranho)))
                .andExpect(status().isBadRequest());

        mvc.perform(patch("/api/viagem/" + id + "/embarcar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"aluno":"%s","justificativa":"aluno de outra rota"}""".formatted(estranho)))
                .andExpect(status().isOk());
    }

    @Test
    void deveEditarRota() throws Exception {
        mvc.perform(put("/api/viagem/rota/" + rotaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Rota atualizada","numeroRota":"R-999"}"""))
                .andExpect(status().isCreated());

        mvc.perform(put("/api/viagem/rota/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Inexistente"}"""))
                .andExpect(status().isNotFound());
    }

    @Test
    void schedulerLegadoDesligadoSemTabelaAlheia() {
        assertThat(context.getBeansOfType(
                br.com.busco.viagem.infra.job.ViagemProgramadaScheduler.class)).isEmpty();
    }

    @Test
    void conceitosForaDoCoreRespondem404() throws Exception {
        mvc.perform(post("/api/viagem/ausencia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());

        mvc.perform(post("/api/viagem/metrica")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());

        mvc.perform(post("/api/viagem/ocorrencia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }

}
