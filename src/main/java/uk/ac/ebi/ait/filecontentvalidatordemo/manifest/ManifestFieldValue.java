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

import uk.ac.ebi.ena.webin.cli.validator.message.ValidationOrigin;

import java.util.ArrayList;
import java.util.List;

public class
ManifestFieldValue 
{
    private final ManifestFieldDefinition definition;
    private String value;
    private final List<ValidationOrigin> origin = new ArrayList<>();
    private boolean validFieldValueOrFileSuffix = true;

    public ManifestFieldValue(ManifestFieldDefinition definition, String value, ValidationOrigin origin )
    {
        assert( definition != null );
        assert( value != null );
        this.definition = definition;
        this.value = value;
        this.origin.add(origin);
        this.origin.add(new ValidationOrigin("field", definition.getName()));
        this.origin.add(new ValidationOrigin("value", value));
    }
    
    public String
    getName() 
    {
        return definition.getName();
    }

    
    public ManifestFieldDefinition 
    getDefinition() 
    {
        return definition;
    }

    
    public String
    getValue() 
    {
        return value;
    }

    
    public void 
    setValue( String value )
    {
        this.value = value;
    }

    
    public boolean 
    isValidFieldValueOrFileSuffix() 
    {
        return validFieldValueOrFileSuffix;
    }

    
    public void 
    setValidFieldValueOrFileSuffix( boolean validFieldValueOrFileSuffix )
    {
        this.validFieldValueOrFileSuffix = validFieldValueOrFileSuffix;
    }

    
    public List<ValidationOrigin>
    getOrigin()
    {
        return origin;
    }
}
