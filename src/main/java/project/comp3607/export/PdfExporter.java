package project.comp3607.export;

import project.comp3607.model.Session;

import com.lowagie.text.Chunk;

public class PdfExporter implements ExporterInterface {

    @Override
    public String getDisplayName() { return "PDF"; }

    @Override
    public void export(Session session){

        System.out.println("Do Something");
    }


}
