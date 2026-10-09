package br.com.busco.viagem.ui.rest;

import br.com.busco.viagem.app.EmbarcarAlunoResultado;
import br.com.busco.viagem.app.EmbarcarTodosResultado;
import br.com.busco.viagem.app.ViagemService;
import br.com.busco.viagem.app.cmd.AtualizarRota;
import br.com.busco.viagem.app.cmd.AtualizarViagem;
import br.com.busco.viagem.app.cmd.CancelarViagem;
import br.com.busco.viagem.app.cmd.CriarRota;
import br.com.busco.viagem.app.cmd.CriarViagem;
import br.com.busco.viagem.app.cmd.DesembarcarAluno;
import br.com.busco.viagem.app.cmd.EmbarcarAluno;
import br.com.busco.viagem.app.cmd.EmbarcarTodos;
import br.com.busco.viagem.app.cmd.FinalizarViagem;
import br.com.busco.viagem.app.cmd.IniciarViagem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

/**
 * Escritas do core de viagem — mesmo contrato do {@code ViagemController} do
 * monólito (paths, verbos, payloads, status). Ausência, métrica e ocorrência
 * pertencem a outros contextos: essas rotas não existem aqui (404).
 */
@RestController
@RequestMapping("/api/viagem")
@AllArgsConstructor
public class ViagemController {

    private final ViagemService service;

