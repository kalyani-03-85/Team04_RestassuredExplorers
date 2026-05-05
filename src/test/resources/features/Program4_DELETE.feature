@delete
Feature: Program Module - Delete Program

  Background:
    Admin sets Authorization to Bearer Token

  Scenario Outline: Admin deletes a program using programName
    Given Admin creates DELETE Request for "<testCase>"
    When Admin sends a DELETE request to the endpoint
    Then Admin receives "<statusCode>" with message "<message>"

    Examples:
      | testCase                                   | statusCode | message                     |
      | deletes a program with valid programName        | 200 | deleted successfully        |
      | deletes a program with non existing programName | 404 | not found                   |
      | deletes a program with invalid endpoint         | 404 | Invalid endpoint            |
      | deletes a program with invalid method           | 405 | Request method 'POST' is not supported |
      | delete program by programname with no auth         | 401 | Unauthorized                |

  Scenario Outline: Admin deletes a program using programId
    Given Admin creates DELETE Request for "<testCase>"
    When Admin sends a DELETE request to the endpoint
    Then Admin receives "<statusCode>" with message "<message>"

    Examples:
      | testCase                                   | statusCode | message                     |
      | deletes a program with valid program ID         | 200 | deleted successfully        |
      | deletes a program with alphabets in program ID  | 404 | Invalid endpoint                   |
      | deletes a program with invalid endpoint         | 404 | Invalid endpoint            |
      | deletes a program with invalid method           | 405 | Request method 'POST' is not supported |
      | delete program by programid with no auth        | 401 | Unauthorized                |
