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
package uk.ac.ebi.ait.filecontentvalidatordemo.manifest.processor;

import uk.ac.ebi.ait.filecontentvalidatordemo.manifest.ManifestFieldProcessor;
import uk.ac.ebi.ait.filecontentvalidatordemo.manifest.ManifestFieldValue;
import uk.ac.ebi.ait.filecontentvalidatordemo.utils.WebinCliMessage;
import uk.ac.ebi.ena.webin.cli.validator.message.ValidationMessage;
import uk.ac.ebi.ena.webin.cli.validator.message.ValidationResult;

import java.util.regex.Pattern;

public class 
ASCIIFileNameProcessor implements ManifestFieldProcessor
{
    static final Pattern pattern = Pattern.compile( "^([\\p{Alnum}]|\\\\|\\]|\\[|#|-|_|\\.|,|\\/|:|@|\\+| |\\(|\\)|'|~|<|%|\\?)+$" );

    @Override
    public void
    process( ValidationResult result, ManifestFieldValue fieldValue )
    {
        if( !pattern.matcher( fieldValue.getValue() ).matches() )
            result.add( ValidationMessage.error( WebinCliMessage.ASCII_FILE_NAME_PROCESSOR_ERROR, fieldValue.getName(), fieldValue.getValue(), pattern.pattern() ) );
    }
}
