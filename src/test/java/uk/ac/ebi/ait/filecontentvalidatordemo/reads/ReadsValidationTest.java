package uk.ac.ebi.ait.filecontentvalidatordemo.reads;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.ait.filecontentvalidatordemo.error.WebinCliException;
import uk.ac.ebi.ait.filecontentvalidatordemo.executor.FileContentValidatorExecutor;
import uk.ac.ebi.ait.filecontentvalidatordemo.utils.FileContextValidatorExecutorBuilder;
import uk.ac.ebi.ait.filecontentvalidatordemo.utils.ManifestBuilder;
import uk.ac.ebi.ait.filecontentvalidatordemo.utils.ReportTester;
import uk.ac.ebi.ena.webin.cli.validator.file.SubmissionFile;
import uk.ac.ebi.ena.webin.cli.validator.file.SubmissionFiles;
import uk.ac.ebi.ena.webin.cli.validator.manifest.ReadsManifest;
import uk.ac.ebi.ena.webin.cli.validator.manifest.ReadsManifest.FileType;
import uk.ac.ebi.ena.webin.cli.validator.response.ReadsValidationResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.ac.ebi.ait.filecontentvalidatordemo.utils.WebinCliTestUtils.getResourceDir;

public class ReadsValidationTest {

    private static final Logger log = LoggerFactory.getLogger(ReadsValidationTest.class);

    private static final File RESOURCE_DIR = getResourceDir("reads");

    private static final String NAME = "test";

    private static ManifestBuilder manifestBuilder() {
        return new ManifestBuilder()
//                .field("STUDY", "test")
//                .field("SAMPLE", "test")
//                .field("PLATFORM", "ILLUMINA")
//                .field("INSTRUMENT", "unspecified")
//                .field("INSERT_SIZE", "1")
//                .field("LIBRARY_STRATEGY", "CLONEEND")
//                .field("LIBRARY_SOURCE", "OTHER")
//                .field("LIBRARY_SELECTION", "Inverse rRNA selection");
                .field("NAME", NAME);
    }

    private static FileContextValidatorExecutorBuilder<ReadsManifest, ReadsValidationResponse> executorBuilder =
            new FileContextValidatorExecutorBuilder(
                    ReadsManifest.class, FileContextValidatorExecutorBuilder.MetadataProcessorType.MOCK);

    @Test
    public void invalidBAM() throws IOException {
        File manifestFile =
                manifestBuilder().file(FileType.BAM, "invalid.bam").build();

        FileContentValidatorExecutor<ReadsManifest, ReadsValidationResponse> executor =
                executorBuilder.build(manifestFile, RESOURCE_DIR);
        executor.readManifest();
        SubmissionFile<FileType> submissionFile =
                new SubmissionFile<>(FileType.BAM, getResourceFile("reads/invalid.bam"));
        SubmissionFiles<FileType> submissionFiles = new SubmissionFiles<>();
        submissionFiles.set(Collections.singletonList(submissionFile));

        assertThat(submissionFiles.get(FileType.BAM).size()).isOne();

        assertThatThrownBy(() -> executor.validateSubmission())
                .isInstanceOf(WebinCliException.class)
                .hasMessage("");

        new ReportTester(executor).textInFileReport("invalid.bam", "File contains no valid reads");

        printReportFileContent(executor);
    }

    @Test
    public void validBAM() throws IOException {
        File manifestFile =
                manifestBuilder().file(FileType.BAM, "valid.bam").build();

        FileContentValidatorExecutor<ReadsManifest, ReadsValidationResponse> executor =
                executorBuilder.build(manifestFile, RESOURCE_DIR);
        executor.readManifest();
        SubmissionFiles submissionFiles = executor.getManifestReader().getManifest().files();
        assertThat(submissionFiles.get().size()).isEqualTo(1);
        assertThat(submissionFiles.get(FileType.BAM).size()).isOne();
        executor.validateSubmission();

        printReportFileContent(executor);
    }

    @Test
    public void invaliFastq() throws IOException {
        File manifestFile =
                manifestBuilder()
                        .file(FileType.FASTQ, "invalid.fastq.gz")
                        .build();

        FileContentValidatorExecutor<ReadsManifest, ReadsValidationResponse> executor =
                executorBuilder.build(manifestFile, RESOURCE_DIR);
        executor.readManifest();
        SubmissionFiles submissionFiles = executor.getManifestReader().getManifest().files();
        assertThat(submissionFiles.get().size()).isEqualTo(1);
        assertThat(submissionFiles.get(FileType.FASTQ).size()).isOne();

        assertThatThrownBy(() -> executor.validateSubmission())
                .isInstanceOf(WebinCliException.class)
                .hasMessage("");

        new ReportTester(executor).textInFileReport("invalid.fastq.gz", "does not match FASTQ regexp");

        printReportFileContent(executor);
    }

    @Test
    public void validFastq() throws IOException {
        File manifestFile =
                manifestBuilder().file(FileType.FASTQ, "valid.fastq.gz").build();

        FileContentValidatorExecutor<ReadsManifest, ReadsValidationResponse> executor =
                executorBuilder.build(manifestFile, RESOURCE_DIR);
        executor.readManifest();
        SubmissionFiles submissionFiles = executor.getManifestReader().getManifest().files();
        assertThat(submissionFiles.get().size()).isEqualTo(1);
        assertThat(submissionFiles.get(FileType.FASTQ).size()).isOne();
        executor.validateSubmission();

        printReportFileContent(executor);
    }

