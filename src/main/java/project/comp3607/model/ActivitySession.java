package project.comp3607.model;


import project.comp3607.events.Event;
import java.util.List;
import project.comp3607.events.EventTypeEnum;
import java.time.Duration;
import java.time.Instant;

public class ActivitySession {
    private long activeSeconds;
    private long idleSeconds;

    public ActivitySession(long activeSeconds, long idleSeconds) {
        this.activeSeconds = activeSeconds;
        this.idleSeconds = idleSeconds;
    }

    public long getActiveSeconds() { return activeSeconds; }
    public long getIdleSeconds() { return idleSeconds; }

    public static ActivitySession compute() {
        return new ActivitySession(0, 0);

    }

    private static boolean isActivityEvent(Event event) {
        EventTypeEnum eventType = event.getEventType();

        if (eventType == EventTypeEnum.PACKAGE_OPEN ||
            eventType == EventTypeEnum.PACKAGE_CLOSE ||
            eventType == EventTypeEnum.CLASS_CREATED ||
            eventType == EventTypeEnum.CLASS_DELETED ||
            eventType == EventTypeEnum.COMPILE_SUCCEEDED ||
            eventType == EventTypeEnum.COMPILE_ERROR)
        {
            return true;
        }

        return false;
    }

    public static ActivitySession compute(Session session) {
        List<Event> events = session.getEvents();

        if (events.isEmpty()) {
            return new ActivitySession(0, 0);
        }

        long activeSeconds = 0;

        for(int i = 0; i < events.size(); i++ ){
            Event curr = events.get(i);
            if (!isActivityEvent(curr)) continue;

            Instant nextActivityTime = null;
            for(int j = i + 1; j < events.size(); j++){
                if(isActivityEvent(events.get(j))){
                    nextActivityTime = events.get(j).getTimestamp();
                    break;
                }
            }

            Instant upperBound = nextActivityTime != null ? nextActivityTime : events.get(events.size() - 1).getTimestamp();

            long gapSeconds = Duration.between(curr.getTimestamp(), upperBound).getSeconds();
            activeSeconds += Math.min(gapSeconds, 60L);

        }

        long totalSeconds = Duration.between(events.get(0).getTimestamp(), events.get(events.size() - 1).getTimestamp()).getSeconds();

        long idleSeconds = Math.max(0, totalSeconds - activeSeconds);

        return new ActivitySession(activeSeconds, idleSeconds);

    }


}
