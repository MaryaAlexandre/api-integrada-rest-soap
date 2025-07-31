package com.exemplo.tarefas.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Tarefa {
    private String id;
    private String titulo;
    private String descricao;
    private String status; // Ex: PENDENTE, CONCLUIDA
    private LocalDateTime dataCriacao;

    public Tarefa() {
        this.id = UUID.randomUUID().toString();
        this.dataCriacao = LocalDateTime.now();
        this.status = "PENDENTE";
    }

    public Tarefa(String titulo, String descricao) {
        this();
        this.titulo = titulo;
        this.descricao = descricao;
    }

    // --- Getters e Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}