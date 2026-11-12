package project.comp3607.export;

import project.comp3607.model.Session;
//only import what we need
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import project.comp3607.events.Event;
import project.comp3607.events.EventTypeEnum;
import project.comp3607.model.SessionSummary;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class PdfExporter implements ExporterInterface {
    //Objects to define the base of the PDF
    //all PDf have a Title, BODY, header and optional text MUTED
    private static final Font TITLE  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
    private static final Font H2     = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
    private static final Font BODY   = FontFactory.getFont(FontFactory.HELVETICA, 11);
    private static final Font MUTED  = FontFactory.getFont(FontFactory.HELVETICA, 10);

    @Override
    public String getDisplayName() { return "PDF"; }

    @Override

    public void export(SessionSummary summary){

         String filename = summary.getSession().getSessionId() + ".pdf";

            //exception handle when reading files
        try (FileOutputStream out = new FileOutputStream(filename)) {
            Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(doc, out);
            doc.open();

            addHeader(doc, session, summary);
            addActiveIdleChart(doc, summary);
            addErrorFrequencyChart(doc, summary);
            addProgressionTimeline(doc, session);

            doc.close();
            System.out.println("[PdfExporter] Session exported to " + filename);
        } catch (Exception e) {
            System.err.println("[PdfExporter] Error exporting session: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Define header parameters
     private void addHeader(Document doc, SessionSummary summary) throws Exception {
        Paragraph title = new Paragraph("BlueJ Session Report", TITLE);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);
        doc.add(Chunk.NEWLINE);

        PdfPTable meta = new PdfPTable(2);
        meta.setWidthPercentage(70);
        meta.setHorizontalAlignment(Element.ALIGN_LEFT);
        meta.setWidths(new float[]{1, 2});

        row(meta, "Student ID",  summary.getSession().getStudentId());
        row(meta, "Session ID",  summary.getSession().getSessionId());
        row(meta, "Started",     fmt(summary.getSession().getStartTime()));
        row(meta, "Ended",       fmt(summary.getSession().getEndTime()));
        //row(meta, "Duration",    (summary.getSession().getActiveSeconds() + summary.getSession().getIdleSeconds()) + " s");
        row(meta, "Compilations", summary.getCompilationSummary().getTotalCompilations()
                + "  (" + summary.successfulCompilations + " ok / "
                + summary.failedCompilations + " failed, "
                + String.format("%.0f%%", summary.compileSuccessRate * 100) + ")");

        doc.add(meta);
        doc.add(Chunk.NEWLINE);
    }




        //Graph and histogram summary
    private void addActiveIdleChart(Document doc, SessionSummary summary) throws Exception {
        doc.add(new Paragraph("Active vs Idle Time", H2));

        long active = summary.getSessionSummary().getActiveSeconds(), idle = summary.getSessionSummary().getIdleSeconds();
        if (active + idle == 0) {
            doc.add(new Paragraph("No timed events recorded.", MUTED));
            doc.add(Chunk.NEWLINE);
            return;
        }

        DefaultPieDataset<String> data = new DefaultPieDataset<>();
        data.setValue("Active (" + active + " s)", active);
        data.setValue("Idle ("   + idle   + " s)", idle);

        JFreeChart chart = ChartFactory.createPieChart(
                null, data, /*legend*/ true, /*tooltips*/ false, /*urls*/ false);
        embedChart(doc, chart);
    }

    private void addErrorFrequencyChart(Document doc, SessionSummary summary) throws Exception {
        doc.add(new Paragraph("Compilation Error Frequency", H2));

        if (sum.errorFrequency.isEmpty()) {
            doc.add(new Paragraph("No compilation errors recorded.", MUTED));
            doc.add(Chunk.NEWLINE);
            return;
        }

        DefaultCategoryDataset data = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> e : sum.errorFrequency.entrySet()) {
            data.addValue(e.getValue(), "Errors", e.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                null, "Category", "Count", data,
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                /*legend*/ false, /*tooltips*/ false, /*urls*/ false);
        embedChart(doc, chart);
    }

    private void addProgressionTimeline(Document doc, SessionSummary summary) throws Exception {
        doc.add(new Paragraph("Progression Timeline", H2));

        List<Event> progression = session.getSessionSummary().getEvents().stream()
                .filter(e -> switch (e.getEventType()) {
                    case SESSION_START, SESSION_END ->true; //CLASS_MODIFIED, METHOD_ADDED -> true;
                    default -> false;
                })
                .toList();

        if (progression.isEmpty()) {
            doc.add(new Paragraph("No class or method events recorded.", MUTED));
            return;
        }

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3, 4});

        header(table, "Seq");
        header(table, "Timestamp");
        header(table, "Event");

        for (Event e : progression) {
            table.addCell(new Phrase(String.valueOf(e.getSequence()), BODY));
            table.addCell(new Phrase(e.getTimestamp().toString(), BODY));
            table.addCell(new Phrase(describe(e), BODY));
        }
        doc.add(table);
    }


     private static void embedChart(Document doc, JFreeChart chart) throws Exception {
        BufferedImage img = chart.createBufferedImage(500, 300);
        com.lowagie.text.Image pdfImg = com.lowagie.text.Image.getInstance(img, null);
        pdfImg.scaleToFit(500, 300);
        doc.add(pdfImg);
        doc.add(Chunk.NEWLINE);
    }

    private static void row(PdfPTable t, String label, String value) {
        PdfPCell l = new PdfPCell(new Phrase(label, MUTED));
        PdfPCell v = new PdfPCell(new Phrase(value == null ? "" : value, BODY));
        l.setBorder(Rectangle.NO_BORDER);
        v.setBorder(Rectangle.NO_BORDER);
        t.addCell(l);
        t.addCell(v);
    }

    private static void header(PdfPTable t, String text) {
        PdfPCell c = new PdfPCell(new Phrase(text, H2));
        c.setHorizontalAlignment(Element.ALIGN_LEFT);
        t.addCell(c);
    }

    private static String describe(Event e) {
        Map<String, Object> d = e.getEventDetails();
        String name = String.valueOf(
                d.getOrDefault("class_name",
                d.getOrDefault("method_name", d.getOrDefault("name", ""))));
        return switch (e.getEventType()) {
            case SESSION_START -> "Session created" + name;
            case SESSION_END ->"Session end" + name;
            // case CLASS_CREATED  -> "class created: "     + name;
            // case CLASS_DELETED  -> "class deleted: "     + name;
            // case CLASS_MODIFIED -> "class modified: "    + name;
            // case METHOD_ADDED   -> "method added: "      + name;
            default             -> e.getEventType().name();
        };
    }

    private static String fmt(Instant t) { return t == null ? "(none)" : t.toString(); }


}
