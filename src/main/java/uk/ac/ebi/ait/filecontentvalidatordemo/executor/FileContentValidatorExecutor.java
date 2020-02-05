package uk.ac.ebi.ait.filecontentvalidatordemo.executor;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.ac.ebi.ait.filecontentvalidatordemo.config.FileConfig;
import uk.ac.ebi.ait.filecontentvalidatordemo.config.FileContentValidatorContext;
import uk.ac.ebi.ait.filecontentvalidatordemo.error.WebinCliException;
import uk.ac.ebi.ait.filecontentvalidatordemo.manifest.reader.ManifestReader;
import uk.ac.ebi.ait.filecontentvalidatordemo.utils.FileUtils;
import uk.ac.ebi.ait.filecontentvalidatordemo.utils.WebinCliMessage;
import uk.ac.ebi.ena.webin.cli.validator.api.ValidationResponse;
import uk.ac.ebi.ena.webin.cli.validator.api.Validator;
import uk.ac.ebi.ena.webin.cli.validator.file.SubmissionFile;
import uk.ac.ebi.ena.webin.cli.validator.manifest.Manifest;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static uk.ac.ebi.ait.filecontentvalidatordemo.utils.FileUtils.emptyDirectory;

@Slf4j
@Data
public class FileContentValidatorExecutor<M extends Manifest, R extends ValidationResponse> {

    private File validationDir;
    private File processDir;

    private ManifestReader<M> manifestReader;
    private final Validator<M, R> validator;
    protected R validationResponse;

    private FileConfig fileConfig;
    private final FileContentValidatorContext context;

    private static final String VALIDATE_DIR = "validate";
    private static final String PROCESS_DIR = "process";

    private static final String REPORT_FILE = "webin-cli.report";

    public FileContentValidatorExecutor(FileContentValidatorContext context, FileConfig fileConfig,
                                        ManifestReader<M> manifestReader, Validator<M, R> validator) {
        this.manifestReader = manifestReader;
        this.validator = validator;
        this.fileConfig = fileConfig;
        this.context = context;
    }

    public final void readManifest() {
        this.validationDir = FileUtils.createOutputDir(fileConfig.getOutputDir(), ".");

        File manifestReportFile = getManifestReportFile();
        manifestReportFile.delete();

        try {
            getManifestReader().readManifest(
                    fileConfig.getInputDir().toPath(),
                    fileConfig.getManifestFile(),
                    getManifestReportFile());
        } catch (WebinCliException ex) {
            throw ex;
        } catch (Exception ex) {
            throw WebinCliException.systemError(ex, WebinCliMessage.EXECUTOR_INIT_ERROR.format(ex.getMessage()));
        }

        if (manifestReader == null || !manifestReader.getValidationResult().isValid()) {
            throw WebinCliException.userError( WebinCliMessage.MANIFEST_READER_INVALID_MANIFEST_FILE_ERROR.format(manifestReportFile.getPath()) );
        }
    }

    public final void validateSubmission() {
        var submissionName = getSubmissionName();
        this.validationDir = createSubmissionDir(VALIDATE_DIR, submissionName);
        this.processDir = createSubmissionDir(PROCESS_DIR, submissionName);

        M manifest = manifestReader.getManifest();

//        setIgnoreErrors(manifest);

        if(!manifest.getFiles().get().isEmpty()) {
            for (SubmissionFile subFile : (List<SubmissionFile>) (manifest.getFiles().get())) {
                subFile.setReportFile(Paths.get(getValidationDir().getPath()).resolve(subFile.getFile().getName() + ".report").toFile());
            }
        }

        final File submissionReportFile = getSubmissionReportFile();
        log.info(submissionReportFile.getAbsolutePath());
        manifest.setReportFile(submissionReportFile);
        manifest.setProcessDir(getProcessDir());

        try {
            validationResponse = getValidator().validate(manifest);
        } catch (RuntimeException ex) {
            throw WebinCliException.systemError(ex);
        }
        if(validationResponse != null && validationResponse.getStatus() == ValidationResponse.status.VALIDATION_ERROR) {
            throw WebinCliException.validationError("");
        }
    }

    public File
    getManifestReportFile( )
    {
        File manifestFile = fileConfig.getManifestFile();
        return FileUtils.getReportFile( this.validationDir, manifestFile.getName(), ".report" );
    }

    public File createSubmissionDir(String dir, String submissionName) {
        if (StringUtils.isBlank(submissionName)) {
            throw WebinCliException.systemError(WebinCliMessage.EXECUTOR_INIT_ERROR.format("Missing submission name."));
        }
        File newDir = FileUtils.createOutputDir( fileConfig.getOutputDir(), String.valueOf( context ), submissionName, dir);
        if (!emptyDirectory(newDir)) {
            throw WebinCliException.systemError(WebinCliMessage.EXECUTOR_EMPTY_DIRECTORY_ERROR.format(newDir));
        }
        return newDir;
    }

    private Validator<M,R> getValidator() {
        return validator;
    }

    private String getSubmissionName() {
        String name = manifestReader.getManifest().getName();
        if (name != null) {
            return name.trim().replaceAll("\\s+", "_");
        }
        return null;
    }

    public File getSubmissionReportFile() {
        return Paths.get(getValidationDir().getPath()).resolve(REPORT_FILE).toFile();
    }
}
