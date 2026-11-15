package project.comp3607;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import project.comp3607.events.Event;
import project.comp3607.events.EventTypeEnum;
import project.comp3607.export.JsonExporter;
import project.comp3607.model.Session;
import project.comp3607.model.SessionSummary;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;


public class JsonExporterTest{

    private static final Path OUTPUT = Path.of("SESSION_ID_PLACEHOLDER.json");
    private static final Instant T0 = Instant.parse("2026-11-01T10:00:00Z");

    //like PDF exporter create a SessionSummary to test the JSON creation
    private static SessionSummary summary(){
        Session s = new Session();

        s.addEvent(new Event(1, EventTypeEnum.SESSION_START, Map.of(), T0));
        s.addEvent(new Event(2, EventTypeEnum.COMPILE_ERROR,
                Map.of("category", "CANNOT_FIND_SYMBOL"), T0.plusSeconds(20)));
        s.addEvent(new Event(3, EventTypeEnum.CLASS_CREATED,
                Map.of("class_name", "TestName"), T0.plusSeconds(125)));
        s.addEvent(new Event(4, EventTypeEnum.SESSION_END, Map.of(), T0.plusSeconds(600)));

        SessionSummary.compute(s);
    }
    @AfterEach //after each test method file is deleted
    public void clearFileInMemory()
        throws IOException{
            Files.delete(OUTPUT);
        }

    }

    @Test
    public void fileCreatedAsJson()
    throws IOException{
        new JsonExporter().export(summary());

        assertTrue(Files.exists(OUTPUT), "File was not created as JSON" + OUTPUT);
    }

