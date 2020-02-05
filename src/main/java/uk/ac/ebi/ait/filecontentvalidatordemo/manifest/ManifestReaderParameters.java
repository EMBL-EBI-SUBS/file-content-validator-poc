package uk.ac.ebi.ait.filecontentvalidatordemo.manifest;

public interface ManifestReaderParameters {
    boolean isManifestValidateMandatory();
    boolean isManifestValidateFileExist();
    boolean isManifestValidateFileCount();
}
