package uk.ac.ebi.ait.filecontentvalidatordemo.config;

import lombok.Data;
import uk.ac.ebi.ait.filecontentvalidatordemo.manifest.processor.MetadataProcessorParameters;
import uk.ac.ebi.ait.filecontentvalidatordemo.manifest.processor.metadata.SampleProcessor;

import java.io.File;

@Data
public class FileConfig implements MetadataProcessorParameters {
    private File outputDir;
    private File inputDir = new File( "." );
    private File manifestFile;

    private SampleProcessor sampleProcessor;

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isTest() {
        return false;
    }
}
