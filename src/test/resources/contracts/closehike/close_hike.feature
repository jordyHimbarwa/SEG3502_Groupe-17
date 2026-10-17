# language: fr
Fonctionnalité: CloseHike.closeHike
  Afin que la liste des participants soit figée avant la randonnée
  En tant que système
  Je veux fermer une randonnée lorsque sa date limite d'inscription est atteinte

  Plan du scénario: Fermeture à la date limite
    Étant donné une randonnée "Lac Pink" "<statut>" dont la date limite d'inscription est atteinte
    Quand le système ferme la randonnée "Lac Pink"
    Alors la randonnée "Lac Pink" est "fermée"
    Et l'étape "fermée" est consignée comme effectuée par le système

    Exemples:
      | statut   |
      | ouverte  |
      | complète |

  Scénario: Plus aucune inscription ni annulation après la fermeture
    Étant donné une randonnée "Lac Pink" fermée
    Alors toute nouvelle inscription à "Lac Pink" est refusée
    Et toute annulation d'inscription à "Lac Pink" est refusée

  Scénario: Une randonnée annulée n'est pas fermée
    Étant donné une randonnée "Lac Pink" annulée dont la date limite d'inscription est atteinte
    Quand le système ferme la randonnée "Lac Pink"
    Alors la randonnée "Lac Pink" reste "annulée"

  Scénario: Fermeture avant la date limite refusée
    Étant donné une randonnée "Lac Pink" ouverte dont la date limite d'inscription n'est pas atteinte
    Quand le système ferme la randonnée "Lac Pink"
    Alors la fermeture est refusée avec le motif "date limite non atteinte"
    Et la randonnée "Lac Pink" reste "ouverte"
