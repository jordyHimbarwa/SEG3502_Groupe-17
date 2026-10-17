# language: fr
Fonctionnalité: SendHikeReminder.sendHikeReminder
  Afin que les participants n'oublient pas la randonnée et soient prévenus du mauvais temps
  En tant que système
  Je veux envoyer un rappel la veille de la randonnée

  Contexte:
    Étant donné une randonnée "Lac Pink" sur le sentier "Sentier Lac-Pink" qui a lieu demain à 9 h
    Et les inscriptions suivantes à "Lac Pink" :
      | randonneur      | statut             |
      | ana@exemple.ca  | confirmée          |
      | bob@exemple.ca  | confirmée          |
      | cleo@exemple.ca | en liste d'attente |
      | dan@exemple.ca  | annulée            |

  Scénario: Rappel sans précipitations prévues
    Étant donné que le service météorologique prévoit du soleil au point de départ demain
    Quand le système envoie le rappel de la randonnée "Lac Pink"
    Alors un courriel de rappel est envoyé uniquement à "ana@exemple.ca" et "bob@exemple.ca"
    Et chaque courriel indique la date, l'heure de début, le sentier et le point de départ
    Et aucun courriel ne contient d'avertissement météorologique

  Plan du scénario: Rappel avec avertissement météorologique
    Étant donné que le service météorologique prévoit "<prévision>" au point de départ demain
    Quand le système envoie le rappel de la randonnée "Lac Pink"
    Alors chaque courriel de rappel contient un avertissement météorologique

    Exemples:
      | prévision |
      | pluie     |
      | neige     |

  Scénario: Service météorologique indisponible
    Étant donné que le service météorologique est indisponible
    Quand le système envoie le rappel de la randonnée "Lac Pink"
    Alors un courriel de rappel est envoyé à "ana@exemple.ca" et "bob@exemple.ca"
    Et aucun courriel ne contient d'avertissement météorologique

  Scénario: Aucun rappel pour une randonnée annulée
    Étant donné que la randonnée "Lac Pink" est annulée
    Quand le système envoie le rappel de la randonnée "Lac Pink"
    Alors aucun courriel de rappel n'est envoyé
