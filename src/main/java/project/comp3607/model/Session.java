package project.comp3607.model;

import project.comp3607.events.Event;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Session {
    private final String sessionId;
    private final String studentId;
    private Instant startTime;
    private Instant endTime;

    private final List<Event> events = new ArrayList<>();

    public Session() {
        this.studentId = "STU_ID_PLACEHOLDER";
        this.sessionId = "SESSION_ID_PLACEHOLDER";
    }

    public void addEvent(Event event) {
        events.add(event);
        if (startTime == null) startTime = event.getTimestamp();
        endTime = event.getTimestamp();
    }

    public String getSessionId() { return sessionId; }
    public String getStudentId() { return studentId; }
    public Instant getStartTime() { return startTime; }
    public Instant getEndTime() { return endTime; }
    public List<Event> getEvents() { return Collections.unmodifiableList(events); }

}
