package com.biblioteca.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.dto.EstatisticasDTO;
import com.biblioteca.repository.AlunoRepository;
import com.biblioteca.repository.EmprestimoRepository;
import com.biblioteca.repository.LivroRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EstatisticaService {

    private final LivroRepository livroRepository;
    private final AlunoRepository alunoRepository;
    private final EmprestimoRepository emprestimoRepository;

    @Transactional(readOnly = true)
    public EstatisticasDTO getEstatisticas() {
        EstatisticasDTO dto = new EstatisticasDTO();
        
        // Estatísticas de livros
        dto.setTotalLivros(livroRepository.count());
        dto.setLivrosDisponiveis(livroRepository.countByDisponivelTrue());
        dto.setLivrosEmprestados(dto.getTotalLivros() - dto.getLivrosDisponiveis());
        
        // Estatísticas de alunos
        dto.setTotalAlunos(alunoRepository.count());
        dto.setAlunosAtivos(alunoRepository.countByAtivoTrue());
        
        // Estatísticas de empréstimos
        LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        dto.setEmprestimosMes(emprestimoRepository.countByDataEmprestimoBetween(
            inicioMes, 
            LocalDateTime.now()
        ));
        
        dto.setEmprestimosAtrasados(emprestimoRepository.countByDataPrevistaDevolucaoBeforeAndDataDevolucaoIsNull(
            LocalDateTime.now()
        ));
        
        // Cálculo da taxa de devolução no prazo
        long totalDevolucoes = emprestimoRepository.countByDataDevolucaoIsNotNull();
        long devolucoesNoPrazo = emprestimoRepository.countByDataDevolucaoIsNotNullAndDataDevolucaoBeforeDataPrevistaDevolucao();
        
        dto.setTaxaDevolucaoNoPrazo(totalDevolucoes > 0 
            ? (double) devolucoesNoPrazo / totalDevolucoes * 100 
            : 0.0);
        
        return dto;
    }
} 