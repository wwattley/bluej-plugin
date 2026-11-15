package project.comp3607;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import project.comp3607.events.ErrorCategoryEnum;
import project.comp3607.events.Event;
import project.comp3607.events.EventTypeEnum;
import project.comp3607.export.JsonExporter;
import project.comp3607.model.Session;
import project.comp3607.model.SessionSummary;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;


public class JsonExporterTest{

    private static final Path OUTPUT = Path.of("SESSION_ID_PLACEHOLDER.json");


    //like PDF exporter create a SessionSummary to test the JSON creation
   private static SessionSummary summary(Event ... events) {
        Session s = new Session();
        for (Event e : events) s.addEvent(e);
        return new SessionSummary(s);
    }
        //convert the OUTPUT PATH to a jsonObject
     private static JsonObject readOutput() throws IOException {
        try (Reader r = Files.newBufferedReader(OUTPUT)) {
            return JsonParser.parseReader(r).getAsJsonObject();
        }
    }
    @AfterEach //after each test method file is deleted
    public void clearFileInMemory()
        throws IOException{
            Files.delete(OUTPUT);
        }



    @Test
    public void fileCreatedAsJson()
    throws IOException{
        new JsonExporter().export(summary(

               new Event(1, EventTypeEnum.SESSION_START, Map.of())));


        assertTrue(Files.exists(OUTPUT), "File was not created as JSON" + OUTPUT);
    }




    @Test
    void rootHasSessionSummaryAndEventsBlocks() throws Exception {
        new JsonExporter().export(summary());
        JsonObject root = readOutput();
        assertTrue(root.has("session"));
        assertTrue(root.has("summary"));
        assertTrue(root.has("events"));
    }


  @Test
    void sessionBlockUsesIsoTimestampsAndDuration() throws Exception {
        new JsonExporter().export(summary(
                    new Event(1, EventTypeEnum.SESSION_START, Map.of()),
                new Event(2, EventTypeEnum.SESSION_END,   Map.of())));
        JsonObject s = readOutput().getAsJsonObject("session");
        assertEquals("SESSION_ID_PLACEHOLDER", s.get("session_id").getAsString());
        assertEquals("STU_ID_PLACEHOLDER",     s.get("student_id").getAsString());
        assertEquals("2026-11-01T10:00:00Z",   s.get("start_time").getAsString());
        assertEquals("2026-11-01T10:10:00Z",   s.get("end_time").getAsString());
        assertEquals(600L,                     s.get("duration_seconds").getAsLong());
    }

    @Test
    void summaryBlockCarriesCompilationActivityAndClassFields() throws Exception {
        new JsonExporter().export(summary(
                new Event(1, EventTypeEnum.SESSION_START, Map.of()),
                new Event(2, EventTypeEnum.COMPILE_SUCCEEDED,
                        Map.of("files", List.of("A.java", "B.java", "C.java"))),
                new Event(3, EventTypeEnum.COMPILE_ERROR,
                        Map.of("file", "A.java", "line", 1, "message", "x",
                               "category", ErrorCategoryEnum.SYMBOL)),
                new Event(4, EventTypeEnum.CLASS_CREATED,
                        Map.of("class_name", "A")),
                new Event(5, EventTypeEnum.SESSION_END, Map.of())));

        JsonObject sum = readOutput().getAsJsonObject("summary");
        assertEquals(2,    sum.get("total_compilations").getAsInt());
        assertEquals(1,    sum.get("successful_compilations").getAsInt());
        assertEquals(1,    sum.get("failed_compilations").getAsInt());
        assertEquals(50.0, sum.get("compile_success_rate").getAsDouble(), 1e-9);
        assertEquals(1,    sum.get("classes_created").getAsInt());
        assertEquals(0,    sum.get("classes_deleted").getAsInt());
        assertTrue(sum.has("active_seconds"));
        assertTrue(sum.has("idle_seconds"));
        assertEquals(1, sum.getAsJsonObject("error_frequency").get("SYM").getAsInt());
    }

    @Test
    void eventsArrayCarriesSeqTimestampAndShortTypeCode() throws Exception {
        new JsonExporter().export(summary(
                new Event(1, EventTypeEnum.SESSION_START, Map.of()),
                new Event(2, EventTypeEnum.COMPILE_ERROR,
                        Map.of("file", "A.java", "line", 3, "message", "x",
                               "category", ErrorCategoryEnum.SYMBOL))));

        JsonArray events = readOutput().getAsJsonArray("events");
        assertEquals(2, events.size());

        JsonObject first = events.get(0).getAsJsonObject();
        assertEquals(1,                       first.get("seq").getAsInt());
        assertEquals("2026-11-01T10:00:00Z", first.get("timestamp").getAsString());
        // NOTE: type is emitted as getCode(), not enum name(). Schema sample used "SESSION_START".
        assertEquals("SS",                    first.get("type").getAsString());

        JsonObject second = events.get(1).getAsJsonObject();
        assertEquals("CE", second.get("type").getAsString());
        // Gson serializes the ErrorCategoryEnum detail as its name() (SYMBOL), not its code (SYM).
        assertEquals("SYMBOL",
                second.getAsJsonObject("details").get("category").getAsString());
    }

    @Test
    void emptySessionProducesNullTimestampsAndZeroDuration() throws Exception {
        new JsonExporter().export(summary());
        JsonObject s = readOutput().getAsJsonObject("session");
        assertTrue(s.get("start_time").isJsonNull());
        assertTrue(s.get("end_time").isJsonNull());
        assertEquals(0L, s.get("duration_seconds").getAsLong());
        assertEquals(0, readOutput().getAsJsonArray("events").size());
    }



}


