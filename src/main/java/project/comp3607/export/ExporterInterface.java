package project.comp3607.export;

import project.comp3607.model.Session;

public interface ExporterInterface {
    /**
    * Exports the given session.
    * @param session The session to be exported.
    * @param sessionId the unique identifier used for the filenames.
    */

    void export(Session session);

    //returns a PDF OR JSON
    String getDisplayName();
}
