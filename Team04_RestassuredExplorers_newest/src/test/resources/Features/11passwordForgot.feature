@forgotpassword
Feature: Forgot Password Feature

  Scenario: Admin requests password reset with invalid content type
    Given Admin creates POST request with invalid content type
    When Admin sends a HTTPS request to the valid endpoint
    Then Admin receives 415 unsupported media type

  Scenario: Admin requests password reset with invalid method
    Given Admin creates GET request with valid credential
    When Admin sends a HTTPS request to the valid endpoint
    Then Admin receives 405 method not allowed

  Scenario: Admin requests password reset with invalid endpoint
    Given Admin creates POST request with valid credential
    When Admin sends a HTTPS request to the invalid endpoint
    Then Admin receives 404 Not found

  Scenario: Admin requests password reset with empty email
    Given Admin creates POST request with empty email
    When Admin sends a HTTPS request to the valid endpoint
    Then Admin receives 400 Bad request with valid error message

  Scenario: Admin requests password reset with invalid email
    Given Admin creates POST request with invalid email
    When Admin sends a HTTPS request to the valid endpoint
    Then Admin receives 400 Bad request with valid error message

  Scenario: Admin requests password reset with null in email field
    Given Admin creates POST request with null in email
    When Admin sends a HTTPS request to the valid endpoint
    Then Admin receives 400 Bad request with valid error message

  Scenario: Admin requests password reset with unregistered email
    Given Admin creates POST request with unregistered email
    When Admin sends a HTTPS request to the valid endpoint
    Then Admin receives 400 Bad request with valid error message

      Scenario: Admin requests password reset with valid email
    Given Admin creates POST request with valid credential
    When Admin sends a HTTPS request to the valid endpoint
    Then Admin receives 201 created with auto generated token