    @PostMapping
    @Operation(summary = "Cadastra Viagem", description = "Cadastra Viagem.")
    @ApiResponse(responseCode = "201", description = "Viagem cadastrada com sucesso", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<Void> cadastrar(@RequestBody @jakarta.validation.Valid @NonNull CriarViagem cmd) throws Exception {
        UUID id = UUID.fromString(service.handle(cmd).toUUID());

        return ResponseEntity.created(fromCurrentRequest()
                        .path("/").path(id.toString()).build().toUri())
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar Viagem", description = "Editar a Viagem.")
    @ApiResponse(responseCode = "201", description = "Viagem editada com sucesso", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<Void> editar(@PathVariable UUID id, @RequestBody @jakarta.validation.Valid @NonNull AtualizarViagem cmd) throws Exception {
        cmd.setId(id);
        service.handle(cmd);

        return ResponseEntity.created(fromCurrentRequest()
                        .path("/").path(id.toString()).build().toUri())
                .build();
    }

    @PatchMapping("/{id}/iniciar")
    @Operation(summary = "Iniciar viagem", description = "Iniciar a viagem.")
    @ApiResponse(responseCode = "201", description = "Iniciar viagem com sucesso", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<Void> iniciar(@PathVariable UUID id) throws Exception {
        IniciarViagem cmd = IniciarViagem.builder().id(id).build();
        service.handle(cmd);

        return ResponseEntity.created(fromCurrentRequest()
                        .path("/").path(id.toString()).build().toUri())
                .build();
    }

    @PatchMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar viagem", description = "Finalizar a viagem.")
    @ApiResponse(responseCode = "201", description = "Finalizar viagem com sucesso", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<Void> finalizar(@PathVariable UUID id) throws Exception {
        FinalizarViagem cmd = FinalizarViagem.builder().id(id).build();
        service.handle(cmd);

        return ResponseEntity.created(fromCurrentRequest()
                        .path("/").path(id.toString()).build().toUri())
                .build();
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar viagem", description = "Cancelar a Viagem.")
    @ApiResponse(responseCode = "201", description = "Viagem cancelada com sucesso", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<Void> cancelar(@PathVariable UUID id) throws Exception {
        CancelarViagem cmd = CancelarViagem.builder().id(id).build();
        service.handle(cmd);

        return ResponseEntity.created(fromCurrentRequest()
                        .path("/").path(id.toString()).build().toUri())
                .build();
    }

    @PatchMapping("/{id}/assumir")
    @Operation(summary = "Assumir viagem", description = "Substitui o monitor da viagem planejada pelo monitor autenticado.")
    @ApiResponse(responseCode = "204", description = "Viagem assumida com sucesso")
    @ApiResponse(responseCode = "403", description = "Usuário autenticado não é monitor")
    @ApiResponse(responseCode = "404", description = "Viagem não encontrada")
    @ApiResponse(responseCode = "409", description = "Viagem indisponível ou conflito de horário")
    public ResponseEntity<Void> assumir(@PathVariable UUID id) {
        MonitorAutenticado monitor = monitorAutenticado();
        service.assumirViagem(id, monitor.id(), monitor.ehMonitor());
        return ResponseEntity.noContent().build();
    }

    /**
     * Identidade do monitor a partir da autenticação. Contrato esperado do
     * token: authority {@code ROLE_MONITOR} e nome = UUID do monitor (ou
     * claim equivalente mapeada pelo gateway). Sem isso → não é monitor.
     */
    static MonitorAutenticado monitorAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return new MonitorAutenticado(null, false);
        }
        boolean ehMonitor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MONITOR")
                        || a.getAuthority().equals("MONITOR"));
        UUID id = null;
        try {
            id = UUID.fromString(auth.getName());
        } catch (IllegalArgumentException ignored) {
            // nome não é UUID → sem id de monitor
        }
        return new MonitorAutenticado(id, ehMonitor);
    }

    record MonitorAutenticado(UUID id, boolean ehMonitor) {
    }

    @PatchMapping("/{id}/embarcar")
    @Operation(summary = "Embarcar aluno", description = "Embarcar aluno. O corpo deve conter o texto da carteirinha ou o id do aluno.")
    @ApiResponse(responseCode = "200", description = "Aluno embarcado com sucesso", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<EmbarcarAlunoResultado> embarcar(@PathVariable UUID id,
                                                          @RequestBody @jakarta.validation.Valid @NonNull EmbarcarAluno body) throws Exception {
        EmbarcarAluno cmd = EmbarcarAluno.builder().id(id).aluno(body.getAluno())
                .latitude(body.getLatitude()).longitude(body.getLongitude())
                .justificativa(body.getJustificativa()).build();
        EmbarcarAlunoResultado resultado = service.handle(cmd);

        return ResponseEntity.ok(resultado);
    }

    @PatchMapping("/{id}/embarcar-todos")
    @Operation(summary = "Embarcar todos os alunos pendentes", description = "Embarca de uma vez todos os alunos da viagem que ainda não embarcaram.")
    @ApiResponse(responseCode = "200", description = "Alunos embarcados com sucesso", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<EmbarcarTodosResultado> embarcarTodos(@PathVariable UUID id) throws Exception {
        EmbarcarTodos cmd = EmbarcarTodos.builder().id(id).build();
        EmbarcarTodosResultado resultado = service.handle(cmd);

        return ResponseEntity.ok(resultado);
    }

    @PatchMapping("/{id}/desembarcar")
    @Operation(summary = "Desembarcar aluno", description = "Desembarcar aluno. O corpo deve conter o texto da carteirinha ou o id do aluno.")
    @ApiResponse(responseCode = "200", description = "Aluno desembarcado com sucesso", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<EmbarcarAlunoResultado> desembarcar(@PathVariable UUID id,
                                                              @RequestBody @jakarta.validation.Valid @NonNull DesembarcarAluno body) throws Exception {
        DesembarcarAluno cmd = DesembarcarAluno.builder().id(id).aluno(body.getAluno())
                .latitude(body.getLatitude()).longitude(body.getLongitude()).build();
        EmbarcarAlunoResultado resultado = service.handle(cmd);

        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/rota")
    @Operation(summary = "Cadastra Rota", description = "Cadastra Rota.")
    @ApiResponse(responseCode = "201", description = "Rota cadastrada com sucesso", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<Void> cadastrarRota(@RequestBody @jakarta.validation.Valid @NonNull CriarRota cmd) throws Exception {
        UUID id = UUID.fromString(service.handle(cmd).toUUID());

        return ResponseEntity.created(fromCurrentRequest()
                        .path("/").path(id.toString()).build().toUri())
                .build();
    }

    @PutMapping("/rota/{id}")
    @Operation(summary = "Editar Rota", description = "Editar a Rota.")
    @ApiResponse(responseCode = "201", description = "Viagem editada com sucesso", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<Void> editarRota(@PathVariable UUID id, @RequestBody @jakarta.validation.Valid @NonNull AtualizarRota cmd) throws Exception {
        cmd.setId(id);
        service.handle(cmd);

        return ResponseEntity.created(fromCurrentRequest()
                        .path("/").path(id.toString()).build().toUri())
                .build();
    }

    /**
     * Conceitos fora do core (outros contextos): ausência, métrica e
     * ocorrência não existem aqui — 404 em vez do 405 padrão.
     */
    @PostMapping({"/ausencia", "/metrica", "/ocorrencia"})
    @PatchMapping({"/ausencia", "/metrica", "/ocorrencia"})
    @PutMapping({"/ausencia", "/metrica", "/ocorrencia"})
    public ResponseEntity<Void> foraDoCore() {
        return ResponseEntity.notFound().build();
    }
}
