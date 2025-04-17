package com.biblioteca.service;

import com.biblioteca.model.Emprestimo;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.EmprestimoRepository;
import com.biblioteca.repository.LivroRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final EmprestimoRepository emprestimoRepository;
    private final LivroRepository livroRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] gerarRelatorioEmprestimosAtrasadosPDF() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Relatório de Empréstimos Atrasados")
                .setFontSize(16)
                .setBold());
        document.add(new Paragraph("Gerado em: " + LocalDateTime.now().format(FORMATTER)));

        Table table = new Table(5);
        table.addCell("Aluno");
        table.addCell("Livro");
        table.addCell("Data Empréstimo");
        table.addCell("Data Prevista");
        table.addCell("Dias Atraso");

        List<Emprestimo> emprestimosAtrasados = emprestimoRepository.findAllAtrasados(LocalDateTime.now());
        
        for (Emprestimo emprestimo : emprestimosAtrasados) {
            table.addCell(emprestimo.getAluno().getNome());
            table.addCell(emprestimo.getLivro().getTitulo());
            table.addCell(emprestimo.getDataEmprestimo().format(FORMATTER));
            table.addCell(emprestimo.getDataPrevistaDevolucao().format(FORMATTER));
            long diasAtraso = java.time.temporal.ChronoUnit.DAYS.between(
                emprestimo.getDataPrevistaDevolucao(), 
                LocalDateTime.now()
            );
            table.addCell(String.valueOf(diasAtraso));
        }

        document.add(table);
        document.close();

        return baos.toByteArray();
    }

    public String gerarRelatorioLivrosMaisEmprestadosCSV() {
        List<Livro> livros = livroRepository.findAll();
        Map<Livro, Long> emprestimosPorLivro = livros.stream()
                .collect(Collectors.toMap(
                    livro -> livro,
                    livro -> emprestimoRepository.countByLivroId(livro.getId())
                ));

        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);

        // Cabeçalho
        csvWriter.writeNext(new String[]{"Título", "Autor", "ISBN", "Quantidade de Empréstimos"});

        // Dados
        emprestimosPorLivro.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach(entry -> {
                    Livro livro = entry.getKey();
                    csvWriter.writeNext(new String[]{
                        livro.getTitulo(),
                        livro.getAutor(),
                        livro.getIsbn(),
                        String.valueOf(entry.getValue())
                    });
                });

        return stringWriter.toString();
    }

    public byte[] gerarRelatorioEstoquePDF() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Relatório de Estoque de Livros")
                .setFontSize(16)
                .setBold());
        document.add(new Paragraph("Gerado em: " + LocalDateTime.now().format(FORMATTER)));

        Table table = new Table(5);
        table.addCell("Título");
        table.addCell("Autor");
        table.addCell("ISBN");
        table.addCell("Quantidade");
        table.addCell("Status");

        List<Livro> livros = livroRepository.findAll();
        
        for (Livro livro : livros) {
            table.addCell(livro.getTitulo());
            table.addCell(livro.getAutor());
            table.addCell(livro.getIsbn());
            table.addCell(String.valueOf(livro.getQuantidadeEstoque()));
            table.addCell(livro.isDisponivel() ? "Disponível" : "Indisponível");
        }

        document.add(table);
        document.close();

        return baos.toByteArray();
    }
} 