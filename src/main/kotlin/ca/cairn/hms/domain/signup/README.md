# Sous-domaine : Inscriptions (`domain.signup`)

**Type :** cœur

Inscriptions des randonneurs aux randonnées et liste d'attente.

- **Concepts :** inscription (horodatage, statut : confirmée, en liste d'attente, annulée), liste d'attente ordonnée selon l'heure d'inscription.
- **Règles clés :** adhésion active ; randonnée ouverte ; difficulté au plus un niveau au-dessus du niveau d'habileté (débutant ↔ facile, intermédiaire ↔ modéré, avancé ↔ difficile, expert ↔ très difficile) ; annulation possible jusqu'à la date limite d'inscription ; lorsqu'une place confirmée se libère, le premier de la liste d'attente est promu automatiquement.
- **À clarifier avec le client :** le glossaire parle de « son niveau ou un niveau au-dessus » (sans niveau inférieur), alors que le cas S'inscrire dit « au plus un niveau au-dessus ». Les contrats retiennent la seconde lecture.
- **Collabore avec :** `hike` (statut ouverte/complète), `hiker` (adhésion, niveau), `notification` (courriels).
