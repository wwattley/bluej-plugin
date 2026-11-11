package project.comp3607.model;

import project.comp3607.events.Event;
import project.comp3607.events.EventTypeEnum;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.Duration;

public class Session {
    private final String sessionId;
    private final String studentId;
    private final List<Event> events;
    private Instant startTime;
    private Instant endTime;

    public Session() {
        this.studentId = "STU_ID_PLACEHOLDER";
        this.sessionId = "SESSION_ID_PLACEHOLDER";
        this.events = new ArrayList<>();
    }

    public void addEvent(Event event) {
        events.add(event);
        if (event.getEventType() == EventTypeEnum.SESSION_START) 
            startTime = event.getTimestamp();
        
        if (event.getEventType() == EventTypeEnum.SESSION_END) 
                   endTime = event.getTimestamp();
    }

     public synchronized long getSessionDurationSeconds() {

        if (startTime == null || endTime == null) {
            return 0;
        }

        return Duration.between(
                startTime, endTime
        ).getSeconds();
    }

    public String getSessionId() { return sessionId; }
    public String getStudentId() { return studentId; }
    public Instant getStartTime() { return startTime; }
    public Instant getEndTime() { return endTime; }
    public List<Event> getEvents() { return Collections.unmodifiableList(events); }

}
