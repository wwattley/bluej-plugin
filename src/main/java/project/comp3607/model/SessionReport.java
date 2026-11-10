package project.comp3607.model;

import java.util.List;
import java.util.Map;

public class SessionReport {
    public SessionMeta session;
    public Summary summary;
    public List<EventDto> events;

public SessionReport(SessionMeta session, Summary summary, List<EventDto> events) {
        this.session = session;
        this.summary = summary;
        this.events  = events;
    }

public static class SessionMeta {
        public String sessionId;
        public String studentId;
        public String startTime;          // ISO-8601, e.g. 2026-11-01T10:00:00Z
        public String endTime;
        public long   durationSeconds;

        public SessionMeta(String sessionId, String studentId,
                           String startTime, String endTime, long durationSeconds) {
            this.sessionId        = sessionId;
            this.studentId        = studentId;
            this.startTime        = startTime;
            this.endTime          = endTime;
            this.durationSeconds  = durationSeconds;
        }
    }

       public static class Summary {
        public int    totalCompilations;
        public int    successfulCompilations;
        public int    failedCompilations;
        public double compileSuccessRate;
        public int    totalRuns;
        public int    runtimeExceptions;
        public double compileToRunRatio;
        public long   activeSeconds;
        public long   idleSeconds;
        public int    classesCreated;
        public int    classesDeleted;
        public Map<String, Integer> errorFrequency;

        public Summary() { /* Gson / builder */ }
    }
        public static class EventDto {
        public int    seq;
        public String timestamp;                  // ISO-8601
        public String type;                       // "SS", "CE", ...
        public Map<String, Object> details;       // already snake_case keys from EventFactory

        public EventDto(int seq, String timestamp, String type, Map<String, Object> details) {
            this.seq       = seq;
            this.timestamp = timestamp;
            this.type      = type;
            this.details   = details;
        }
    }
}

