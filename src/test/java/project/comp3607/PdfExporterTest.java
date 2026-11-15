package project.comp3607;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import project.comp3607.events.Event;
import project.comp3607.events.EventTypeEnum;
import project.comp3607.export.PdfExporter;
import project.comp3607.model.Session;
import project.comp3607.model.SessionSummary;


import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Map;


public class PdfExporterTest {
     private static final Path OUTPUT = Path.of("SESSION_ID_PLACEHOLDER.pdf");//test pdf


    //internal method to build a testSummary for Exporter to Test
    //returns a Sessionsummary so the .export method can export it as a PDF
    private static SessionSummary summary() {
        Session s = new Session();
        SessionSummary summary = new SessionSummary(s);

        s.addEvent(new Event(1, EventTypeEnum.SESSION_START, Map.of()));
        s.addEvent(new Event(2, EventTypeEnum.COMPILE_ERROR,
                Map.of("category", "CANNOT_FIND_SYMBOL")));
        s.addEvent(new Event(3, EventTypeEnum.CLASS_CREATED,
                Map.of("class_name", "TestName")));
        s.addEvent(new Event(4, EventTypeEnum.SESSION_END, Map.of()));
        return summary;
    }

     @AfterEach
    void cleanup()
    throws Exception{
        Files.deleteIfExists(OUTPUT);
    }

    @Test
    void fileCreatedAsPdf() throws Exception{
        new PdfExporter().export(summary());

        assertTrue(Files.exists(OUTPUT), "PDF file not created" + OUTPUT);
    }

    @Test void PdfOutputSize() throws Exception{

        new PdfExporter().export(summary());

        assertTrue(Files.size(OUTPUT) > 1200,
        "PDF size is a little too small, increase the size!"+ Files.size(OUTPUT));
    }
}
