package br.com.provaipog.todolist.task;

import lombok.Data;

@Data
public class TaskFilterRequest {
    private String nome;
    private Priority prioridade;
    private Situation situacao;
    private int pagina = 0;
    private int tamanho = 10;
    private String ordenarPor = "nome";
    private String direcao = "ASC";
}
