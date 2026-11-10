/*
    Deprecated: This class is no longer used.
    It was used to print session information to the console.
    Information about the session data is now exported to a JSON file instead.
*/


package project.comp3607.model;

import project.comp3607.events.Event;

import java.io.PrintStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class SessionPrinter {
    private final PrintStream out;

    public SessionPrinter() {
        this(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
    }
    public SessionPrinter(PrintStream out) { this.out = out; }

    public void print(Session session) {
        List<Event> events = session.getEvents();

        out.println("=== SESSION REPORT ===");
        out.println("Session ID : " + session.getSessionId());
        out.println("Student ID : " + session.getStudentId());
        out.println("Start      : " + fmt(session.getStartTime()));
        out.println("End        : " + fmt(session.getEndTime()));
        out.println("Duration   : " + duration(session));
        out.println();
        out.println("Events (" + events.size() + "):");
        for (Event e : events) {
            out.println("  " + e.getSequence() + ". "
                + "[" + e.getEventType().getCode() + "] "
                + fmt(e.getTimestamp()) + "  "
                + "details=" + details(e.getEventDetails()));
        }
        out.println("======================");
    }

    private static String fmt(Instant t) {
        return t == null ? "(none)" : t.toString();
    }

    private static String duration(Session s) {
        if (s.getStartTime() == null || s.getEndTime() == null) return "(unknown)";
        Duration d = Duration.between(s.getStartTime(), s.getEndTime());
        long secs = d.toSeconds();
        return secs + "s";
    }

    private static String details(Map<String, Object> details) {
        if (details.isEmpty()) return "{ }";
        StringBuilder sb = new StringBuilder("{ ");
        int n = 0;
        for (var entry : details.entrySet()) {
            if (n++ > 0) sb.append(", ");
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        sb.append(" }");
        return sb.toString();
    }
}
