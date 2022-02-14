#language: fr
Fonctionnalité: Inscription d'un utilisateur.

  Contexte:
    Etant donné une liste d'accounts
      | login | email          | password | firstName | lastName |
      | admin | admin@acme.com | admin    | admin     | admin    |
      | user  | user@acme.com  | user     | user      | user     |
      | test1 | test1@acme.com | test1    | test1     | test1    |
      | test2 | test2@acme.com | test2    | test2     | test2    |
      | test3 | test3@acme.com | test3    | test3     | test3    |

  Scénario: Inscription d'un compte utilisateur.
    Etant donné un utilisateur qui à pour login "user"
    Quand on inscrit "user"
    Alors le résultat est un nouveau compte