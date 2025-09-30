package br.com.provaipog.todolist.task;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ITaskRepository extends JpaRepository<TaskModel, UUID> {
    List<TaskModel> findByIdUser(UUID idUser);
    
    @Query("SELECT t FROM tb_tasks t WHERE t.idUser = :idUser " +
           "AND (:nome IS NULL OR LOWER(t.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) " +
           "AND (:prioridade IS NULL OR t.prioridade = :prioridade) " +
           "AND (:situacao IS NULL OR t.situacao = :situacao)")
    Page<TaskModel> findByIdUserWithFilters(
        @Param("idUser") UUID idUser,
        @Param("nome") String nome,
        @Param("prioridade") Priority prioridade,
        @Param("situacao") Situation situacao,
        Pageable pageable
    );
}
