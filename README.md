# File-content-validator - Proof of Concept

## Goal
Our goal was to create a file content validator library for raw data files that could be used to create a local file content validator and file content validator online service.

The local file content validator would be used by the users to validate their raw data files before creating a submission and uploading those files.

The online service would be used by many parties, amongst them ENA’s ([European Nucleotide Archive](https://www.ebi.ac.uk/ena)) webin applications (Webin submission service, programmatic Webin submission service and the Webin command line submission service), DSP ([Data Submission Portal](https://www.ebi.ac.uk/submission/api.html)), EBI’s Archives ([EGA](https://www.ebi.ac.uk/ega/), [EVA](https://www.ebi.ac.uk/eva/), [ArrayExpress](https://www.ebi.ac.uk/arrayexpress/), [PRIDE](https://www.ebi.ac.uk/pride/), [MetaboLights](https://www.ebi.ac.uk/metabolights/)) and could be also used by various applications implemented by users, like HCA ([Human Cell Atlas](https://www.humancellatlas.org/)).

## Current status
Currently in the AIT (Archival Infrastructure and Technology) team we have 3 different file content validators. One for ENA, 1 for DSP and another 1 for HCA. That also means that we have 3 different code bases.

Possible issues:
Each of them could have different results when validating the content of the files. It could happen that HCA - that is a client of DSP - is successfully validating a file and sends it to DSP, but DSP reject it with a validation error. That could confuse the user of HCA.
3 different code bases also means separate maintenance for all of them (bug fixes, features addition, improvements)

## Research project and its result
The goal with the research was to try to create a Proof of Concept project based on ENA’s webin-cli’s file content validator.

In this project the following libraries were used as dependencies from ENA:

* webin-cli-validator: https://github.com/enasequence/webin-cli-validator
* readtools: https://github.com/enasequence/readtools
* Sequencetools: https://github.com/enasequence/sequencetools
* ENA TXMB Tools: https://github.com/enasequence/txmbtools

The webin-cli library (Webin command line submission interface) was not used intentionally, because we only want to validate the contents of the files and return the validation result to the user. We don’t want to provide any other functionality in this validator.

The research goal was that with the above listed ENA libraries + some 3rd party tools and Spring dependencies create a new project and copy the file validation test cases from webin-cli and make these tests successfully pass.

The application could be created successfully to pass all the test cases without using the webin-cli library, although along the way there were some issues to solve. These are listed in the next session of this document.


**NOTE** In the created POC application there are a few classes that were copied over from the webin-cli library. In these classes there are parts of the code that deliberately has been removed (commented out), because I suspected that they don’t belong to the file content validation. These decisions might not be correct, because the lack of my knowledge related to these validation tasks. This probably needs a discussion with a relevant person from the ENA team.

## Issues / Questions

1. I had to copy or recreate a lots of classes from webin-cli library, because webin-cli-validator library very strongly coupled to it. In my opinion it should be the other way around, so webin-cli should use the validator library.

2. Classes related to XML writer functionality are also strongly coupled to the current version of validation. When I removed them from the validator context the validation executed without any problem. I am wondering what is their purpose in the validation context?

3. What is the real purpose of the manifest file? Does the file content validation need it?

4. If the file content validation needs the manifest file, are all of the current fields used for validation purposes or could we remove some of them?

## Suggested plans for improvement

#### Suggestion for a workaround (quick fix): 

1. Copy all the classes needed by the validator - to do file content validation - from webin-cli into webin-cli-validator library. Copy them exactly into the same java package as they were in webin-cli. This way webin-cli could overwrite them, if it is needed. Remove all the XML Writer related classes from the validator context in the validator library.

**Timeframe**: 1 sprint (2 weeks) to TEST env, after 1 sprint test to PROD

**Resources**: 1 dev from DSP (Karoly) coordinating the changes with ENA team

2. With the newly refactored webin-cli-validator a new online file-content-validator service could be created. This service could be a public service that could be used by all the above parties listed in the Goal section.

**TimeFrame**: 2 sprint (4 weeks) to TEST env, after 1 sprint test to PROD

**Resources**: 1 dev from DSP (Karoly)

3. ENA could continue to maintain its libraries (webin-cli, webin-cli-validator) after this refactor.

#### Suggestion for permanent refactoring:

1. After the above Issues/Questions has been discussed the webin-cli-validator could be refactored: move the validation related part out from webin-cli library into the webin-cli-validator library and remove all the XML Writer related classes from the validator context, too.

**Timeframe**: 2 sprint (4 weeks) to TEST env, after 1 sprint test to PROD

**Resources**: 1 dev from DSP (Karoly) & 1 dev from ENA

2. With the newly refactored webin-cli-validator a new online file-content-validator service could be created. This service could be a public service that could be used by all the above parties listed in the Goal section.

**TimeFrame**: 2 sprint (4 weeks) to TEST env, after 1 sprint test to PROD

**Resources**: 1 dev from DSP (Karoly)

3. ENA could continue to maintain its libraries (webin-cli, webin-cli-validator) after this refactor.
