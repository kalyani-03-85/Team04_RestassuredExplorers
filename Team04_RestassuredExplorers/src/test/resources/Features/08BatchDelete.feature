@batchDelete
Feature: Batch Delete Request

Background: Admin sets valid Auth
Given Admin sets valid authorization

@runnow
Scenario: Admin deletes a batch with valid batch id
Given Admin creates DELETE request with valid BatchId
When Admin sends Delete HTTPS request to the endpoint 	
Then Admin receives Ok status with message

Scenario: Admin deletes a batch with invalid batch id
Given Admin creates DELETE request with invalid BatchId	
When Admin sends Delete HTTPS request to the endpoint
Then Admin receives status code with error message

Scenario: Admin deletes a batch by batch id with invalid endpoint
Given Admin creates DELETE request with valid BatchId	
When Admin sends Delete HTTPS request to the invalid endpoint	
Then Admin receives status code with error message

Scenario: Admin deletes a batch by batch id with invalid method
Given Admin creates POST request with valid BatchId	
When Admin sends a POST HTTPS request to the valid endpoint	
Then Admin receives status code with error message

Scenario: Admin deletes a batch by batch id with invalid content type
Given Admin creates DELETE request with invalid content type	
When Admin sends Delete HTTPS request to the valid endpoint with invalid content type
Then Admin receives status code with error message
