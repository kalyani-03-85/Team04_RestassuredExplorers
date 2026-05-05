
Feature: Batch Controller GET

Background: Admin sets valid Auth
Given Admin sets valid authorization

Scenario: Admin retrieves all batches with invalid endpoint
Given Admin creates GET request
When Admin sends HTTPS request to the invalid endpoint
Then Admin receives status code with error message

Scenario: Admin retrieves all batches with invalid method
Given Admin creates POST request
When Admin sends HTTPS request to the endpoint
Then Admin receives status code with error message

Scenario: Admin retrieves all batches with invalid content type
Given Admin creates GET request with invalid content type
When Admin sends HTTPS request to the endpoint with invalid content type
Then Admin receives status code with error message

Scenario: Admin retrieves all batches with valid endpoint
Given Admin creates GET request
When Admin sends HTTPS request to the endpoint
Then Admin receives status code with response body

@runnow
Scenario: Admin retrieves a batch with valid batch ID
Given Admin creates GET request with valid Batch ID	
When Admin sends HTTPS request to the endpoint 	
Then Admin receives status code with response body

Scenario: Admin retrieves a batch with inactive batch ID
Given Admin creates GET request with inactive Batch ID
When Admin sends HTTPS request to the endpoint 	
Then Admin receives status code with response body

Scenario: Admin retrieves a batch with invalid batch ID
Given Admin creates GET request with invalid Batch ID	
When Admin sends HTTPS request to the endpoint 	
Then Admin receives status code with error message

Scenario: Admin retrieves a batch by batch id with invalid endpoint
Given Admin creates GET request 	
When Admin sends HTTPS request to the invalid endpoint 
Then Admin receives status code with error message

Scenario: Admin retrieves a batch by batch id with invalid method
Given Admin creates POST request with valid endpoint	
When Admin sends POST HTTPS request to the endpoint 	
Then Admin receives status code with error message

Scenario: Admin retrieves a batch by batch id with invalid content type
Given Admin creates GET request with invalid content type	
When Admin sends HTTPS request to the endpoint with invalid content type
Then Admin receives status code with error message
                                                         
Scenario: Admin retrieves a batch with invalid batch name
Given Admin creates GET request with invalid Batch Name
When Admin sends HTTPS request to the endpoint 	
Then Admin receives status code with error message

Scenario: Admin retrieves a batch by batch name with invalid endpoint
Given Admin creates GET request
When Admin sends HTTPS request to the invalid endpoint 
Then Admin receives status code with error message

Scenario: Admin retrieves a batch by batch name with invalid method
Given Admin creates POST request with valid endpoint	
When Admin sends POST HTTPS request to the endpoint 	
Then Admin receives status code with error message

Scenario: Admin retrieves a batch by batch name with invalid content type
Given Admin creates GET request with invalid content type	
When Admin sends HTTPS request to the endpoint with invalid content type
Then Admin receives status code with error message

@runnow
Scenario: Admin retrieves a batch with valid batch name
Given Admin creates GET request with valid Batch Name
When 	Admin sends HTTPS request to the endpoint
Then Admin receives status code with response body

Scenario: Admin retrieves a batch with invalid Program Id
Given Admin creates GET request with invalid Program Id
When 	Admin sends HTTPS request to the endpoint
Then Admin receives status code with error message

Scenario: Admin retrieves a batch by Program Id with invalid endpoint
Given Admin creates GET request 	
When Admin sends HTTPS request to the invalid endpoint
Then Admin receives status code with error message

Scenario: Admin retrieves a batch by Program Id with invalid method
Given Admin creates POST request
When 	Admin sends POST HTTPS request to the endpoint 	
Then Admin receives status code with error message

Scenario: Admin retrieves a batch by Program Id with invalid content type
Given Admin creates GET request with invalid content type 
When Admin sends HTTPS request to the endpoint with invalid content type
Then Admin receives status code with error message

Scenario: Admin retrieves a batch with valid Program Id
Given Admin creates GET request with valid Program Id
When 	Admin sends HTTPS request to the endpoint
Then Admin receives status code with response body

