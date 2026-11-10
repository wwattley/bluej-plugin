package project.comp3607.events;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;

public class EventFactory {
    private final AtomicInteger sequenceGenerator = new AtomicInteger(0);

    private Event createEvent(EventTypeEnum type, Map<String, Object> details) {
        int nextSequence = sequenceGenerator.incrementAndGet();

        return new Event(
            nextSequence,
            type,
            details
        );
    }

    public Event createSessionStartEvent() { return createEvent(EventTypeEnum.SESSION_START, Map.of()); }
    public Event createSessionEndEvent() { return createEvent(EventTypeEnum.SESSION_END, Map.of()); }


    // Extend this class with more event creation methods when needed
    public Event createPackageOpenEvent(String packageName) {
        return createEvent(EventTypeEnum.PACKAGE_OPEN, Map.of("package_name", packageName));
    }

    public Event createPackageCloseEvent(String packageName) {
        return createEvent(EventTypeEnum.PACKAGE_CLOSE, Map.of("package_name", packageName));
    }

}
