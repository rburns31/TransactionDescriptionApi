Feature: Call the Transaction Description API and validate all the responses

  In order to validate the responses from the Transaction Description API
  As a tester
  I want to create a Cucumber script

  Scenario Outline: Make a valid Transaction Description API read call
    Given The client can access the Transaction Description API
    When a HTTP read request is made with the URI "<uri>"
    Then the HTTP response code will be "<code>"
    Examples:
      | uri                                               | code |
      | http://localhost:8080/TransDescripAPI/e/600       | 200  |
      | http://localhost:8080/TransDescripAPI/f/600       | 200  |
      | http://localhost:8080/TransDescripAPI/F/600       | 200  |
      | http://localhost:8080/TransDescripAPI/French/600  | 200  |
      | http://localhost:8080/TransDescripAPI/french/600  | 200  |
      | http://localhost:8080/TransDescripAPI/english/600 | 200  |
      | http://localhost:8080/TransDescripAPI/eng/600     | 400  |
      | http://localhost:8080/TransDescripAPI/g/600       | 400  |
      | http://localhost:8080/TransDescripAPI/1/600       | 400  |
      | http://localhost:8080/TransDescripAPI/f/6001      | 400  |
      | http://localhost:8080/TransDescripAPI/f/600x      | 400  |
      | http://localhost:8080/TransDescripAPI/r/6001      | 400  |
      | http://localhost:8080/TransDescripAPI/read/e/600  | 404  |

  Scenario Outline: Make a valid Transaction Description API read call
    Given The client can access the Transaction Description API
    When a HTTP read request is made with the URI "<uri>"
    Then the transaction description response will be "<tDescription>"
    Examples:
      | uri                                               | tDescription               |
      | http://localhost:8080/TransDescripAPI/e/600       | INTEREST CREDIT ADJUSTMENT |
      | http://localhost:8080/TransDescripAPI/f/600       | RAJUSTEMENT - INTERETS     |
      | http://localhost:8080/TransDescripAPI/F/600       | RAJUSTEMENT - INTERETS     |
      | http://localhost:8080/TransDescripAPI/French/600  | RAJUSTEMENT - INTERETS     |
      | http://localhost:8080/TransDescripAPI/french/600  | RAJUSTEMENT - INTERETS     |
      | http://localhost:8080/TransDescripAPI/english/600 | INTEREST CREDIT ADJUSTMENT |
      | http://localhost:8080/TransDescripAPI/eng/600     |                            |
      | http://localhost:8080/TransDescripAPI/g/600       |                            |
      | http://localhost:8080/TransDescripAPI/1/600       |                            |
      | http://localhost:8080/TransDescripAPI/f/6001      |                            |
      | http://localhost:8080/TransDescripAPI/f/600x      |                            |
      | http://localhost:8080/TransDescripAPI/r/6001      |                            |
      | http://localhost:8080/TransDescripAPI/read/e/600  |                            |

  Scenario Outline: Make a valid Transaction Description API create call
    Given The client can access the Transaction Description API
    When a HTTP create request is made with the URI "<uri>"
    Then the HTTP response code will be "<code>"
    Examples:
      | uri                                                      | code |
      | http://localhost:8080/TransDescripAPI/create/e/601/teste | 201  |
      | http://localhost:8080/TransDescripAPI/create/f/601/testf | 201  |
      | http://localhost:8080/TransDescripAPI/create/f/601/test2 | 409  |

  Scenario Outline: Make a valid Transaction Description API update call
    Given The client can access the Transaction Description API
    When a HTTP update request is made with the URI "<uri>"
    Then the HTTP response code will be "<code>"
    Examples:
      | uri                                                       | code |
      | http://localhost:8080/TransDescripAPI/update/e/601/teste2 | 200  |
      | http://localhost:8080/TransDescripAPI/update/f/601/testf2 | 200  |
      | http://localhost:8080/TransDescripAPI/update/f/701/testf2 | 400  |

  Scenario Outline: Make a valid Transaction Description API delete call
    Given The client can access the Transaction Description API
    When a HTTP delete request is made with the URI "<uri>"
    Then the HTTP response code will be "<code>"
    Examples:
      | uri                                                | code |
      | http://localhost:8080/TransDescripAPI/delete/e/601 | 200  |
      | http://localhost:8080/TransDescripAPI/delete/f/601 | 200  |
      | http://localhost:8080/TransDescripAPI/delete/f/701 | 400  |