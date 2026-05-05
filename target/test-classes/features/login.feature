@login
Feature: Admin login

   Background: Admin sets No Auth
     
     Scenario: Admin generates token with valid credentials
      Given Admin creates POST request with valid credentials
      When Admin sends a HTTPS request to the valid endpoint
      Then Admin receives status code with auto generated token














