# language: fr
Fonctionnalité: BrowseHikes.listHikesForSkillLevel
  Afin de ne voir que les randonnées auxquelles je peux m'inscrire
  En tant que randonneur connecté
  Je veux lister les randonnées ouvertes correspondant à mon niveau d'habileté

  Contexte:
    Étant donné les randonnées ouvertes suivantes :
      | titre    | difficulté     |
      | Facile   | facile         |
      | Modérée  | modéré         |
      | Difficile| difficile      |
      | Extrême  | très difficile |

  Plan du scénario: Randonnées au plus un niveau au-dessus du niveau d'habileté
    Étant donné un randonneur connecté de niveau "<niveau>"
    Quand il liste les randonnées correspondant à son niveau d'habileté
    Alors le résultat contient exactement <randonnées>

    Exemples:
      | niveau        | randonnées                          |
      | débutant      | "Facile", "Modérée"                 |
      | intermédiaire | "Facile", "Modérée", "Difficile"    |
      | expert        | "Facile", "Modérée", "Difficile", "Extrême" |

  Scénario: Un utilisateur non authentifié ne peut pas utiliser cette commande
    Quand un visiteur liste les randonnées correspondant à son niveau d'habileté
    Alors la commande est refusée pour défaut d'authentification
