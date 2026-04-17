Feature: Gestion des livres

  Scenario: Ajouter un livre et le retrouver dans la liste
    Given l'utilisateur crée le livre "Clean Code" de "Robert C. Martin"
    When l'utilisateur récupère la liste des livres
    Then la liste contient les livres suivants
      | title      | author          |
      | Clean Code | Robert C. Martin |

  Scenario: Ajouter plusieurs livres et les retrouver triés alphabétiquement
    Given l'utilisateur crée le livre "Refactoring" de "Martin Fowler"
    And l'utilisateur crée le livre "Algorithmes" de "Cormen"
    When l'utilisateur récupère la liste des livres
    Then la liste contient les livres suivants
      | title       | author        |
      | Algorithmes | Cormen        |
      | Refactoring | Martin Fowler |

  Scenario: Réserver un livre disponible
    Given l'utilisateur crée le livre "Clean Code" de "Robert C. Martin"
    When l'utilisateur réserve le livre "Clean Code"
    Then le livre "Clean Code" est marqué comme non disponible

  Scenario: Réserver un livre déjà réservé retourne une erreur
    Given l'utilisateur crée le livre "Clean Code" de "Robert C. Martin"
    And l'utilisateur réserve le livre "Clean Code"
    When l'utilisateur réserve le livre "Clean Code"
    Then la réservation échoue avec une erreur 400