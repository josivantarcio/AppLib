package com.biblioteca.repository;

import com.biblioteca.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    Optional<Aluno> findByMatricula(String matricula);
    Optional<Aluno> findByEmail(String email);
    boolean existsByMatricula(String matricula);
    boolean existsByEmail(String email);
} 