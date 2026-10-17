package com.thyago.taskflow_api.repository;

import com.thyago.taskflow_api.entity.Tarefa;
import com.thyago.taskflow_api.enums.StatusTarefa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    List<Tarefa> findByUsuarioId(Long idUsuario);

    boolean existsByUsuarioId(Long idUsuario);

    Page<Tarefa> findByStatus(StatusTarefa status, Pageable pageable);

    List<Tarefa> findByDataValidadeBeforeAndStatusNotInOrderByDataValidadeAsc(
            LocalDate data, Collection<StatusTarefa> status);

}