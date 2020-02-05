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
package uk.ac.ebi.ait.filecontentvalidatordemo.manifest.processor.metadata;

import uk.ac.ebi.ait.filecontentvalidatordemo.error.WebinCliException;
import uk.ac.ebi.ait.filecontentvalidatordemo.manifest.ManifestFieldProcessor;
import uk.ac.ebi.ait.filecontentvalidatordemo.manifest.ManifestFieldValue;
import uk.ac.ebi.ait.filecontentvalidatordemo.manifest.processor.MetadataProcessorParameters;
import uk.ac.ebi.ait.filecontentvalidatordemo.service.SampleService;
import uk.ac.ebi.ait.filecontentvalidatordemo.utils.WebinCliMessage;
import uk.ac.ebi.ena.webin.cli.validator.message.ValidationMessage;
import uk.ac.ebi.ena.webin.cli.validator.message.ValidationResult;
import uk.ac.ebi.ena.webin.cli.validator.reference.Sample;

public class
SampleProcessor implements ManifestFieldProcessor
{
    private final MetadataProcessorParameters parameters;
    private ManifestFieldProcessor.Callback<Sample> callback;

    public
    SampleProcessor(MetadataProcessorParameters parameters, ManifestFieldProcessor.Callback<Sample> callback )
    {
        this.parameters = parameters;
        this.callback = callback;
    }

    public
    SampleProcessor( MetadataProcessorParameters parameters )
    {
        this.parameters = parameters;
    }

    public void setCallback(Callback<Sample> callback) {
        this.callback = callback;
    }

    public Callback<Sample> getCallback() {
        return callback;
    }

    @Override
    public void
    process( ValidationResult result, ManifestFieldValue fieldValue )
    {
        String value = fieldValue.getValue();

        try
        {
            SampleService sampleService = new SampleService.Builder()
                                                           .setCredentials( parameters.getUsername(), parameters.getPassword() )
                                                           .setTest( parameters.isTest() )
                                                           .build();
            Sample sample = sampleService.getSample( value );
            fieldValue.setValue( sample.getBioSampleId() );
            callback.notify( sample );

        } catch( WebinCliException e )
        {
            if (WebinCliMessage.CLI_AUTHENTICATION_ERROR.text().equals(e.getMessage())) {
                result.add(ValidationMessage.error(e));
            }
            else {
                result.add(ValidationMessage.error(WebinCliMessage.SAMPLE_PROCESSOR_LOOKUP_ERROR, value, e.getMessage()));
            }
        }
    }
}
