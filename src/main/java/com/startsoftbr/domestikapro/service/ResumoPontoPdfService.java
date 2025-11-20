package com.startsoftbr.domestikapro.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.startsoftbr.domestikapro.dto.RegistroPontoResponse;
import com.startsoftbr.domestikapro.dto.ResumoPontoResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResumoPontoPdfService {

    private final RegistroPontoService registroPontoService;

    public byte[] gerar(Long funcionarioId) throws Exception {

        ResumoPontoResponse resumo = registroPontoService.resumo(funcionarioId);

        Document doc = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, baos);

        doc.open();

        Font titulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font normal = new Font(Font.FontFamily.HELVETICA, 12);

        doc.add(new Paragraph("Relatório Diário de Ponto", titulo));
        doc.add(new Paragraph("Data: " +
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normal));
        doc.add(new Paragraph("Funcionária: " + resumo.getNome(), normal));
        doc.add(new Paragraph(" ", normal));

        doc.add(new Paragraph("Primeira Entrada: " + resumo.getPrimeiraEntrada(), normal));
        doc.add(new Paragraph("Última Saída: " + resumo.getUltimaSaida(), normal));
        doc.add(new Paragraph("Total Trabalhado: " + resumo.getTotalTrabalhado(), normal));
        doc.add(new Paragraph("Total Pausa: " + resumo.getTotalPausa(), normal));
        doc.add(new Paragraph(" ", normal));

        PdfPTable tabela = new PdfPTable(2);
        tabela.setWidthPercentage(100);

        PdfPCell c1 = new PdfPCell(new Phrase("Hora"));
        PdfPCell c2 = new PdfPCell(new Phrase("Tipo"));

        tabela.addCell(c1);
        tabela.addCell(c2);

        for (RegistroPontoResponse r : resumo.getRegistros()) {
        	String hora = r.getDataHora().toLocalTime()
        	        .format(DateTimeFormatter.ofPattern("HH:mm"));
            tabela.addCell(hora);
            tabela.addCell(r.getTipo());
        }

        doc.add(tabela);

        doc.close();
        return baos.toByteArray();
    }
}
