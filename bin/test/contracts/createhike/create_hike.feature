# language: fr
Fonctionnalité: CreateHike.createHike
  Afin d'offrir une randonnée aux membres du club
  En tant qu'organisateur de randonnée
  Je veux créer une randonnée sur un sentier de la CCN

  Contexte:
    Étant donné les sentiers de la CCN suivants :
      | identifiant | nom              | difficulté | saison       | randonnée permise |
      | T-101       | Sentier Lac-Pink | facile     | toute l'année | oui              |
      | T-202       | Sentier Wolf     | difficile  | été          | oui              |
      | T-303       | Promenade 9      | facile     | toute l'année | non              |
    Et un organisateur connecté "org@exemple.ca" de niveau d'organisateur "difficile" dont la certification expire le "2027-06-30"

  Scénario: Création réussie
    Quand "org@exemple.ca" crée la randonnée "Lac Pink en automne" sur le sentier "T-101" le "2026-11-08" à 9 h, d'une durée de 3 h, avec une date limite d'inscription le "2026-11-01" et une capacité de 12
    Alors la randonnée reçoit un identifiant unique
    Et la randonnée est "ouverte" avec la difficulté "facile"
    Et l'étape "ouverte" est consignée avec l'utilisateur "org@exemple.ca"
    Et les randonneurs peuvent s'y inscrire

  Plan du scénario: Création refusée
    Quand "org@exemple.ca" crée une randonnée sur le sentier "<sentier>" le "<date>" avec une date limite d'inscription le "<limite>" et une capacité de <capacité>
    Alors la création est refusée avec le motif "<motif>"
    Et aucune randonnée n'est créée

    Exemples:
      | sentier | date       | limite     | capacité | motif                                         |
      | T-999   | 2026-11-08 | 2026-11-01 | 12       | sentier inconnu de la CCN                     |
      | T-303   | 2026-11-08 | 2026-11-01 | 12       | randonnée non permise sur ce sentier          |
      | T-202   | 2026-12-13 | 2026-12-06 | 12       | date hors de la saison d'ouverture du sentier |
      | T-101   | 2026-11-08 | 2026-10-20 | 12       | date limite plus de 2 semaines avant la date  |
      | T-101   | 2026-11-08 | 2026-11-09 | 12       | date limite après la date de la randonnée     |
      | T-101   | 2027-08-01 | 2027-07-25 | 12       | certification expirée à la date de la randonnée |
      | T-101   | 2026-11-08 | 2026-11-01 | 0        | capacité invalide                             |

  Scénario: Niveau d'organisateur insuffisant
    Étant donné un organisateur connecté "jun@exemple.ca" de niveau d'organisateur "modéré"
    Quand "jun@exemple.ca" crée une randonnée sur le sentier "T-202" le "2026-07-12" avec une date limite d'inscription le "2026-07-05"
    Alors la création est refusée avec le motif "niveau d'organisateur inférieur à la difficulté du sentier"

  Scénario: Un randonneur non organisateur ne peut pas créer de randonnée
    Étant donné un randonneur connecté "ana@exemple.ca" sans certification
    Quand "ana@exemple.ca" crée une randonnée sur le sentier "T-101"
    Alors la création est refusée pour défaut d'autorisation

  Scénario: Données de la CCN indisponibles
    Étant donné que le service de la CCN est indisponible
    Et que le sentier "T-101" figure dans la copie locale des sentiers de la CCN
    Quand "org@exemple.ca" crée une randonnée valide sur le sentier "T-101"
    Alors la randonnée est créée à partir de la copie locale des sentiers
