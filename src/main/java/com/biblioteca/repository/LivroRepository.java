package com.biblioteca.repository;

import com.biblioteca.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    Optional<Livro> findByIsbn(String isbn);
    
    boolean existsByIsbn(String isbn);
    
    List<Livro> findByTituloContainingIgnoreCase(String titulo);
    
    @Query("SELECT l FROM Livro l WHERE l.quantidadeEstoque > 0 AND l.disponivel = true")
    List<Livro> findAllDisponiveis();

    long countByDisponivelTrue();
} 