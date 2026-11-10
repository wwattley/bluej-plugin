package project.comp3607.events;

import java.time.Instant;
import java.util.Map;

public interface EventInterface {
    int getSequence();
    Instant getTimestamp();
    EventTypeEnum getEventType();
    Map<String, Object> getEventDetails();
}
