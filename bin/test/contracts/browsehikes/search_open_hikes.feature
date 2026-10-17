# language: fr
Fonctionnalité: BrowseHikes.searchOpenHikes
  Afin de trouver rapidement une randonnée précise
  En tant que visiteur ou utilisateur enregistré
  Je veux rechercher les randonnées ouvertes par nom de sentier, date ou difficulté

  Contexte:
    Étant donné les randonnées suivantes :
      | titre      | sentier          | difficulté | date       | statut  |
      | Lac Pink   | Sentier Lac-Pink | facile     | 2026-11-08 | ouverte |
      | Wolf       | Sentier Wolf     | difficile  | 2026-11-08 | ouverte |
      | Wolf hiver | Sentier Wolf     | difficile  | 2026-12-13 | fermée  |

  Scénario: Recherche par nom de sentier
    Quand je recherche les randonnées ouvertes avec le nom de sentier "Wolf"
    Alors le résultat contient exactement "Wolf"

  Scénario: Recherche par date
    Quand je recherche les randonnées ouvertes à la date "2026-11-08"
    Alors le résultat contient exactement "Lac Pink", "Wolf"

  Scénario: Recherche par difficulté
    Quand je recherche les randonnées ouvertes de difficulté "facile"
    Alors le résultat contient exactement "Lac Pink"

  Scénario: Les critères se combinent
    Quand je recherche les randonnées ouvertes avec le nom de sentier "Wolf" et la difficulté "facile"
    Alors le résultat est vide

  Scénario: Une randonnée non ouverte n'est jamais retournée
    Quand je recherche les randonnées ouvertes à la date "2026-12-13"
    Alors le résultat est vide
