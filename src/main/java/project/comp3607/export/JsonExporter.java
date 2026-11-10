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
    public void export(Session session) {
        String filename = session.getSessionId() + ".json";
         JsonObject root = new JsonObject();
        //session summary not defined yet placeholer
          SessionSummary summary = SessionSummary.compute(session);

        root.addProperty("schema_version", "1.0");
        root.add("session", buildSession(session));
        root.add("summary", buildSummary(summary));
        root.add("events",  buildEvents(session));

        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(session, writer);
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
        o.addProperty("java_version", System.getProperty("java.version"));
        return o;
    }

    //build events attributes from the Session object
     private JsonArray buildEvents(Session s) {
        JsonArray arr = new JsonArray();
        for (Event e : s.getEvents()) {
            JsonObject o = new JsonObject();
            o.addProperty("seq",       e.getSequence());
            o.addProperty("timestamp", e.getTimestamp().toString());
            o.addProperty("type",      e.getEventType().name());
            o.add("details", gson.toJsonTree(e.getEventDetails()));
            arr.add(o);
        }
        return arr;
    }
    //method comes into play when a Session is defined according to the schema below
    //SessionSummary does not exist current but will be added by a different name or same name
    private JsonObject buildSummary(SessionSummary sum) {
        JsonObject o = new JsonObject();
        o.addProperty("total_compilations",      sum.totalCompilations);
        o.addProperty("successful_compilations", sum.successfulCompilations);
        o.addProperty("failed_compilations",     sum.failedCompilations);
        o.addProperty("compile_success_rate",    sum.compileSuccessRate);
        o.addProperty("total_runs",              sum.totalRuns);
        o.addProperty("runtime_exceptions",      sum.runtimeExceptions);
        o.addProperty("compile_to_run_ratio",    sum.compileToRunRatio);
        o.addProperty("active_seconds",          sum.activeSeconds);
        o.addProperty("idle_seconds",            sum.idleSeconds);
        o.addProperty("classes_created",         sum.classesCreated);
        o.addProperty("classes_deleted",         sum.classesDeleted);

        JsonObject freq = new JsonObject();
        sum.errorFrequency.forEach(freq::addProperty);
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
