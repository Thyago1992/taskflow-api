package com.thyago.taskflow_api.repository;

import com.thyago.taskflow_api.entity.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    List<Tarefa> findByIdUsuario(Long idUsuario);

}
