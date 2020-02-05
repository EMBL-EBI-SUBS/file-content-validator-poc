/*
 * Copyright 2018-2019 EMBL - European Bioinformatics Institute
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.ac.ebi.ait.filecontentvalidatordemo.config;

import uk.ac.ebi.ait.filecontentvalidatordemo.executor.FileContentValidatorExecutor;
import uk.ac.ebi.ait.filecontentvalidatordemo.manifest.ManifestReaderBuilder;
import uk.ac.ebi.ait.filecontentvalidatordemo.manifest.reader.GenomeManifestReader;
import uk.ac.ebi.ait.filecontentvalidatordemo.manifest.reader.ManifestReader;
import uk.ac.ebi.ait.filecontentvalidatordemo.manifest.reader.ReadsManifestReader;
import uk.ac.ebi.ait.filecontentvalidatordemo.manifest.reader.SequenceManifestReader;
import uk.ac.ebi.ait.filecontentvalidatordemo.manifest.reader.TaxRefSetManifestReader;
import uk.ac.ebi.ait.filecontentvalidatordemo.manifest.reader.TranscriptomeManifestReader;
import uk.ac.ebi.embl.api.validation.submission.SubmissionValidator;
import uk.ac.ebi.ena.readtools.validator.ReadsValidator;
import uk.ac.ebi.ena.txmbvalidator.TxmbValidator;
import uk.ac.ebi.ena.webin.cli.validator.api.ValidationResponse;
import uk.ac.ebi.ena.webin.cli.validator.api.Validator;
import uk.ac.ebi.ena.webin.cli.validator.manifest.GenomeManifest;
import uk.ac.ebi.ena.webin.cli.validator.manifest.Manifest;
import uk.ac.ebi.ena.webin.cli.validator.manifest.ReadsManifest;
import uk.ac.ebi.ena.webin.cli.validator.manifest.SequenceManifest;
import uk.ac.ebi.ena.webin.cli.validator.manifest.TaxRefSetManifest;
import uk.ac.ebi.ena.webin.cli.validator.manifest.TranscriptomeManifest;

public enum FileContentValidatorContext {
  genome(
      GenomeManifest.class,
      GenomeManifestReader.class,
//      GenomeXmlWriter.class,
      SubmissionValidator.class,
      "Genome assembly"),
  transcriptome(
      TranscriptomeManifest.class,
      TranscriptomeManifestReader.class,
//      TranscriptomeXmlWriter.class,
      SubmissionValidator.class,
      "Transcriptome assembly"),
  sequence(
      SequenceManifest.class,
      SequenceManifestReader.class,
//      SequenceXmlWriter.class,
      SubmissionValidator.class,
      "Sequence assembly"),
  reads(
      ReadsManifest.class,
      ReadsManifestReader.class,
//      ReadsXmlWriter.class,
      ReadsValidator.class,
      "Raw reads"),
  taxrefset(
          TaxRefSetManifest .class,
          TaxRefSetManifestReader.class,
//          TaxRefSetXmlWriter.class,
          TxmbValidator.class,
          "Taxonomy reference set");

  private final Class<? extends Manifest> manifestClass;
  private final Class<? extends ManifestReader<? extends Manifest>> manifestReaderClass;
//  private final Class<? extends XmlWriter<? extends Manifest, ? extends ValidationResponse>> xmlWriterClass;
  private final Class<? extends Validator<? extends Manifest, ? extends ValidationResponse>> validatorClass;

  private final String titlePrefix;

  FileContentValidatorContext(
      Class<? extends Manifest> manifestClass,
      Class<? extends ManifestReader<? extends Manifest>> manifestReaderClass,
//      Class<? extends XmlWriter<? extends Manifest, ? extends ValidationResponse>> xmlWriterClass,
      Class<? extends Validator<? extends Manifest, ? extends ValidationResponse>> validatorClass,
      String titlePrefix) {
    this.manifestClass = manifestClass;
    this.manifestReaderClass = manifestReaderClass;
//    this.xmlWriterClass = xmlWriterClass;
    this.validatorClass = validatorClass;
    this.titlePrefix = titlePrefix;
  }

  public Class<? extends Manifest> getManifestClass() {
    return manifestClass;
  }

  public Class<? extends ManifestReader> getManifestReaderClass() {
    return manifestReaderClass;
  }

  public static <M extends Manifest, R extends ValidationResponse> FileContentValidatorExecutor<M, R> createExecutor(
          Class<M> manifestClass, FileConfig parameters) {
    for (FileContentValidatorContext context : FileContentValidatorContext.values()) {
      if (context.getManifestClass().equals(manifestClass)) {
        return (FileContentValidatorExecutor<M, R>) context.createExecutor(parameters);
      }
    }
    return null;
  }

  public FileContentValidatorExecutor<?, ?> createExecutor(FileConfig parameters) {
    return createExecutor(parameters,
        new ManifestReaderBuilder(manifestReaderClass, parameters).build()
    );
  }

  public FileContentValidatorExecutor<?, ?> createExecutor(
          FileConfig parameters, ManifestReader<?> manifestReader) {

    try {
//      XmlWriter<?, ?> xmlWriter = xmlWriterClass.newInstance();
      Validator<?, ?> validator = validatorClass.newInstance();
      return new FileContentValidatorExecutor(this, parameters, manifestReader, validator);

    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public String getTitlePrefix() {
    return titlePrefix;
  }
}
