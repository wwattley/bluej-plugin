package project.comp3607.export;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import project.comp3607.model.Session;
import java.io.FileWriter;
import java.io.IOException;

public class JsonExporter implements ExporterInterface {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void export(Session session) {
        String filename = session.getSessionId() + ".json";

        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(session, writer);
            System.out.println("[JsonExporter] Session exported to " + filename);
        } catch (IOException e) {
            System.err.println("[JsonExporter] Error exporting session: " + e.getMessage());
        }
    }

}
