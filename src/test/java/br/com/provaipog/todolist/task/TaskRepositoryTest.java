package br.com.provaipog.todolist.task;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private ITaskRepository taskRepository;

    private UUID userId;
    private TaskModel task1;
    private TaskModel task2;
    private TaskModel task3;

    @BeforeEach
    void setUp() throws Exception {
        userId = UUID.randomUUID();
        
        task1 = new TaskModel();
        task1.setIdUser(userId);
        task1.setNome("Tarefa 1");
        task1.setDescricao("Descrição da tarefa 1");
        task1.setPrioridade(Priority.ALTA);
        task1.setSituacao(Situation.ABERTA);
        task1.setDataPrevistaConclusao(LocalDate.now().plusDays(1));

        task2 = new TaskModel();
        task2.setIdUser(userId);
        task2.setNome("Tarefa 2");
        task2.setDescricao("Descrição da tarefa 2");
        task2.setPrioridade(Priority.MEDIA);
        task2.setSituacao(Situation.CONCLUIDA);
        task2.setDataPrevistaConclusao(LocalDate.now().plusDays(2));

        task3 = new TaskModel();
        task3.setIdUser(userId);
        task3.setNome("Estudar Java");
        task3.setDescricao("Estudar Spring Boot");
        task3.setPrioridade(Priority.BAIXA);
        task3.setSituacao(Situation.PENDENTE);
        task3.setDataPrevistaConclusao(LocalDate.now().plusDays(3));

        taskRepository.saveAll(List.of(task1, task2, task3));
    }

    @Test
    void testFindByIdUser() {
        List<TaskModel> tasks = taskRepository.findByIdUser(userId);
        assertEquals(3, tasks.size());
    }

    @Test
    void testFindByIdUserWithFilters_AllTasks() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome"));
        Page<TaskModel> result = taskRepository.findByIdUserWithFilters(
            userId, null, null, null, pageable);
        
        assertEquals(3, result.getTotalElements());
        assertEquals(3, result.getContent().size());
    }

    @Test
    void testFindByIdUserWithFilters_ByNome() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome"));
        Page<TaskModel> result = taskRepository.findByIdUserWithFilters(
            userId, "Estudar", null, null, pageable);
        
        assertEquals(1, result.getTotalElements());
        assertEquals("Estudar Java", result.getContent().get(0).getNome());
    }

    @Test
    void testFindByIdUserWithFilters_ByPrioridade() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome"));
        Page<TaskModel> result = taskRepository.findByIdUserWithFilters(
            userId, null, Priority.ALTA, null, pageable);
        
        assertEquals(1, result.getTotalElements());
        assertEquals(Priority.ALTA, result.getContent().get(0).getPrioridade());
    }

    @Test
    void testFindByIdUserWithFilters_BySituacao() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome"));
        Page<TaskModel> result = taskRepository.findByIdUserWithFilters(
            userId, null, null, Situation.CONCLUIDA, pageable);
        
        assertEquals(1, result.getTotalElements());
        assertEquals(Situation.CONCLUIDA, result.getContent().get(0).getSituacao());
    }

    @Test
    void testFindByIdUserWithFilters_CombinedFilters() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome"));
        Page<TaskModel> result = taskRepository.findByIdUserWithFilters(
            userId, "Tarefa", Priority.MEDIA, Situation.CONCLUIDA, pageable);
        
        assertEquals(1, result.getTotalElements());
        assertEquals("Tarefa 2", result.getContent().get(0).getNome());
    }

    @Test
    void testFindByIdUserWithFilters_Pagination() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("nome"));
        Page<TaskModel> result = taskRepository.findByIdUserWithFilters(
            userId, null, null, null, pageable);
        
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalPages());
    }

    @Test
    void testFindByIdUserWithFilters_EmptyResult() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome"));
        Page<TaskModel> result = taskRepository.findByIdUserWithFilters(
            userId, "Inexistente", null, null, pageable);
        
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }
}
