# Sous-domaine : Randonnées (`domain.hike`)

**Type :** cœur

Planification des randonnées et gestion de leur cycle de vie.

- **Concepts :** randonnée (identifiant, titre, description, sentier, date et heure de début, durée prévue, date limite d'inscription, capacité), statut, journal des transitions.
- **Cycle de vie :** ouverte ⇄ complète → fermée → terminée ; annulée à tout moment avant le début. Chaque transition consigne l'utilisateur (ou le système) qui l'a effectuée.
- **Règles clés :** date dans la saison d'ouverture du sentier ; date limite d'inscription au plus 2 semaines avant la randonnée ; certification de l'organisateur valide à la date de la randonnée ; niveau d'organisateur ≥ difficulté du sentier ; la difficulté d'une randonnée est celle publiée par la CCN pour son sentier.
- **Hors portée :** ce qui se passe le jour de la randonnée.
- **Collabore avec :** `trail`, `hiker` (organisateur), `signup` (places restantes).
