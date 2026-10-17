# language: fr
Fonctionnalité: CancelHike.cancelHike
  Afin de prévenir les participants lorsqu'une randonnée ne peut pas avoir lieu
  En tant qu'organisateur de la randonnée ou administrateur
  Je veux annuler une randonnée avant son début en indiquant un motif

  Contexte:
    Étant donné une randonnée "Lac Pink" organisée par "org@exemple.ca" qui n'a pas commencé
    Et les inscriptions suivantes à "Lac Pink" :
      | randonneur      | statut             |
      | ana@exemple.ca  | confirmée          |
      | bob@exemple.ca  | en liste d'attente |
      | cleo@exemple.ca | annulée            |

  Plan du scénario: Annulation autorisée
    Étant donné que "<utilisateur>" est connecté en tant que <rôle>
    Quand "<utilisateur>" annule la randonnée "Lac Pink" pour le motif "Sentier fermé par la CCN"
    Alors la randonnée "Lac Pink" est "annulée"
    Et l'étape "annulée" est consignée avec l'utilisateur "<utilisateur>"
    Et les inscriptions de "ana@exemple.ca" et "bob@exemple.ca" sont "annulées"
    Et un courriel contenant le motif "Sentier fermé par la CCN" est envoyé à "ana@exemple.ca" et "bob@exemple.ca"
    Et aucun courriel n'est envoyé à "cleo@exemple.ca"

    Exemples:
      | utilisateur      | rôle                       |
      | org@exemple.ca   | organisateur de la randonnée |
      | admin@exemple.ca | administrateur             |

  Scénario: Motif obligatoire
    Étant donné que "org@exemple.ca" est connecté en tant qu'organisateur de la randonnée
    Quand "org@exemple.ca" annule la randonnée "Lac Pink" sans motif
    Alors l'annulation est refusée avec le motif "motif requis"

  Scénario: Un autre organisateur ne peut pas annuler la randonnée
    Étant donné que "autre@exemple.ca" est connecté en tant qu'organisateur d'autres randonnées
    Quand "autre@exemple.ca" annule la randonnée "Lac Pink" pour le motif "Raison quelconque"
    Alors l'annulation est refusée pour défaut d'autorisation

  Scénario: Annulation après le début refusée
    Étant donné que la randonnée "Lac Pink" a commencé
    Quand "org@exemple.ca" annule la randonnée "Lac Pink" pour le motif "Orage"
    Alors l'annulation est refusée avec le motif "randonnée déjà commencée"
