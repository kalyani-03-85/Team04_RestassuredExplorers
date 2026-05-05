@programGET
Feature: Program Module - GET Opearations

  Background:
     Admin sets Authorization to Bearer Token
  
  Scenario Outline: Admin retrieves all programs with various inputs
    Given Admin creates GET Request for "<testCase>"
    When Admin sends a GET request to the endpoint
    Then Admin receives "<statusCode>" with "<message>"

    Examples:
      | testCase                               | statusCode | message                                        |
      | get all programs with valid endpoint   | 200        | OK                                               |
      | get all programs with invalid endpoint | 404        | Invalid endpoint                               |
      | get all programs with invalid method   | 405        | Request method 'POST' is not supported         |

   Scenario Outline: Admin retrieves a program with various inputs
    Given Admin creates GET Request for "<testCase>"
    When Admin sends a GET request to the endpoint
    Then Admin receives "<statusCode>" with "<message>"

    Examples:
      | testCase                         | statusCode | message              |
      | get program with valid id        | 200        | OK                   |
      | get program with invalid id      | 404        | not found    |
      | get program with invalid baseuri | 404        | Not Found            |
      | get program with invalid endpoint| 404        | Invalid endpoint     |
      
    Scenario Outline: Admin retrieves all programs user with various inputs
    Given Admin creates GET Request for "<testCase>"
    When Admin sends a GET request to the endpoint
    Then Admin receives "<statusCode>" with "<message>"

    Examples:
      | testCase                                     | statusCode | message                                      |
      | get all programs user with valid endpoint    | 200        | OK                                           |
      | get all programs user with invalid endpoint  | 404        | Invalid endpoint                             |
      | get all programs user with invalid method    | 405        | Request method 'POST' is not supported       |