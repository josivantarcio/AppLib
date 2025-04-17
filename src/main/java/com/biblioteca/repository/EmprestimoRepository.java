package com.biblioteca.repository;

import com.biblioteca.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    List<Emprestimo> findByAlunoId(Long alunoId);
    
    @Query("SELECT e FROM Emprestimo e WHERE e.dataDevolucao IS NULL AND e.dataPrevistaDevolucao < :now")
    List<Emprestimo> findAllAtrasados(LocalDateTime now);
    
    @Query("SELECT COUNT(e) FROM Emprestimo e WHERE e.aluno.id = :alunoId AND e.dataDevolucao IS NULL")
    long countEmprestimosAtivos(Long alunoId);

    long countByLivroId(Long livroId);
} 