package com.biblioteca.service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import com.biblioteca.model.Emprestimo;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.EmprestimoRepository;
import com.biblioteca.repository.LivroRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.opencsv.CSVWriter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final EmprestimoRepository emprestimoRepository;
    private final LivroRepository livroRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] gerarRelatorioEmprestimosAtrasadosPDF() throws Exception {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Configurações iniciais
            float margin = 50;
            float yStart = page.getMediaBox().getHeight() - margin;
            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
            float yPosition = yStart;
            float rowHeight = 20;
            int rowsPerPage = 25;
            float[] columnWidths = {tableWidth * 0.25f, tableWidth * 0.25f, tableWidth * 0.2f, tableWidth * 0.2f, tableWidth * 0.1f};

            // Título
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Relatório de Empréstimos Atrasados");
            contentStream.endText();
            yPosition -= rowHeight * 2;

            // Data de geração
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Gerado em: " + LocalDateTime.now().format(FORMATTER));
            contentStream.endText();
            yPosition -= rowHeight * 2;

            // Cabeçalho da tabela
            String[] headers = {"Aluno", "Livro", "Data Empréstimo", "Data Prevista", "Dias Atraso"};
            float xPosition = margin;
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

            for (int i = 0; i < headers.length; i++) {
                contentStream.beginText();
                contentStream.newLineAtOffset(xPosition, yPosition);
                contentStream.showText(headers[i]);
                contentStream.endText();
                xPosition += columnWidths[i];
            }
            yPosition -= rowHeight;

            // Dados da tabela
            List<Emprestimo> emprestimosAtrasados = emprestimoRepository.findAllAtrasados(LocalDateTime.now());
            contentStream.setFont(PDType1Font.HELVETICA, 12);

            for (Emprestimo emprestimo : emprestimosAtrasados) {
                if (yPosition <= margin + rowHeight) {
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    yPosition = yStart - 4 * rowHeight;
                }

                xPosition = margin;
                contentStream.beginText();
                contentStream.newLineAtOffset(xPosition, yPosition);
                contentStream.showText(emprestimo.getAluno().getNome());
                contentStream.endText();
                xPosition += columnWidths[0];

                contentStream.beginText();
                contentStream.newLineAtOffset(xPosition, yPosition);
                contentStream.showText(emprestimo.getLivro().getTitulo());
                contentStream.endText();
                xPosition += columnWidths[1];

                contentStream.beginText();
                contentStream.newLineAtOffset(xPosition, yPosition);
                contentStream.showText(emprestimo.getDataEmprestimo().format(FORMATTER));
                contentStream.endText();
                xPosition += columnWidths[2];

                contentStream.beginText();
                contentStream.newLineAtOffset(xPosition, yPosition);
                contentStream.showText(emprestimo.getDataPrevistaDevolucao().format(FORMATTER));
                contentStream.endText();
                xPosition += columnWidths[3];

                long diasAtraso = java.time.temporal.ChronoUnit.DAYS.between(
                    emprestimo.getDataPrevistaDevolucao(), 
                    LocalDateTime.now()
                );

                contentStream.beginText();
                contentStream.newLineAtOffset(xPosition, yPosition);
                contentStream.showText(String.valueOf(diasAtraso));
                contentStream.endText();

                yPosition -= rowHeight;
            }

            contentStream.close();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    public String gerarRelatorioLivrosMaisEmprestadosCSV() throws Exception {
        List<Livro> livros = livroRepository.findAll();
        Map<Livro, Long> emprestimosPorLivro = livros.stream()
                .collect(Collectors.toMap(
                    livro -> livro,
                    livro -> emprestimoRepository.countByLivroId(livro.getId())
                ));

        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);

        try {
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

            csvWriter.flush();
            return stringWriter.toString();
        } finally {
            csvWriter.close();
            stringWriter.close();
        }
    }

    public byte[] gerarRelatorioEstoquePDF() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        try {
            // Título
            document.add(new Paragraph("Relatório de Estoque de Livros")
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER));
            
            // Data de geração
            document.add(new Paragraph("Gerado em: " + LocalDateTime.now().format(FORMATTER)));

            // Tabela
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 3, 2, 1, 2}));
            
            // Cabeçalho da tabela
            table.addCell(new Cell().add(new Paragraph("Título")));
            table.addCell(new Cell().add(new Paragraph("Autor")));
            table.addCell(new Cell().add(new Paragraph("ISBN")));
            table.addCell(new Cell().add(new Paragraph("Quantidade")));
            table.addCell(new Cell().add(new Paragraph("Status")));

            // Dados da tabela
            List<Livro> livros = livroRepository.findAll();
            
            for (Livro livro : livros) {
                table.addCell(new Cell().add(new Paragraph(livro.getTitulo())));
                table.addCell(new Cell().add(new Paragraph(livro.getAutor())));
                table.addCell(new Cell().add(new Paragraph(livro.getIsbn())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(livro.getQuantidadeEstoque()))));
                table.addCell(new Cell().add(new Paragraph(livro.isDisponivel() ? "Disponível" : "Indisponível")));
            }

            document.add(table);
            return baos.toByteArray();
        } finally {
            document.close();
            pdf.close();
            writer.close();
        }
    }
} 