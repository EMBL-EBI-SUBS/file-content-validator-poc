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
package uk.ac.ebi.ait.filecontentvalidatordemo.manifest;

import uk.ac.ebi.ait.filecontentvalidatordemo.manifest.processor.MetadataProcessorFactory;
import uk.ac.ebi.ait.filecontentvalidatordemo.manifest.processor.MetadataProcessorParameters;
import uk.ac.ebi.ait.filecontentvalidatordemo.manifest.reader.ManifestReader;
import uk.ac.ebi.ena.webin.cli.validator.manifest.Manifest;

public class ManifestReaderBuilder<M extends Manifest> {
    private final Class<ManifestReader<M>> manifestReaderClass;
    private final MetadataProcessorFactory metadataProcessorFactory;
    private ManifestReaderParameters manifestReaderParameters;

    public ManifestReaderBuilder(Class<ManifestReader<M>> manifestReaderClass) {
        this.manifestReaderClass = manifestReaderClass;
        this.manifestReaderParameters = ManifestReader.DEFAULT_PARAMETERS;
        this.metadataProcessorFactory = new MetadataProcessorFactory(null);
    }


    public ManifestReaderBuilder(Class<ManifestReader<M>> manifestReaderClass, MetadataProcessorParameters parameters) {
        this.manifestReaderClass = manifestReaderClass;
        this.manifestReaderParameters = ManifestReader.DEFAULT_PARAMETERS;
        this.metadataProcessorFactory = new MetadataProcessorFactory(parameters);
    }

    ManifestReaderBuilder setManifestReaderParameters(ManifestReaderParameters manifestReaderParameters) {
        this.manifestReaderParameters = manifestReaderParameters;
        return this;
    }

    public ManifestReader<M> build() {
        try {
            return manifestReaderClass
//                    .getDeclaredConstructor(ManifestReaderParameters.class)
//                    .newInstance(manifestReaderParameters);
                    .getDeclaredConstructor(ManifestReaderParameters.class, MetadataProcessorFactory.class)
                    .newInstance(manifestReaderParameters, metadataProcessorFactory);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
