#language: fr
Fonctionnalité: Inscription d'un compte utilisateur.

  Contexte:
    Etant donné une liste de login, email, password, firstName, lastName

      | login | email          | password | firstName | lastName |
      | admin | admin@acme.com | admin    | admin     | admin    |
      | user  | user@acme.com  | user     | user      | user     |
      | test1 | test1@acme.com | test1    | test1     | test1    |
      | test2 | test2@acme.com | test2    | test2     | test2    |
      | test3 | test3@acme.com | test3    | test3     | test3    |

  Scénario: Inscription d'un compte utilisateur.
    Etant donné l'utilisateur qui à pour login "user"
    Quand on envoie l'inscription de "user"
    Alors le résultat est la création d'un nouveau compte non activé