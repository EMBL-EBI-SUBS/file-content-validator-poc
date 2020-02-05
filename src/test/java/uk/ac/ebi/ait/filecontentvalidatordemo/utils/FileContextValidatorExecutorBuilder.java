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
package uk.ac.ebi.ait.filecontentvalidatordemo.utils;

import org.mockito.invocation.InvocationOnMock;
import uk.ac.ebi.ait.filecontentvalidatordemo.config.FileConfig;
import uk.ac.ebi.ait.filecontentvalidatordemo.config.FileContentValidatorContext;
import uk.ac.ebi.ait.filecontentvalidatordemo.executor.FileContentValidatorExecutor;
import uk.ac.ebi.ait.filecontentvalidatordemo.manifest.processor.metadata.SampleProcessor;
import uk.ac.ebi.ena.webin.cli.validator.api.ValidationResponse;
import uk.ac.ebi.ena.webin.cli.validator.manifest.Manifest;
import uk.ac.ebi.ena.webin.cli.validator.reference.Sample;

import java.io.File;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

public class FileContextValidatorExecutorBuilder<M extends Manifest, R extends ValidationResponse> {
    private final Class<M> manifestClass;
    private final FileConfig parameters = WebinCliTestUtils.getTestWebinCliParameters();

    private SampleProcessor sampleProcessor;

    public enum MetadataProcessorType {
        DEFAULT,
        MOCK
    }

    public FileContextValidatorExecutorBuilder(Class<M> manifestClass, MetadataProcessorType metadataProcessorType) {
        this.manifestClass = manifestClass;
    }

    public FileContextValidatorExecutorBuilder sample(Sample sample) {
        this.sampleProcessor = spy(new SampleProcessor(null));
        doAnswer((InvocationOnMock invocation) ->
                {
                    SampleProcessor processor = (SampleProcessor)invocation.getMock();
                    processor.getCallback().notify(sample);
                    return null;
                }
        ).when(this.sampleProcessor).process(any(), any());
        return this;
    }


    public FileContentValidatorExecutor<M, R> build(File manifestFile, File inputDir) {
        parameters.setManifestFile(manifestFile);
        parameters.setInputDir(inputDir);
        parameters.setOutputDir(WebinCliTestUtils.createTempDir());
        parameters.setSampleProcessor(sampleProcessor);
        return FileContentValidatorContext.createExecutor(manifestClass, parameters);
    }

    public FileConfig getParameters() {
        return parameters;
    }
}
