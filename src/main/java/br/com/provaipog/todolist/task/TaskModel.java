package br.com.provaipog.todolist.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonAlias;

@Data
@Entity(name = "tb_tasks")
public class TaskModel {
    
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false, length = 100)
    @JsonAlias({"name"})
    private String nome;
    
    @Column(length = 500)
    @JsonAlias({"description"})
    private String descricao;
    
    @NotNull(message = "Prioridade é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JsonAlias({"priority"})
    private Priority prioridade;
    
    @NotNull(message = "Situação é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JsonAlias({"situation"})
    private Situation situacao = Situation.ABERTA;
    
    @NotNull(message = "Data prevista de conclusão é obrigatória")
    @Column(nullable = false)
    @JsonAlias({"expectedCompletionDate", "dueDate"})
    private LocalDate dataPrevistaConclusao;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @JsonAlias({"userId"})
    private UUID idUser;
    
    public void setNome(String nome) throws Exception {
        if (nome == null || nome.trim().isEmpty()) {
            throw new Exception("O campo nome é obrigatório");
        }
        if (nome.length() > 100) {
            throw new Exception("O campo nome deve conter no máximo 100 caracteres");
        }
        this.nome = nome;
    }
    
    public void setDataPrevistaConclusao(LocalDate dataPrevistaConclusao) throws Exception {
        if (dataPrevistaConclusao == null) {
            throw new Exception("A data prevista de conclusão é obrigatória");
        }
        if (dataPrevistaConclusao.isBefore(LocalDate.now())) {
            throw new Exception("A data prevista de conclusão não pode ser anterior à data atual");
        }
        this.dataPrevistaConclusao = dataPrevistaConclusao;
    }
}
