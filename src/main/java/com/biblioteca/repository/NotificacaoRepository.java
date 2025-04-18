package com.biblioteca.repository;

import com.biblioteca.model.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    
    List<Notificacao> findByAlunoIdAndLidaFalseOrderByDataCriacaoDesc(Long alunoId);
    
    List<Notificacao> findByEmailEnviadoFalseAndDataCriacaoBefore(LocalDateTime data);
    
    @Modifying
    @Query("UPDATE Notificacao n SET n.lida = true, n.dataLeitura = :dataLeitura WHERE n.id = :id")
    void marcarComoLida(@Param("id") Long id, @Param("dataLeitura") LocalDateTime dataLeitura);
    
    @Modifying
    @Query("UPDATE Notificacao n SET n.emailEnviado = true, n.dataEnvioEmail = :dataEnvio WHERE n.id = :id")
    void marcarEmailComoEnviado(@Param("id") Long id, @Param("dataEnvio") LocalDateTime dataEnvio);
} 