    @Test
    public void validPairedFastqTwoFiles() throws IOException {
        File manifestFile =
                manifestBuilder()
                        .file(FileType.FASTQ, "valid_paired_1.fastq.gz")
                        .file(FileType.FASTQ, "valid_paired_2.fastq.gz")
                        .build();

        FileContentValidatorExecutor<ReadsManifest, ReadsValidationResponse> executor =
                executorBuilder.build(manifestFile, RESOURCE_DIR);
        executor.readManifest();
        SubmissionFiles submissionFiles = executor.getManifestReader().getManifest().files();
        assertThat(submissionFiles.get().size()).isEqualTo(2);
        assertThat(submissionFiles.get(FileType.FASTQ).size()).isEqualTo(2);
        executor.validateSubmission();
        assertThat(executor.getValidationResponse().isPaired());

        printReportFileContent(executor);
    }

    @Test
    public void validPairedFastqOneFile() throws IOException {
        File manifestFile =
                manifestBuilder().file(FileType.FASTQ, "valid_paired_single_fastq.gz").build();

        FileContentValidatorExecutor<ReadsManifest, ReadsValidationResponse> executor =
                executorBuilder.build(manifestFile, RESOURCE_DIR);
        executor.readManifest();
        SubmissionFiles submissionFiles = executor.getManifestReader().getManifest().files();
        assertThat(submissionFiles.get().size()).isEqualTo(1);
        assertThat(submissionFiles.get(FileType.FASTQ).size()).isOne();
        executor.validateSubmission();
        assertThat(executor.getValidationResponse().isPaired());

        printReportFileContent(executor);
    }

    @Test
    public void invalidPairedFastqTwoFiles() throws IOException {
        File manifestFile =
                manifestBuilder()
                        .file(FileType.FASTQ, "invalid_not_paired_1.fastq.gz")
                        .file(FileType.FASTQ, "invalid_not_paired_2.fastq.gz")
                        .build();

        FileContentValidatorExecutor<ReadsManifest, ReadsValidationResponse> executor =
                executorBuilder.build(manifestFile, RESOURCE_DIR);
        executor.readManifest();
        SubmissionFiles submissionFiles = executor.getManifestReader().getManifest().files();
        assertThat(submissionFiles.get().size()).isEqualTo(2);
        assertThat(submissionFiles.get(FileType.FASTQ).size()).isEqualTo(2);

        assertThatThrownBy(() -> executor.validateSubmission())
                .isInstanceOf(WebinCliException.class)
                .hasMessage("");

        new ReportTester(executor).textInSubmissionReport("Detected paired fastq submission with less than 20% of paired reads");

        printReportFileContent(executor);
    }

    @Test
    public void sameFilePairedFastq() throws IOException {
        File manifestFile =
                manifestBuilder()
                        .file(FileType.FASTQ, "valid.fastq.gz")
                        .file(FileType.FASTQ, "valid.fastq.gz")
                        .build();

        FileContentValidatorExecutor<ReadsManifest, ReadsValidationResponse> executor =
                executorBuilder.build(manifestFile, RESOURCE_DIR);
        executor.readManifest();
        SubmissionFiles submissionFiles = executor.getManifestReader().getManifest().files();
        assertThat(submissionFiles.get().size()).isEqualTo(2);
        assertThat(submissionFiles.get(FileType.FASTQ).size()).isEqualTo(2);

        assertThatThrownBy(() -> executor.validateSubmission())
                .isInstanceOf(WebinCliException.class)
                .hasMessage("");

        System.out.println(executor.getValidationDir().getAbsolutePath());
        new ReportTester(executor).textInSubmissionReport("Multiple (1) occurrences of read name");

        printReportFileContent(executor);
    }

    @Test
    public void invalidCram() throws IOException {
        File manifestFile =
                manifestBuilder()
                        .file(FileType.CRAM, "invalid.cram")
                        .build();

        FileContentValidatorExecutor<ReadsManifest, ReadsValidationResponse> executor =
                executorBuilder.build(manifestFile, RESOURCE_DIR);
        executor.readManifest();
        SubmissionFiles submissionFiles = executor.getManifestReader().getManifest().files();
        assertThat(submissionFiles.get().size()).isEqualTo(1);
        assertThat(submissionFiles.get(FileType.CRAM).size()).isOne();

        assertThatThrownBy(() -> executor.validateSubmission())
                .isInstanceOf(WebinCliException.class)
                .hasMessage("");

        new ReportTester(executor).textInFileReport("invalid.cram", "File contains no valid reads");

        printReportFileContent(executor);
    }

    @Test
    public void validCram() throws IOException {
        File manifestFile =
                manifestBuilder().file(FileType.CRAM, "valid.cram").build();

        FileContentValidatorExecutor<ReadsManifest, ReadsValidationResponse> executor =
                executorBuilder.build(manifestFile, RESOURCE_DIR);
        executor.readManifest();
        SubmissionFiles submissionFiles = executor.getManifestReader().getManifest().files();
        assertThat(submissionFiles.get().size()).isEqualTo(1);
        assertThat(submissionFiles.get(FileType.CRAM).size()).isOne();
        executor.validateSubmission();

        printReportFileContent(executor);
    }

    private void printReportFileContent(FileContentValidatorExecutor<ReadsManifest, ReadsValidationResponse> executor) throws IOException {
        System.out.println("Validation Report file content:");
        Files.lines(Paths.get(executor.getSubmissionReportFile().getPath())).forEach(System.out::println);
    }

    public static File getResourceFile(String filename) {
        return new File(
                Objects.requireNonNull(ReadsValidationTest.class.getClassLoader().getResource(filename)).getFile()
        );
    }
}
