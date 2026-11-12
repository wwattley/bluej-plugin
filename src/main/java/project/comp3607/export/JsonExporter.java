package project.comp3607.export;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import project.comp3607.events.Event;
import project.comp3607.model.Session;
import project.comp3607.model.SessionSummary;
import java.io.FileWriter;
import java.io.IOException;

import java.time.Duration;

public class JsonExporter implements ExporterInterface {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String getDisplayName() { return "JSON"; }

    @Override
    public void export(SessionSummary summary) {
        String filename = summary.getSession().getSessionId() + ".json";
        JsonObject root = new JsonObject();


        root.add("session", buildSession(summary.getSession()));
        root.add("summary", buildSummary(summary));
        root.add("events",  buildEvents(summary.getSession()));

        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(root, writer);
            System.out.println("[JsonExporter] Session exported to " + filename);
        } catch (IOException e) {
            System.err.println("[JsonExporter] Error exporting session: " + e.getMessage());
        }
    }

    //build Session attributes from the Session Object
    private JsonObject buildSession(Session s) {
        JsonObject o = new JsonObject();
        o.addProperty("session_id", s.getSessionId());
        o.addProperty("student_id", s.getStudentId());
        o.addProperty("start_time", s.getStartTime() == null ? null : s.getStartTime().toString());
        o.addProperty("end_time",   s.getEndTime()   == null ? null : s.getEndTime().toString());
        o.addProperty("duration_seconds", durationSeconds(s));

        return o;
    }

    //build events attributes from the Session object
    private JsonArray buildEvents(Session s) {
        JsonArray arr = new JsonArray();
        for (Event e : s.getEvents()) {
            JsonObject o = new JsonObject();
            o.addProperty("seq",       e.getSequence());
            o.addProperty("timestamp", e.getTimestamp().toString());
            o.addProperty("type",      e.getEventType().getCode());
            o.add("details", gson.toJsonTree(e.getEventDetails()));
            arr.add(o);
        }
        return arr;
    }

    //method comes into play when a Session is defined according to the schema below
    private JsonObject buildSummary(SessionSummary summary) {
        JsonObject o = new JsonObject();
        o.addProperty("total_compilations",      summary.getCompilationSummary().getTotalCompilations());
        o.addProperty("successful_compilations", summary.getCompilationSummary().getSuccessfulCompilations());
        o.addProperty("failed_compilations",     summary.getCompilationSummary().getFailedCompilations());
        o.addProperty("compile_success_rate",    summary.getCompilationSummary().getCompilationSuccessRate());
        // o.addProperty("active_seconds",          summary.ActiveSeconds());
        // o.addProperty("idle_seconds",            summary.idleSeconds());
        o.addProperty("classes_created",       summary.getClassLifeSummary().getClassesCreated());
        o.addProperty("classes_deleted",      summary.getClassLifeSummary().getClassesDeleted());

        JsonObject freq = new JsonObject();
        summary.getCompilationSummary().getErrorFrequency().forEach(freq::addProperty);
        o.add("error_frequency", freq);
        return o;
    }

    private static long durationSeconds(Session s) {
        if (s.getStartTime() == null) return 0;
        Duration d = Duration.between(s.getStartTime(),
                                      s.getEndTime() == null ? s.getStartTime() : s.getEndTime());
        return d.getSeconds();
    }

}
