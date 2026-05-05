@batchUpdate
Feature: Batch Update

Background: Admin sets valid Auth
Given Admin sets valid authorization

@runnow
  Scenario: Admin updates a batch with valid batch ID
    Given Admin creates PUT request with valid BatchId
    When Admin sends PUT HTTPS request to the endpoint
Then Admin receives status code with response body    

@runnow    
 Scenario: Admin updates a batch with invalid batch ID
    Given Admin creates PUT request with Invalid batch id
    When Admin sends PUT HTTPS request to the endpoint
    Then Admin receives Status Code with message "Batch not found with Id"

@runnow
  Scenario: Admin updates a batch by batch id with missing mandatory fields in request body
    Given Admin creates PUT request with out mandatory fileds
    When Admin sends PUT HTTPS request to the endpoint
    Then Admin receives Status Code with message "No of Classes is needed; It should be a positive number"
    
@runnow
  Scenario: Admin updates a batch by batch id with duplicate batch name
    Given Admin creates PUT request with duplicate batchname
    When Admin sends PUT HTTPS request to the endpoint
Then Admin receives Status Code with message "batchName Must be in the format of ProgramName_<number>"

@runnow
  Scenario: Admin updates a batch by batch id with invalid batch name format
    Given Admin creates PUT request with valid BatchId
    When Admin sends PUT HTTPS request to the endpoint
  Then Admin receives Status Code with message "batchName Must be in the format of ProgramName_<number>"


  Scenario: Admin updates a batch by batch id with batch name more than allowable length
    Given Admin creates PUT request with valid BatchId
    When Admin sends PUT HTTPS request to the endpoint
  Then Admin receives Status Code with message "Batch Name must be of min length 6 and max length 28 characters."

  Scenario: Admin updates a batch by batch id with batch name less than allowable length in total
  Given Admin creates PUT request with valid BatchId
    When Admin sends PUT HTTPS request to the endpoint
    Then Admin receives Status Code with message "Batch Name must be of min length 6 and max length 28 characters."

  Scenario: Admin updates a batch by batch id with invalid batch description
      Given Admin creates PUT request with valid BatchId
    When Admin sends PUT HTTPS request to the endpoint
    Then Admin receives Status Code with message "batchDescription must begin with letter and can only have letters, numbers, comma, hyphen, colon, period, underscore and space"

  Scenario: Admin updates a batch by batch id with invalid batch status
      Given Admin creates PUT request with valid BatchId
    When Admin sends PUT HTTPS request to the endpoint
    Then Admin receives Status Code with message "Invalid Status: must be Active or Inactive"

  Scenario: Admin updates a batch by batch id with invalid batch number of classes
      Given Admin creates PUT request with valid BatchId
    When Admin sends PUT HTTPS request to the endpoint
    Then Admin receives Status Code with message "No of classes should be a 1 digit or 2 digit number. Cannot exceed 2 digits"

  Scenario: Admin updates a batch by batch id with invalid program id
   Given Admin creates a PUT request with invalid program id
    When Admin sends PUT HTTPS request to the endpoint
    Then Admin receives status code with error message

  Scenario: Admin updates a batch by batch id with special characters in program name
      Given Admin creates PUT request with valid BatchId
    When Admin sends PUT HTTPS request to the endpoint
   Then Admin receives Status Code with message "programName must begin with alphabet and can contain only alphanumeric characters"

  Scenario: Admin updates a batch by batch id with numbers in program name
      Given Admin creates PUT request with valid BatchId
    When Admin sends PUT HTTPS request to the endpoint
   Then Admin receives Status Code with message "programName must begin with alphabet and can contain only alphanumeric characters"

  Scenario: Admin updates a batch by batch id with inactive program name 
    Given Admin creates PUT request with inactive program
    When Admin sends PUT HTTPS request to the endpoint
   Then Admin receives status code with error message

  Scenario: Admin updates a batch by batch id with invalid endpoint
   Given Admin creates PUT request with valid BatchId
   When Admin sends HTTPS PUT request to the invalid endpoint
   Then Admin receives status code with error message

  Scenario: Admin updates a batch by batch id with invalid method
    Given Admin creates POST request with valid request body
    When Admin sends POST HTTPS request to the endpoint with invalid method
    Then Admin receives status code with error message

  Scenario: Admin updates a batch by batch id with invalid content type
    Given Admin creates PUT request with invalid content type
    When Admin sends PUT HTTPS request to the endpoint with invalid content type
Then Admin receives status code with error message

@runnow
Scenario: Admin updates a batch by batch id with a program name that does not match the associated program id
Given Admin creates a PUT request with program name that does not match the associated program id
    When Admin sends PUT HTTPS request to the endpoint
Then Admin receives status code and response body contains the program details corresponding to the provided program id
