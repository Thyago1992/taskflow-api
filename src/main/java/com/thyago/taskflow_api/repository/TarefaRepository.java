package com.thyago.taskflow_api.repository;

import com.thyago.taskflow_api.entity.Tarefa;
import com.thyago.taskflow_api.enums.StatusTarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    List<Tarefa> findByUsuarioId(Long idUsuario);

    boolean existsByUsuarioId(Long idUsuario);

    List<Tarefa> findByDataValidadeBeforeAndStatusNotInOrderByDataValidadeAsc(
            LocalDate data, Collection<StatusTarefa> status);

}