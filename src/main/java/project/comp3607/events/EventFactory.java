package project.comp3607.events;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;

import java.util.List;

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


    public Event createPackageOpenEvent(String packageName) {
        return createEvent(EventTypeEnum.PACKAGE_OPEN, Map.of("package_name", packageName));
    }

    public Event createPackageCloseEvent(String packageName) {
        return createEvent(EventTypeEnum.PACKAGE_CLOSE, Map.of("package_name", packageName));
    }

    public Event createClassCreatedEvent(String className) {
        return createEvent(EventTypeEnum.CLASS_CREATED, Map.of("class_name", className));
    }

    public Event createClassDeletedEvent(String className) {
        return createEvent(EventTypeEnum.CLASS_DELETED, Map.of("class_name", className));
    }

    public Event createCompileSucceededEvent(List<String> files) {
        return createEvent(EventTypeEnum.COMPILE_SUCCEEDED, Map.of("files", files));
    }

    public Event createCompileErrorEvent(
        String file,
        int line,
        String message,
        ErrorCategoryEnum category
    ) {
        return createEvent(EventTypeEnum.COMPILE_ERROR, Map.of(
            "file", file,
            "line", line,
            "message", message,
            "category", category
        ));
    }

}
