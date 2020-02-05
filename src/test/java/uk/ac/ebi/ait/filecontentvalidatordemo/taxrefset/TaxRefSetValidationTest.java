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
package uk.ac.ebi.ait.filecontentvalidatordemo.taxrefset;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.ait.filecontentvalidatordemo.executor.FileContentValidatorExecutor;
import uk.ac.ebi.ait.filecontentvalidatordemo.utils.FileContextValidatorExecutorBuilder;
import uk.ac.ebi.ait.filecontentvalidatordemo.utils.ManifestBuilder;
import uk.ac.ebi.ena.webin.cli.validator.api.ValidationResponse;
import uk.ac.ebi.ena.webin.cli.validator.manifest.TaxRefSetManifest;

import java.io.File;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.ac.ebi.ait.filecontentvalidatordemo.utils.WebinCliTestUtils.getDefaultSample;
import static uk.ac.ebi.ait.filecontentvalidatordemo.utils.WebinCliTestUtils.getResourceDir;

public class TaxRefSetValidationTest {

  private static final File VALID_DIR = getResourceDir("taxrefset");
  private static final FileContextValidatorExecutorBuilder<TaxRefSetManifest, ValidationResponse> executorBuilder =
          new FileContextValidatorExecutorBuilder(TaxRefSetManifest.class, FileContextValidatorExecutorBuilder.MetadataProcessorType.MOCK)
                  .sample(getDefaultSample());

  private static ManifestBuilder manifestBuilder() {
    return new ManifestBuilder()
//            .field("DESCRIPTION", "test_desc")
//            .field("STUDY", "ERP115786")
            .field("TAXONOMY_SYSTEM", "NCBI")
//            .field("TAXONOMY_SYSTEM_VERSION", "1")
            .field("CUSTOM_FIELD", "Annotation:Source of annotation")
            .field("CUSTOM_FIELD", "ITSoneDB URL:URL within ITSoneDB")
            .field("NAME", "test");
  }

  @Before
  public void before() {
    Locale.setDefault(Locale.UK);
  }

  @Test
  public void testValidSubmission() {
    File[] files = VALID_DIR.listFiles((dir, name) -> name.endsWith(".tsv.gz"));
    assertThat(files.length).isEqualTo(1);
    String tsvFileName = files[0].getName();

    files = VALID_DIR.listFiles((dir, name) -> name.endsWith(".fasta.gz"));
    assertThat(files.length).isEqualTo(1);
    String fastaFileName = files[0].getName();

    File manifestFile = manifestBuilder().file(TaxRefSetManifest.FileType.TAB, tsvFileName).file(TaxRefSetManifest.FileType.FASTA, fastaFileName).build();

    FileContentValidatorExecutor<TaxRefSetManifest, ValidationResponse> executor = executorBuilder.build(manifestFile, VALID_DIR);
    executor.readManifest();
    executor.validateSubmission();
    assertThat(executor.getManifestReader().getManifest().files().get(TaxRefSetManifest.FileType.TAB))
            .size()
            .isOne();
    assertThat(executor.getManifestReader().getManifest().files().get(TaxRefSetManifest.FileType.FASTA))
            .size()
            .isOne();
    Map<String, String> customFields = executor.getManifestReader().getManifest().getCustomFields();
    Assert.assertEquals(2, customFields.size());
    Assert.assertEquals("Source of annotation", customFields.get("Annotation"));
    Assert.assertEquals("URL within ITSoneDB", customFields.get("ITSoneDB URL"));

  }
}
