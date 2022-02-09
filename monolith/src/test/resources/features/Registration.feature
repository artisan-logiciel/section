#language: fr
Fonctionnalité: Inscription d'un utilisateur.

  Contexte:
    Etant donné une liste d'accounts
      | login | email          | firstName | lastName |
      | admin | admin@acme.com | admin     | admin    |
      | user  | user@acme.com  | user      | user     |
      | test1 | test1@acme.com | test1     | test1    |
      | test2 | test2@acme.com | test2     | test2    |
      | test3 | test3@acme.com | test3     | test3    |

  Scénario: Inscription d'un compte utilisateur.
    Etant donné un utilisateur qui a pour login "user"
    Quand on inscrit "user"
    Alors le resultat est un nouveau compte