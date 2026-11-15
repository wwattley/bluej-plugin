package project.comp3607.plugin;

import project.comp3607.events.ErrorCategorizer;
import project.comp3607.events.ErrorCategoryEnum;

import project.comp3607.model.SessionTracker;
import project.comp3607.export.JsonExporter;
import project.comp3607.export.PdfExporter;
import javafx.scene.control.MenuItem;

import java.util.List;
import java.util.Arrays;

public class Main extends bluej.extensions2.Extension {
    private SessionTracker tracker;

    @Override
    public void startup(bluej.extensions2.BlueJ bluej) {
        this.tracker = new SessionTracker(
            List.of(
                new JsonExporter(),
                new PdfExporter()
            )
        );

        tracker.startSession();

        // Wiring Listeners for BlueJ events

        bluej.addPackageListener(new bluej.extensions2.event.PackageListener() {
            @Override public void packageOpened(bluej.extensions2.event.PackageEvent packageEvent) {
                try{
                    tracker.recordPackageOpened(packageEvent.getPackage().getName());
                } catch (Exception e) {
                    System.err.println("Error recording package opened event: " + e.getMessage());
                }
            }

            @Override public void packageClosing(bluej.extensions2.event.PackageEvent packageEvent) {
                try{
                    tracker.recordPackageClosed(packageEvent.getPackage().getName());
                } catch (Exception e) {
                    System.err.println("Error recording package closed event: " + e.getMessage());
                }
            }
        });

        bluej.addClassListener(new bluej.extensions2.event.ClassListener() {
            @Override public void classStateChanged(bluej.extensions2.event.ClassEvent event) { }
            @Override public void classNameChanged(bluej.extensions2.event.ClassEvent event) { }

            @Override
            public void classRemoved(bluej.extensions2.event.ClassEvent event) {
                try{
                    tracker.recordClassDeleted(event.getBClass().getJavaClass().getName());
                } catch (Exception e) {
                    System.err.println("Error recording class deleted event: " + e.getMessage());
                }
            }
        });

        bluej.addCompileListener(new bluej.extensions2.event.CompileListener() {
            @Override public void compileFailed(bluej.extensions2.event.CompileEvent compileEvent) {}
            @Override public void compileStarted(bluej.extensions2.event.CompileEvent compileEvent) {}
            @Override public void compileWarning(bluej.extensions2.event.CompileEvent compileEvent) {}

            @Override public void compileSucceeded(bluej.extensions2.event.CompileEvent compileEvent) {
                List<String> files = Arrays.stream(compileEvent.getFiles())
                    .map(file -> file.getName())
                    .toList();
                tracker.recordCompileSucceeded(files);
            }

            @Override public void compileError(bluej.extensions2.event.CompileEvent compileEvent) {
                String file = compileEvent.getFiles().length > 0 ? compileEvent.getFiles()[0].getName() : "Unknown";
                int line = compileEvent.getErrorLineNumber();
                String message = compileEvent.getErrorMessage();
                ErrorCategoryEnum category = new ErrorCategorizer().categorize(message);

                tracker.recordCompileError(file, line, message, category);
            }

        });





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
