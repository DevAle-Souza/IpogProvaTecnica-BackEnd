package br.com.provaipog.todolist.task;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.provaipog.todolist.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/tarefas")
@Tag(name = "Tarefas", description = "API para gerenciamento de tarefas")
public class TaskController {
    
    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    @Operation(summary = "Criar nova tarefa", description = "Cria uma nova tarefa para o usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<?> create(@Valid @RequestBody TaskModel taskModel, HttpServletRequest request) {
        try {
            var idUser = request.getAttribute("idUser");
            taskModel.setIdUser((UUID) idUser);
            
            // Validações de negócio
            if (taskModel.getDataPrevistaConclusao().isBefore(LocalDate.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data prevista de conclusão não pode ser anterior à data atual");
            }
            
            // Define valores padrão
            if (taskModel.getPrioridade() == null) {
                taskModel.setPrioridade(Priority.BAIXA);
            }
            if (taskModel.getSituacao() == null) {
                taskModel.setSituacao(Situation.ABERTA);
            }

            var task = this.taskRepository.save(taskModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(task);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/")
    @Operation(summary = "Listar tarefas", description = "Lista tarefas do usuário com filtros, paginação e ordenação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de tarefas retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<Page<TaskModel>> list(
            @Parameter(description = "Filtro por nome (contém)") @RequestParam(required = false) String nome,
            @Parameter(description = "Filtro por prioridade") @RequestParam(required = false) Priority prioridade,
            @Parameter(description = "Filtro por situação") @RequestParam(required = false) Situation situacao,
            @Parameter(description = "Número da página (inicia em 0)") @RequestParam(defaultValue = "0") int pagina,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int tamanho,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "nome") String ordenarPor,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)") @RequestParam(defaultValue = "ASC") String direcao,
            HttpServletRequest request) {
        
        var idUser = request.getAttribute("idUser");
        
        Sort sort = Sort.by(Sort.Direction.fromString(direcao), ordenarPor);
        Pageable pageable = PageRequest.of(pagina, tamanho, sort);
        
        Page<TaskModel> tasks = this.taskRepository.findByIdUserWithFilters(
            (UUID) idUser, nome, prioridade, situacao, pageable);
        
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id, HttpServletRequest request) {
        var task = this.taskRepository.findById(id).orElse(null);
        
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada");
        }
        
        var idUser = request.getAttribute("idUser");
        if (!task.getIdUser().equals(idUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuário não tem permissão para acessar essa tarefa");
        }
        
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
        try {
            var task = this.taskRepository.findById(id).orElse(null);

            if (task == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada");
            }

            var idUser = request.getAttribute("idUser");
            if (!task.getIdUser().equals(idUser)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuário não tem permissão para alterar essa tarefa");
            }

            // Validações de negócio
            if (taskModel.getDataPrevistaConclusao() != null && 
                taskModel.getDataPrevistaConclusao().isBefore(LocalDate.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data prevista de conclusão não pode ser anterior à data atual");
            }

            // Não permite alterar a situação diretamente
            if (taskModel.getSituacao() != null && !taskModel.getSituacao().equals(task.getSituacao())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A situação não pode ser alterada diretamente. Use os endpoints específicos (/concluir ou /pendente)");
            }

            Utils.copyNonNullProperties(taskModel, task);
            var taskUpdated = this.taskRepository.save(task);
            return ResponseEntity.ok(taskUpdated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id, HttpServletRequest request) {
        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada");
        }

        var idUser = request.getAttribute("idUser");
        if (!task.getIdUser().equals(idUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuário não tem permissão para excluir essa tarefa");
        }

        this.taskRepository.delete(task);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/concluir")
    @Operation(summary = "Marcar tarefa como concluída", description = "Marca uma tarefa como concluída")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tarefa marcada como concluída"),
        @ApiResponse(responseCode = "400", description = "Tarefa já está concluída"),
        @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
        @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<?> marcarComoConcluida(@PathVariable UUID id, HttpServletRequest request) {
        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada");
        }

        var idUser = request.getAttribute("idUser");
        if (!task.getIdUser().equals(idUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuário não tem permissão para alterar essa tarefa");
        }

        if (task.getSituacao() == Situation.CONCLUIDA) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa já está concluída");
        }

        task.setSituacao(Situation.CONCLUIDA);
        var taskUpdated = this.taskRepository.save(task);
        return ResponseEntity.ok(taskUpdated);
    }

    @PatchMapping("/{id}/pendente")
    @Operation(summary = "Marcar tarefa como pendente", description = "Marca uma tarefa como pendente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tarefa marcada como pendente"),
        @ApiResponse(responseCode = "400", description = "Tarefa já está pendente"),
        @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
        @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<?> marcarComoPendente(@PathVariable UUID id, HttpServletRequest request) {
        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada");
        }

        var idUser = request.getAttribute("idUser");
        if (!task.getIdUser().equals(idUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuário não tem permissão para alterar essa tarefa");
        }

        if (task.getSituacao() == Situation.PENDENTE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa já está pendente");
        }

        task.setSituacao(Situation.PENDENTE);
        var taskUpdated = this.taskRepository.save(task);
        return ResponseEntity.ok(taskUpdated);
    }
}