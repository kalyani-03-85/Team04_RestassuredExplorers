@usersignin
Feature: User sign-in

@loginnegative
  Scenario: Admin generates token with invalid method
    Given Admin creates GET request with valid credentials
    When Admin sends a HTTPS request to the valid endpoint
    Then Admin receives status code
@loginnegative
  Scenario: Admin generates token with invalid base URL
    Given Admin creates POST request with invalid base URL
    When Admin sends a HTTPS request to the valid endpoint with invalid url
    Then Admin receives status code
@loginnegative
  Scenario: Admin generates token with invalid content type
    Given Admin creates POST request with invalid content type
    When Admin sends a HTTPS request to the valid endpoint with invalid content type
    Then Admin receives status code
@loginnegative
  Scenario: Admin generates token with invalid endpoint
    Given Admin creates POST request with valid credentials
    When Admin sends a HTTPS request to the invalid endpoint
    Then Admin receives status code
@loginnegative
  Scenario: Admin generates token with empty email
    Given Admin creates POST request with empty email
    When Admin sends a HTTPS request to the valid endpoint
    Then Admin receives status code with message "EmailId is mandatory"
@loginnegative
  Scenario: Admin generates token with special characters in email
    Given Admin creates POST request with special characters in email
    When Admin sends a HTTPS request to the valid endpoint
    Then Admin receives status code with message "Email Id Not Found"
@loginnegative
  Scenario: Admin generates token with email having spaces
    Given Admin creates POST request with email containing spaces
    When Admin sends a HTTPS request to the valid endpoint
    Then Admin receives status code with message "Email Id Not Found"
@loginnegative
  Scenario: Admin generates token with null in email field
    Given Admin creates POST request with null in email
    When Admin sends a HTTPS request to the valid endpoint
    Then Admin receives status code
@loginnegative
  Scenario: Admin generates token for unregistered email
    Given Admin creates POST request with unregistered email
    When Admin sends a HTTPS request to the valid endpoint
    Then Admin receives status code with message "Email Id Not Found"
@loginnegative
  Scenario: Admin generates token with empty password
    Given Admin creates POST request with empty password
    When Admin sends a HTTPS request to the valid endpoint
    Then Admin receives status code with message "Password is mandatory"
@loginnegative
  Scenario: Admin generates token with special characters inserted in password
    Given Admin creates POST request with special characters inserted in password
    When Admin sends a HTTPS request to the valid endpoint
    Then Admin receives status code with message "Bad Credentials" and false success
@loginnegative
  Scenario: Admin generates token with password having spaces
    Given Admin creates POST request with password containing spaces
    When Admin sends a HTTPS request to the valid endpoint
    Then Admin receives status code with message "password must contain atleast 8 to max 32 characters, a capital letter, a small letter, a special character, a number and no space"
@loginnegative
  Scenario: Admin generates token with null in password field
    Given Admin creates POST request with null in password
    When Admin sends a HTTPS request to the valid endpoint
    Then Admin receives status code with message "Password is mandatory"
@loginnegative
  Scenario: Admin generates for inactive user
    Given Admin creates POST request with inactive user credentials
    When Admin sends a HTTPS request to the valid endpoint
    Then Admin receives status code with message "Email Id Not Found"
@loginnegative
  Scenario: Admin generates token without request body
    Given Admin creates POST request without request body
    When Admin sends a HTTPS request to the valid endpoint without request body
    Then Admin receives status code
@runnow @logintoken
  Scenario: Admin generates token with valid credential
    Given Admin creates POST request with valid credentials
    When Admin sends a HTTPS request to the valid endpoint
    Then Admin receives status code with auto generated token

    