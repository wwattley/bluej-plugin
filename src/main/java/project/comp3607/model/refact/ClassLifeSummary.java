package project.comp3607.model.refact;

import project.comp3607.events.Event;
import project.comp3607.events.EventTypeEnum;

import java.util.List;

import project.comp3607.model.Session;

public class ClassLifeSummary {
    private int classesCreated;
    private int classesDeleted;

    public ClassLifeSummary(int classesCreated, int classesDeleted) {
        this.classesCreated = classesCreated;
        this.classesDeleted = classesDeleted;
    }

    public int getClassesCreated() { return classesCreated; }
    public int getClassesDeleted() { return classesDeleted; }

    public static ClassLifeSummary compute(Session session) {
        int classesCreated = 0;
        int classesDeleted = 0;
        List<Event> events = session.getEvents();

        for (Event event : events) {
            if (event.getEventType() == EventTypeEnum.CLASS_CREATED) {
                classesCreated++;
            } else if (event.getEventType() == EventTypeEnum.CLASS_DELETED) {
                classesDeleted++;
            }
        }

        return new ClassLifeSummary(classesCreated, classesDeleted);
    }

}
