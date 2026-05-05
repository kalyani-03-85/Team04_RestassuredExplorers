@programPost
Feature: Program module

Background:
  Admin sets Authorization to Bearer Token
  
@runnow
Scenario Outline: Admin creating program with different test data
  Given Admin creates POST Request with valid requestbody "<testCase>"
  When Admin sends a HTTPS POST request to the endpoint
  Then Admin receives statuscode "<statusCode>" with message "<message>"

Examples:
  | testCase                                               | statusCode | message |
  | valid request body                                     | 201        | Created |
  | only mandatory field                                   | 201        | Created |
  | program description length between 4 and 25 characters | 201        | Created |
  | program name length between 4 and 25 characters        | 201        | Created |


Scenario Outline: Admin creates a program with invalid requests
  Given Admin creates POST Request with valid requestbody "<testCase>"
  When Admin sends a HTTPS POST request to the endpoint
  Then Admin receives statuscode "<statusCode>" with message "<message>"

Examples:
  | testCase                     | statusCode | message                     |
  | invalid token                | 401        | Unauthorized                |
  | program with invalid endpoint| 404        | Invalid endpoint            |
  | invalid content              | 415        | Content-Type not supported. Please use application/json |
  | program with invalid method  | 405        | Request method 'GET' is not supported |


Scenario Outline: Admin creates program with various inputs
  Given Admin creates POST Request with valid requestbody "<testCase>"
  When Admin sends a HTTPS POST request to the endpoint
  Then Admin receives statuscode "<statusCode>" with message "<message>"

Examples:
  | testCase                          | statusCode | message                                                                 |
  | existing program name             | 400        | cannot create program , since already exists                             |
  | trailing space in program name    | 400        | programName Must contain only letters and sometimes hyphens              |
  | only numbers in program name      | 400        | programName Must contain only letters and sometimes hyphens              |
  | program with invalid status       | 400        | Invalid Status: must be Active or Inactive                               |
  | missing program name              | 400        | Program Name is mandatory                                                |
  | program name too short            | 400        | Program Name must be of min length 4 and max length 25.                  |
  | empty payload                     | 400        | Required request body is missing                                         |
  | special char in program description | 400      | programDescription must begin with letter and can only have letters, numbers, comma, hyphen, colon, period, underscore and space |
  | program description too long      | 400        | Program Description must be of min length 4 and max length 25.           |
