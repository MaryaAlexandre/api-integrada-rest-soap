package com.exemplo.tarefas.controller;

import com.exemplo.tarefas.model.Tarefa;
import com.exemplo.tarefas.repository.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tarefas") // Endpoint base para este serviço
public class TarefaController {

    @Autowired
    private TarefaRepository tarefaRepository;

    @GetMapping // GET /tarefas
    public List<Tarefa> getAllTarefas() {
        return tarefaRepository.findAll();
    }

    @GetMapping("/{id}") // GET /tarefas/{id}
    public ResponseEntity<Tarefa> getTarefaById(@PathVariable String id) {
        Optional<Tarefa> tarefa = tarefaRepository.findById(id);
        return tarefa.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping // POST /tarefas
    public ResponseEntity<Tarefa> createTarefa(@RequestBody Tarefa tarefa) {
        Tarefa novaTarefa = tarefaRepository.save(tarefa);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaTarefa);
    }

    @PutMapping("/{id}") // PUT /tarefas/{id}
    public ResponseEntity<Tarefa> updateTarefa(@PathVariable String id, @RequestBody Tarefa tarefaAtualizada) {
        return tarefaRepository.findById(id)
                .map(tarefa -> {
                    tarefa.setTitulo(tarefaAtualizada.getTitulo());
                    tarefa.setDescricao(tarefaAtualizada.getDescricao());
                    tarefa.setStatus(tarefaAtualizada.getStatus());
                    Tarefa salvo = tarefaRepository.save(tarefa);
                    return ResponseEntity.ok(salvo);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}") // DELETE /tarefas/{id}
    public ResponseEntity<Void> deleteTarefa(@PathVariable String id) {
        if (tarefaRepository.findById(id).isPresent()) {
            tarefaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/concluir") // POST /tarefas/{id}/concluir
    public ResponseEntity<Tarefa> marcarTarefaConcluida(@PathVariable String id) {
        return tarefaRepository.findById(id)
                .map(tarefa -> {
                    tarefa.setStatus("CONCLUIDA");
                    Tarefa salvo = tarefaRepository.save(tarefa);
                    return ResponseEntity.ok(salvo);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}