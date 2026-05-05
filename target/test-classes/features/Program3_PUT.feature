@putoperations
Feature: Program Module - Update Program (PUT)

  Background:
     Admin sets Authorization to Bearer Token
     
  Scenario Outline: Admin updates a program using programid
    Given Admin updates PUT Request for "<testCase>"
    When Admin sends a PUT request to the endpoint
    Then Admin receive statuscode "<statusCode>" with Message "<message>"

    Examples:
      | testCase                         | statusCode         | message                       |
      | valid program id                 | 200                |          |
      | invalid program id               | 404                | not found                 |
      | already existing program name    | 400                | Program Name JavaBasics already exists          |
      | without request body             | 400                | Required request body is missing              |
      | invalid baseURI                  | 404                |                           |
      | invalid method                   | 405                | Request method 'PATCH' is not supported                   |
      | invalid endpoint                 | 404                | Invalid endpoint                  |
      
   Scenario Outline: Admin updates a program using programname
     Given Admin updates PUT Request for "<testCase>"
     When Admin sends a PUT request to the endpoint
     Then  Admin receive statuscode "<statusCode>" with Message "<message>"

   Examples:
     | testCase                                    | statusCode         | message                      |
     | valid program name                          | 200                |                              |
     | invalid program name                        | 404                | no such program name                                                                |
     | missing mandatory fields                    | 400                | Program Name is mandatory                    |                                
     | invalid status                              | 400                | Invalid Status: must be Active or Inactive                                      |
     | special char program description            | 400                | programDescription must begin with letter and can only have letters, numbers, comma, hyphen, colon, period, underscore and space  |
     | valid status                                | 200                |                                                                                 |

