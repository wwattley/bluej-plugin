package project.comp3607.model;

import project.comp3607.events.Event;
import project.comp3607.events.EventFactory;
import project.comp3607.export.ExporterInterface;
import project.comp3607.events.ErrorCategoryEnum;

import java.util.List;

public class SessionTracker {
    private final EventFactory factory = new EventFactory();
    private final List<ExporterInterface> exporters;
    // private final SessionPrinter printer = new SessionPrinter();
    private Session current;

    public SessionTracker(List<ExporterInterface> exporters) {
        this.exporters = exporters;
    }

    public void startSession() {
        current = new Session();
        Event event = factory.createSessionStartEvent();
        current.addEvent(event);
    }

    public void endSession() {
        if (current != null) {
            Event event = factory.createSessionEndEvent();
            current.addEvent(event);

            SessionSummary summary = new SessionSummary(current);

            for (ExporterInterface exporter : exporters) {
                exporter.export(summary);
            }

            current = null;
        }
    }

    public void recordPackageOpened(String packageName) {
        Event event = factory.createPackageOpenEvent(packageName);
        add(event);
    }

    public void recordPackageClosed(String packageName){
        Event event = factory.createPackageCloseEvent(packageName);
        add(event);
    }

    public void recordClassCreated(String className) {
        Event event = factory.createClassCreatedEvent(className);
        add(event);
    }
     public void recordClassDeleted(String className) {
        Event event = factory.createClassDeletedEvent(className);
        add(event);
     }

     public void recordCompileSucceeded(List<String> files) {
        Event event = factory.createCompileSucceededEvent(files);
        add(event);}


    public void recordCompileError(String file, int line, String message, ErrorCategoryEnum category) {
        Event event = factory.createCompileErrorEvent(file, line, message, category);
        add(event);
    }

    private void add(Event event){
        if (current != null){
            current.addEvent(event);
        }
    }
}
