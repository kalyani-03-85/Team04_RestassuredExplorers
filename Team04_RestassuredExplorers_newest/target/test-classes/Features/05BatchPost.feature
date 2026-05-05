@batchPost
Feature: Batch post request

Background: Admin sets valid Auth
Given Admin sets valid authorization

Scenario: Admin creates batch with only optional fields
Given Admin creates POST request with only optional fields in request body
When Admin sends HTTPS request to the endpoint for POST in Batch controller
Then Admin receives Status Code with message "No of Classes is needed; It should be a positive number"

Scenario: Admin creates batch without underscore in name
Given Admin creates POST request without underscore in batch name
When Admin sends HTTPS request to the endpoint for POST in Batch controller
Then Admin receives Status Code with message "batchName Must be in the format of ProgramName_<number>"

Scenario: Admin creates batch with hyphen in name
Given Admin creates POST request with hyphen in batch name
When Admin sends HTTPS request to the endpoint for POST in Batch controller
Then Admin receives Status Code with message "Invalid batch name"

Scenario: Admin creates batch with suffix characters
Given Admin creates POST request with characters in the suffix of batch name
When Admin sends HTTPS request to the endpoint for POST in Batch controller
Then Admin receives Status Code with message "batchName Must be in the format of ProgramName_<number>"

Scenario: Admin creates batch with special characters in suffix
Given Admin creates POST request with special characters in the suffix of batch name
When Admin sends HTTPS request to the endpoint for POST in Batch controller
Then Admin receives Status Code with message "batchName Must be in the format of ProgramName_<number>"

Scenario: Admin creates batch with name length more than twenty eight
Given Admin creates POST request with batch name length more than max characters including prefixed program name
When Admin sends HTTPS request to the endpoint for POST in Batch controller
Then Admin receives Status Code with message "Batch Name must be of min length 6 and max length 28 characters."

Scenario: Admin creates batch with name length less than six
Given Admin creates POST request with batch name length less than six characters including prefixed program name
When Admin sends HTTPS request to the endpoint for POST in Batch controller
Then Admin receives Status Code with message "Invalid batch name."

Scenario: Admin creates batch with duplicate name
Given Admin creates POST request with batch name that is already existing in the system
When Admin sends HTTPS request to the endpoint for POST in Batch controller
Then Admin receives status code with error message

Scenario: Admin creates batch with short description
Given Admin creates POST request with batch description less than four characters
When Admin sends HTTPS request to the endpoint for POST in Batch controller
Then Admin receives status code with error message

Scenario: Admin creates batch with long description
Given Admin creates POST request with batch description more than twenty five characters
When Admin sends HTTPS request to the endpoint for POST in Batch controller
Then Admin receives status code with error message

Scenario: Admin creates batch with random characters in status
Given Admin creates POST request with random characters in status field
When Admin sends HTTPS request to the endpoint for POST in Batch controller
Then Admin receives Status Code with message "Invalid Status: must be Active or Inactive"

Scenario: Admin creates batch with random numbers in status
Given Admin creates POST request with random numbers in status field
When Admin sends HTTPS request to the endpoint for POST in Batch controller
Then Admin receives Status Code with message "Invalid Status: must be Active or Inactive"

Scenario: Admin creates batch with special characters in status
Given Admin creates POST request with special characters in status field
When Admin sends HTTPS request to the endpoint for POST in Batch controller
Then Admin receives Status Code with message "Invalid Status: must be Active or Inactive"

Scenario: Admin creates batch with classes length less than one
Given Admin creates POST request with number of classes length less than one
When Admin sends HTTPS request to the endpoint for POST in Batch controller
Then Admin receives Status Code with message "No of Classes is needed; It should be a positive number"

Scenario: Admin creates batch with classes length more than allowed
Given Admin creates POST request with with number of classes length more than allowed
When Admin sends HTTPS request to the endpoint for POST in Batch controller
Then Admin receives Status Code with message "No of classes should be a 1 digit or 2 digit number. Cannot exceed 2 digits"


Scenario: Admin creates batch with inactive program id
Given Admin creates POST request with inactive program id
When Admin sends HTTPS request to the endpoint with inactive program id for POST in Batch controller
Then Admin receives status code with error message

Scenario: Admin creates batch with invalid program id
Given Admin creates POST request with program id that is not exist in the system
When Admin sends HTTPS request to the endpoint with invalid program id for POST in Batch controller
Then Admin receives status code with error message

Scenario: Admin creates batch with invalid endpoint
Given Admin creates POST request with valid request body and invalid end point
When Admin sends HTTPS request to the endpoint for POST in Batch controller
Then Admin receives status code with error message

Scenario: Admin creates batch with invalid content type
Given Admin creates POST request with invalid content type in Batch Controller
When Admin sends a HTTPS request to the valid endpoint with invalid content type for POST in Batch controller
Then Admin receives status code with error message

Scenario: Admin creates batch with invalid method
Given Admin creates GET request with valid request body
When Admin sends GET HTTPS request to the valid endpoint for POST in Batch controller
Then Admin receives status code with error message


#@runnow
Scenario: Admin creates batch with a program name that does not match the associated program id
Given Admin creates a POST request with program name that does not match the associated program id
When Admin sends HTTPS request to the endpoint for POST in Batch controller
Then Admin receives status code and response body contains the program details corresponding to the provided program id

#@runnow
Scenario: Admin creates batch with mandatory and optional fields
Given Admin creates POST request with mandatory and optional fields
When Admin sends HTTPS request to the endpoint for POST in Batch controller
Then Admin receives status code with response body

@runnow
Scenario: Admin creates batch with only mandatory fields
Given Admin creates POST request with only mandatory fields in request body
When Admin sends HTTPS request to the endpoint for POST in Batch controller
Then Admin receives status code with response body
