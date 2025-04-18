package com.biblioteca.repository;

import com.biblioteca.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    List<Emprestimo> findByAlunoId(Long alunoId);
    
    @Query("SELECT e FROM Emprestimo e WHERE e.dataPrevistaDevolucao < :dataAtual AND e.dataDevolvido IS NULL")
    List<Emprestimo> findAllAtrasados(@Param("dataAtual") LocalDateTime dataAtual);
    
    @Query("SELECT COUNT(e) FROM Emprestimo e WHERE e.aluno.id = :alunoId AND e.dataDevolucao IS NULL")
    long countEmprestimosAtivos(Long alunoId);

    @Query("SELECT COUNT(e) FROM Emprestimo e WHERE e.livro.id = :livroId")
    Long countByLivroId(@Param("livroId") Long livroId);

    long countByDataEmprestimoBetween(LocalDateTime inicio, LocalDateTime fim);
    
    long countByDataPrevistaDevolucaoBeforeAndDataDevolucaoIsNull(LocalDateTime data);
    
    long countByDataDevolucaoIsNotNull();
    
    @Query("SELECT COUNT(e) FROM Emprestimo e WHERE e.dataDevolucao IS NOT NULL AND e.dataDevolucao <= e.dataPrevistaDevolucao")
    long countByDataDevolucaoIsNotNullAndDataDevolucaoBeforeDataPrevistaDevolucao();
} 