@Logout
Feature: Admin Logout API validation

  Background:
  Admin sets Authorization to Bearer Token

Scenario Outline: Validate logout with different authentication states
  Given Admin creates GET Request for scenario "<testCase>"
  When Admin sends a HTTPS request with endpoint
  Then Admin receives "<statusCode>" and "<message>"
  
  Examples:
      | testCase                  | statusCode | message           |
      | logout with no auth       | 401        | Unauthorized      |
      | logout with invalid token | 401        | Unauthorized      |
      | logout with expired token | 401        | Unauthorized      |
      | logout with invalid endpoint | 404        | Invalid endpoint                               |
      | logout with invalid method   | 405        | Request method 'POST' is not supported         |
      | logout with valid token   | 200        | Logout successful |

