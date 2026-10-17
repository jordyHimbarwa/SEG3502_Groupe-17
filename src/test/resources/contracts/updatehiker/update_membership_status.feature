# language: fr
Fonctionnalité: UpdateHiker.updateMembershipStatus
  Afin de refléter la situation d'adhésion d'un membre
  En tant qu'administrateur
  Je veux mettre à jour le statut d'adhésion d'un randonneur

  Plan du scénario: Changement de statut par un administrateur
    Étant donné un administrateur connecté
    Et un randonneur "ana@exemple.ca" avec une adhésion "<avant>"
    Quand l'administrateur met le statut d'adhésion de "ana@exemple.ca" à "<après>"
    Alors l'adhésion de "ana@exemple.ca" est "<après>"

    Exemples:
      | avant     | après     |
      | active    | suspendue |
      | active    | expirée   |
      | suspendue | active    |

  Scénario: Un randonneur ne peut pas changer un statut d'adhésion
    Étant donné un randonneur connecté "bob@exemple.ca"
    Et un randonneur "ana@exemple.ca" avec une adhésion "suspendue"
    Quand "bob@exemple.ca" met le statut d'adhésion de "ana@exemple.ca" à "active"
    Alors la mise à jour est refusée pour défaut d'autorisation
