package com.biblioteca.repository;

import com.biblioteca.model.Reserva;
import com.biblioteca.model.StatusReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    List<Reserva> findByLivroIdAndStatusOrderByDataReservaAsc(Long livroId, StatusReserva status);
    
    List<Reserva> findByAlunoIdAndStatusIn(Long alunoId, List<StatusReserva> status);
    
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.aluno.id = :alunoId AND r.status IN :status")
    long countByAlunoIdAndStatusIn(@Param("alunoId") Long alunoId, @Param("status") List<StatusReserva> status);
    
    List<Reserva> findByStatusAndDataValidadeBefore(StatusReserva status, LocalDateTime data);
    
    List<Reserva> findByStatusAndNotificadoFalse(StatusReserva status);
    
    @Query("SELECT r FROM Reserva r WHERE r.livro.id = :livroId AND r.status = :status ORDER BY r.dataReserva ASC")
    List<Reserva> findProximaReserva(@Param("livroId") Long livroId, @Param("status") StatusReserva status);
} 