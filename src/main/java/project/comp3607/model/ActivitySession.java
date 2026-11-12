package project.comp3607.model;


import project.comp3607.events.Event;
import java.util.List;
import project.comp3607.events.EventTypeEnum;
import java.time.Duration;

public class ActivitySession {
    private long activeSeconds;
    private long idleSeconds;

    public ActivitySession(long activeSeconds, long idleSeconds) {
        this.activeSeconds = activeSeconds;
        this.idleSeconds = idleSeconds;
    }

    public static ActivitySession compute() {
        return new ActivitySession(0, 0);
        
    }
/* 
    private boolean isActivityEvent(Event event) {
        if (event.getEventType() == EventTypeEnum
        .CLASS_CREATED || event.getEventType() == EventTypeEnum.CLASS_DELETED
         || event.getEventType() == EventTypeEnum.COMPILE_SUCCEEDED
          || event.getEventType() == EventTypeEnum.COMPILE_ERROR) {
            return true;
        }


        return false;
    }

    private long eventDuration(Event event1) {


        // Logic to compute duration between two events
        // This is a placeholder; actual implementation would depend on event timestamps
        return 0;
    }
*/

}
