package project.comp3607.model.refact;

import project.comp3607.events.Event;
import project.comp3607.events.EventFactory;
import project.comp3607.export.ExporterInterface;

import project.comp3607.model.Session;

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
}
