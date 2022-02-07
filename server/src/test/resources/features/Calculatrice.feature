Feature: Calculatrice avec addition et soustraction

  Scenario: Addition de deux entiers
    Given un entier 1
    And un second entier 2
    When on additionne les nombres
    Then le resultat est 3

  Scenario: Soustraction d'un nombre à un autre
    Given un entier 2
    And un second entier 1
    When on soustrait un nombre à l'autre
    Then le resultat est 1
