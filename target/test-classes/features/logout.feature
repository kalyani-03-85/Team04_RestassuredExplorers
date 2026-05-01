@Logout
Feature: Admin Logout API validation

  Rule: Authentication validation

    Scenario Outline: Validate logout with different authentication states
      Given Admin sets authorization to "<authType>"
      When Admin sends "GET" request to "valid endpoint"
      Then Admin validates status code <statusCode> with message "<message>"

      Examples:
        | authType      | statusCode | message           |
        | no auth       | 401        | Unauthorized      |
        | invalid token | 401        | Unauthorized      |
        | expired token | 401        | Unauthorized      |
        | valid token   | 200        | Logout successful |


  Rule: Endpoint validation

    Scenario Outline: Validate logout with different endpoints
      Given Admin sets authorization to "valid token"
      When Admin sends "GET" request to "<endpoint>"
      Then Admin validates status code <statusCode> with message "<message>"

      Examples:
        | endpoint         | statusCode | message        |
        | valid endpoint   | 200        | Logout success |
        | invalid endpoint | 404        | Not Found      |


  Rule: Method validation

    Scenario Outline: Validate logout with different HTTP methods
      Given Admin sets authorization to "valid token"
      When Admin sends "<method>" request to "valid endpoint"
      Then Admin validates status code <statusCode> with message "<message>"

      Examples:
        | method | statusCode | message              |
        | GET    | 200        | Logout successful    |
        | POST   | 405        | Method Not Allowed   |