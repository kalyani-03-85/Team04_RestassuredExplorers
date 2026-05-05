@Resetpassword
Feature: Admin resets password

  Rule: Valid and invalid input handling

    Background:
       Admin sets reset token to Bearer Token
@runnow
    Scenario Outline: Reset password with invalid inputs
      Given Admin creates POST Request for "<testCase>"
      When Admin sends a HTTPS request to the endpoint
      Then Admin receives "<statusCode>" with the message "<message>"

    Examples:
      | testCase                                 | statusCode | message                |
      | Reset password with invalid email        | 400        | Bad Request            |
      | Reset password with invalid password     | 400        | Bad Request            |
      | Reset password with invalid endpoint     | 404        | Not Found              |
      | Reset password with invalid content type | 415        | Unsupported Media Type |
      | Reset password with invalid method       | 405        | Method Not Allowed     |
      | Reset password with valid credentials    | 200        | Password saved         |

  #Rule: No authentication
#
    #Background:
      #Given Admin sets No Auth
#
    #Scenario Outline: Reset password without authentication
      #Given Admin creates POST Request for "<testCase>"
      #When Admin sends a HTTPS request to the endpoint
      #Then Admin receives "<statusCode>" with the message "<message>"
#
    #Examples:
      #| testCase                        | statusCode | message      |
      #| Reset password with no auth    | 401        | Unauthorized |
#
  #Rule: Expired token
#
    #Background:
      #Given Admin sets expired token
#
    #Scenario Outline: Reset password with expired token
      #Given Admin creates POST Request for "<testCase>"
      #When Admin sends a HTTPS request to the endpoint
      #Then Admin receives "<statusCode>" with the message "<message>"
#
    #Examples:
      #| testCase                              | statusCode | message      |
      #| Reset password with expired token     | 401        | Unauthorized |
#
  #Rule: Empty token
#
    #Background:
      #Given Admin sets empty token
#
    #Scenario Outline: Reset password with empty token
      #Given Admin creates POST Request for "<testCase>"
      #When Admin sends a HTTPS request to the endpoint
      #Then Admin receives "<statusCode>" with the message "<message>"
#
    #Examples:
      #| testCase                            | statusCode | message      |
      #| Reset password with empty token     | 401        | Unauthorized |
#
  #Rule: Different users token
#
    #Background:
      #Given Admin sets different user's token
#
    #Scenario Outline: Reset password with different user's token
      #Given Admin creates POST Request for "<testCase>"
      #When Admin sends a HTTPS request to the endpoint
      #Then Admin receives "<statusCode>" with the message "<message>"
#
    #Examples:
      #| testCase                                  | statusCode | message      |
      #| Reset password with other user token      | 401        | Unauthorized |