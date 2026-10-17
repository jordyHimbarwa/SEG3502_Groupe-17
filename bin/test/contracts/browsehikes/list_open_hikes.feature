# language: fr
Fonctionnalité: BrowseHikes.listOpenHikes
  Afin de trouver une randonnée qui me convient
  En tant que visiteur ou utilisateur enregistré
  Je veux lister toutes les randonnées ouvertes triées par date

  Contexte:
    Étant donné les randonnées suivantes :
      | titre           | sentier          | date       | statut   | capacité | confirmés |
      | Lac Pink        | Sentier Lac-Pink | 2026-11-08 | ouverte  | 10       | 3         |
      | Mer Bleue       | Sentier 53       | 2026-11-01 | ouverte  | 8        | 0         |
      | Luskville       | Sentier 1        | 2026-10-25 | complète | 5        | 5         |
      | King Mountain   | Sentier King     | 2026-10-20 | annulée  | 6        | 0         |

  Scénario: Un visiteur sans compte liste les randonnées ouvertes
    Quand un visiteur liste les randonnées ouvertes
    Alors le résultat contient exactement "Mer Bleue", "Lac Pink" dans cet ordre
    Et chaque randonnée affiche ses informations, son organisateur et le nombre de places restantes
    Et la randonnée "Lac Pink" affiche 7 places restantes

  Scénario: Aucune randonnée ouverte
    Étant donné qu'aucune randonnée n'est ouverte
    Quand un visiteur liste les randonnées ouvertes
    Alors le résultat est vide
