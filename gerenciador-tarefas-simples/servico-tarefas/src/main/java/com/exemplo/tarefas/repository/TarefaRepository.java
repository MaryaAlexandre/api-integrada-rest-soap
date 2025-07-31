package com.exemplo.tarefas.repository;

import com.exemplo.tarefas.model.Tarefa;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TarefaRepository {
    private final ConcurrentHashMap<String, Tarefa> tarefas = new ConcurrentHashMap<>();

    public TarefaRepository() {
        // Tarefas iniciais
        Tarefa t1 = new Tarefa("Comprar pão", "Pão integral e ovos");
        t1.setStatus("PENDENTE");
        tarefas.put(t1.getId(), t1);

        Tarefa t2 = new Tarefa("Estudar para a prova", "Revisar capítulo 5 de Cálculo");
        t2.setStatus("PENDENTE");
        tarefas.put(t2.getId(), t2);

        Tarefa t3 = new Tarefa("Pagar conta de luz", "Vencimento dia 25");
        t3.setStatus("CONCLUIDA");
        tarefas.put(t3.getId(), t3);
    }

    public List<Tarefa> findAll() {
        return new ArrayList<>(tarefas.values());
    }

    public Optional<Tarefa> findById(String id) {
        return Optional.ofNullable(tarefas.get(id));
    }

    public Tarefa save(Tarefa tarefa) {
        tarefas.put(tarefa.getId(), tarefa);
        return tarefa;
    }

    public void deleteById(String id) {
        tarefas.remove(id);
    }
}