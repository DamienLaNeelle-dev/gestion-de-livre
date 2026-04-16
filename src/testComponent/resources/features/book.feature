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