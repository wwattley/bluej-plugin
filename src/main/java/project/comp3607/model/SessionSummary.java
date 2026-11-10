package project.comp3607.model;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;
import java.util.List;

import javafx.event.Event;
import project.comp3607.events.EventTypeEnum;

public class SessionSummary {
       private int totalCompilation;
    private int successfulCompilation;
    private int failedCompilation;
    private double compilationSuccessRate;
    private int totalRun;
    private int runtimeException;
    private double compileToRunRatio;
    private int activeSeconds;
    private int idleSeconds;
    private int classesCreated;
    private int classesDeleted;
    private Map<String, Integer>  errorFrequency;

    public static SessionReport.Summary compute(Session session) {
        List<Event> events = session.getEvents();

        int successfulCompiles = 0;
        int failedCompiles     = 0;
        int totalRuns          = 0;
        int runtimeExceptions  = 0;
        int classesCreated     = 0;
        int classesDeleted     = 0;

         Map<String, Integer> errorFrequency = new HashMap<>();

         for (Event e : events){
            if(e.eventType == EventTypeEnum.COMPILATION_SUCCESS) {
                successfulCompiles++;
            } else if (e.eventType == EventTypeEnum.COMPILATION_ERROR) {
                failedCompiles++;
                String errorMessage = e.details.get("error_message").toString();
                errorFrequency.put(errorMessage, errorFrequency.getOrDefault(errorMessage, 0) + 1);
            } else if (e.eventType == EventTypeEnum.RUNTIME_EXCEPTION) {
                runtimeExceptions++;
            } else if (e.eventType == EventTypeEnum.RUN_EXECUTION) {
                totalRuns++;
            } else if (e.eventType == EventTypeEnum.CLASS_CREATED) {
                classesCreated++;
            } else if (e.eventType == EventTypeEnum.CLASS_DELETED) {
                classesDeleted++;
            }

         }
    
    public SessionSummary(Session session) {

  
        eventCounts = new EnumMap<>(EventTypeEnum.class);

        // Go through all events and count their types.
        for (Event event : session.getEvents()) {

            EventTypeEnum type = event.getEventType();

            eventCounts.merge(type, 1, Integer::sum);
        }

        // Calculate the session duration.
        if (session.getStartTime() != null &&
            session.getEndTime() != null) {

            durationSeconds = Duration.between(
                session.getStartTime(),
                session.getEndTime()
            ).getSeconds();

        } else {
            durationSeconds = 0;
        }
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }


}
