package com.jordy.studyoptimizer.report;

import com.jordy.studyoptimizer.achievement.dto.AchievementResponse;
import com.jordy.studyoptimizer.analytics.dto.ExerciseStatsResponse;
import com.jordy.studyoptimizer.analytics.dto.SummaryResponse;
import com.jordy.studyoptimizer.goal.GoalMetric;
import com.jordy.studyoptimizer.goal.dto.GoalResponse;
import com.jordy.studyoptimizer.streak.dto.StreakResponse;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Convierte un {@link ProgressReportData} en los bytes de un PDF.
 *
 * Es el UNICO punto del codigo que importa OpenPDF (com.lowagie.*): si manana
 * cambiamos de libreria, solo se toca esta clase. El resto del backend habla
 * en byte[]. El PDF se arma en memoria (ByteArrayOutputStream): para un reporte
 * pequeno no hace falta escribir a disco.
 */
@Component
public class ProgressPdfBuilder {

    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Paleta sobria (mismos azules que un dashboard tipico).
    private static final Color INK = new Color(33, 37, 41);
    private static final Color MUTED = new Color(108, 117, 125);
    private static final Color ACCENT = new Color(13, 110, 253);
    private static final Color HEADER_BG = new Color(13, 110, 253);
    private static final Color ROW_ALT = new Color(243, 246, 250);

