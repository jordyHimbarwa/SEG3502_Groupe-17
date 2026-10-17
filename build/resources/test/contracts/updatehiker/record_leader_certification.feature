# language: fr
Fonctionnalité: UpdateHiker.recordLeaderCertification
  Afin d'autoriser un randonneur à diriger des randonnées
  En tant qu'administrateur
  Je veux enregistrer sa certification de chef de randonnée et son niveau d'organisateur

  Contexte:
    Étant donné un administrateur connecté
    Et un randonneur "ana@exemple.ca"

  Scénario: Le randonneur devient organisateur de randonnée
    Quand l'administrateur enregistre pour "ana@exemple.ca" le certificat "CHR-0117" délivré le "2026-05-01", expirant le "2028-05-01", au niveau d'organisateur "difficile"
    Alors "ana@exemple.ca" est organisateur de randonnée
    Et sa certification porte le numéro "CHR-0117" et expire le "2028-05-01"
    Et son niveau d'organisateur est "difficile"

  Scénario: Dates de certification incohérentes
    Quand l'administrateur enregistre pour "ana@exemple.ca" un certificat délivré le "2026-05-01" expirant le "2026-04-01"
    Alors l'enregistrement est refusé avec le motif "date d'expiration antérieure à la date de délivrance"
    Et "ana@exemple.ca" n'est pas organisateur de randonnée

  Scénario: Seul un administrateur peut enregistrer une certification
    Étant donné un randonneur connecté "bob@exemple.ca"
    Quand "bob@exemple.ca" enregistre une certification pour "ana@exemple.ca"
    Alors l'enregistrement est refusé pour défaut d'autorisation
