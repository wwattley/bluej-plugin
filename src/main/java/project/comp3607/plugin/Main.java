package project.comp3607.plugin;

import project.comp3607.model.SessionTracker;
import project.comp3607.export.JsonExporter;
import javafx.scene.control.MenuItem;

import java.util.List;

public class Main extends bluej.extensions2.Extension {
    private SessionTracker tracker;

    @Override
    public void startup(bluej.extensions2.BlueJ bluej) {
        this.tracker = new SessionTracker(
            List.of(
                new JsonExporter()
            )
        );

        tracker.startSession();
        bluej.setMenuGenerator(new MenuBuilder(tracker));
        // This method is called when the extension is loaded.
        // You can add your extension initialization code here.
    }

    @Override
    public boolean isCompatible() { return true; }

    @Override
    public String getVersion() { return "1.0"; }

    @Override
    public String getName() { return "Session Telemetry Tracker Extension"; }

    @Override
    public String getDescription() { return "This extension tracks user sessions and exports telemetry data."; }

    private static class MenuBuilder extends bluej.extensions2.MenuGenerator {
        private final SessionTracker tracker;

        public MenuBuilder(SessionTracker tracker) {
            this.tracker = tracker;
        }

        @Override
        public MenuItem getToolsMenuItem(bluej.extensions2.BPackage bPackage) {
            MenuItem item = new MenuItem("End Session");
            item.setOnAction(e -> tracker.endSession());
            return item;
        }
    }


}