    private static final Font TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, ACCENT);
    private static final Font SUBTITLE = FontFactory.getFont(FontFactory.HELVETICA, 10, MUTED);
    private static final Font H2 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, INK);
    private static final Font LABEL = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10.5f, INK);
    private static final Font VALUE = FontFactory.getFont(FontFactory.HELVETICA, 10.5f, INK);
    private static final Font TH = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
    private static final Font TD = FontFactory.getFont(FontFactory.HELVETICA, 10, INK);
    private static final Font NOTE = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9.5f, MUTED);

    public byte[] build(ProgressReportData data) {
        Document document = new Document(PageSize.A4, 48, 48, 54, 48);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            addHeader(document, data);
            addSummary(document, data.summary());
            addExerciseProgress(document, data);
            addStreak(document, data.streak());
            addGoals(document, data.goals());
            addAchievements(document, data.achievements());
            addTopExercises(document, data.topExercises());

            document.close();
        } catch (DocumentException e) {
            throw new ReportGenerationException("No se pudo construir el PDF de progreso", e);
        }
        return out.toByteArray();
    }

    // --- secciones ---

    private void addHeader(Document doc, ProgressReportData data) throws DocumentException {
        doc.add(new Paragraph("Reporte de Progreso", TITLE));
        Paragraph sub = new Paragraph("Study Optimizer  |  generado el " + DAY.format(data.generatedOn()), SUBTITLE);
        sub.setSpacingAfter(4f);
        doc.add(sub);
    }

    private void addSummary(Document doc, SummaryResponse s) throws DocumentException {
        doc.add(section("Resumen global"));
        doc.add(kv("Horas estudiadas", oneDecimal(s.totalHours()) + " h  (" + s.totalMinutes() + " min)"));
        doc.add(kv("Sesiones", String.valueOf(s.totalSessions())));
        doc.add(kv("Dias distintos estudiados", String.valueOf(s.distinctDays())));
        doc.add(kv("Pomodoros (25 min)", String.valueOf(s.pomodoros())));
        doc.add(kv("Dificultad media", oneDecimal(s.avgDifficulty()) + " / 5"));
    }

    private void addExerciseProgress(Document doc, ProgressReportData data) throws DocumentException {
        doc.add(section("Progreso de retos"));
        long total = data.totalExercises();
        long done = data.completedExercises();
        int percent = total == 0 ? 0 : (int) Math.round(done * 100.0 / total);
        doc.add(kv("Retos completados", done + " / " + total + "  (" + percent + "%)"));

        List<Integer> phases = data.completedPhases();
        String phasesText = phases.isEmpty()
                ? "ninguna todavia"
                : phases.stream().map(String::valueOf).reduce((a, b) -> a + ", " + b).orElse("");
        doc.add(kv("Fases completas", phasesText));
    }

    private void addStreak(Document doc, StreakResponse st) throws DocumentException {
        doc.add(section("Rachas"));
        doc.add(kv("Racha actual", st.currentStreak() + " dia(s)"));
        doc.add(kv("Racha mas larga", st.longestStreak() + " dia(s)"));
        doc.add(kv("Dias totales estudiados", String.valueOf(st.totalStudyDays())));
        doc.add(kv("Ultimo dia de estudio",
                st.lastStudyDate() == null ? "sin datos" : DAY.format(st.lastStudyDate())));
        doc.add(kv("Estudiaste hoy", st.studiedToday() ? "Si" : "No"));
    }

    private void addGoals(Document doc, List<GoalResponse> goals) throws DocumentException {
        doc.add(section("Metas semanales"));
        if (goals.isEmpty()) {
            doc.add(note("Aun no hay metas configuradas."));
            return;
        }
        PdfPTable table = newTable(new float[]{4, 2, 2, 1.4f, 2});
        headerRow(table, "Metrica", "Objetivo", "Actual", "%", "Estado");
        int i = 0;
        for (GoalResponse g : goals) {
            boolean alt = (i++ % 2) == 1;
            bodyCell(table, metricLabel(g.metric()), Element.ALIGN_LEFT, alt);
            bodyCell(table, String.valueOf(g.target()), Element.ALIGN_CENTER, alt);
            bodyCell(table, String.valueOf(g.current()), Element.ALIGN_CENTER, alt);
            bodyCell(table, g.percent() + "%", Element.ALIGN_CENTER, alt);
            bodyCell(table, g.achieved() ? "Lograda" : "En curso", Element.ALIGN_CENTER, alt);
        }
        doc.add(table);
    }

    private void addAchievements(Document doc, List<AchievementResponse> achievements) throws DocumentException {
        long unlocked = achievements.stream().filter(AchievementResponse::unlocked).count();
        doc.add(section("Logros"));
        doc.add(kv("Desbloqueados", unlocked + " / " + achievements.size()));

        if (achievements.isEmpty()) {
            return;
        }
        PdfPTable table = newTable(new float[]{5, 3});
        headerRow(table, "Logro", "Estado");
        int i = 0;
        for (AchievementResponse a : achievements) {
            boolean alt = (i++ % 2) == 1;
            bodyCell(table, a.title(), Element.ALIGN_LEFT, alt);
            String estado = a.unlocked()
                    ? "Desbloqueado " + formatDateTime(a.unlockedAt())
                    : "Pendiente";
            bodyCell(table, estado, Element.ALIGN_LEFT, alt);
        }
        doc.add(table);
    }

    private void addTopExercises(Document doc, List<ExerciseStatsResponse> top) throws DocumentException {
        doc.add(section("Retos con mas tiempo invertido"));
        if (top.isEmpty()) {
            doc.add(note("Todavia no hay tiempo registrado por reto."));
            return;
        }
        PdfPTable table = newTable(new float[]{1.2f, 5, 1.8f, 1.6f, 1.8f});
        headerRow(table, "Dia", "Reto", "Horas", "Sesiones", "Pomodoros");
        int i = 0;
        for (ExerciseStatsResponse e : top) {
            boolean alt = (i++ % 2) == 1;
            bodyCell(table, String.valueOf(e.dayNumber()), Element.ALIGN_CENTER, alt);
            bodyCell(table, e.title(), Element.ALIGN_LEFT, alt);
            bodyCell(table, oneDecimal(e.totalHours()), Element.ALIGN_CENTER, alt);
            bodyCell(table, String.valueOf(e.sessions()), Element.ALIGN_CENTER, alt);
            bodyCell(table, String.valueOf(e.pomodoros()), Element.ALIGN_CENTER, alt);
        }
        doc.add(table);
    }

    // --- helpers de construccion ---

    /** Encabezado de seccion con espacio arriba para separar bloques. */
    private static Paragraph section(String text) {
        Paragraph p = new Paragraph(text, H2);
        p.setSpacingBefore(16f);
        p.setSpacingAfter(6f);
        return p;
    }

    /** Linea "Etiqueta: valor" (etiqueta en negrita, valor normal). */
    private static Paragraph kv(String label, String value) {
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + ": ", LABEL));
        p.add(new Chunk(value, VALUE));
        p.setSpacingAfter(2.5f);
        return p;
    }

    private static Paragraph note(String text) {
        Paragraph p = new Paragraph(text, NOTE);
        p.setSpacingAfter(2f);
        return p;
    }

    private static PdfPTable newTable(float[] widths) throws DocumentException {
        PdfPTable table = new PdfPTable(widths.length);
        table.setWidthPercentage(100);
        table.setWidths(widths);
        table.setSpacingBefore(4f);
        return table;
    }

    private static void headerRow(PdfPTable table, String... titles) {
        for (String t : titles) {
            PdfPCell cell = new PdfPCell(new Phrase(t, TH));
            cell.setBackgroundColor(HEADER_BG);
            cell.setBorderColor(Color.WHITE);
            cell.setPadding(5f);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private static void bodyCell(PdfPTable table, String text, int align, boolean alt) {
        PdfPCell cell = new PdfPCell(new Phrase(text, TD));
        cell.setPadding(4.5f);
        cell.setHorizontalAlignment(align);
        cell.setBorderColor(new Color(222, 226, 230));
        if (alt) {
            cell.setBackgroundColor(ROW_ALT);
        }
        table.addCell(cell);
    }

    // --- helpers de formato ---

    private static String oneDecimal(double v) {
        return String.format(Locale.ROOT, "%.1f", v);
    }

    private static String formatDateTime(LocalDateTime dt) {
        return dt == null ? "" : DAY.format(dt.toLocalDate());
    }

    private static String metricLabel(GoalMetric metric) {
        return switch (metric) {
            case EXERCISES_COMPLETED -> "Retos completados";
            case STUDY_MINUTES -> "Minutos de estudio";
            case SESSIONS -> "Sesiones";
        };
    }
}
