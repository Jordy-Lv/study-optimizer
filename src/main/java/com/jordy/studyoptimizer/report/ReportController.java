package com.jordy.studyoptimizer.report;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    /**
     * Descarga el reporte de progreso en PDF.
     *
     * Devolvemos ResponseEntity<byte[]> (no un String/record): el cuerpo son
     * bytes binarios, asi que fijamos a mano el Content-Type application/pdf y
     * el Content-Disposition "attachment" para que el navegador lo descargue
     * con nombre de archivo en vez de intentar mostrarlo como texto.
     */
    @GetMapping(value = "/progress", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> progress() {
        byte[] pdf = service.progressPdf();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"reporte-progreso.pdf\"")
                .contentLength(pdf.length)
                .body(pdf);
    }
}
