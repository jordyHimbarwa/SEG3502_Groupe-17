# language: fr
Fonctionnalité: CancelSignUp.cancelSignUp
  Afin de libérer ma place si je ne peux plus participer
  En tant que randonneur connecté
  Je veux annuler mon inscription avant la date limite d'inscription

  Scénario: Annulation d'une inscription confirmée dans une randonnée ouverte
    Étant donné une randonnée "Lac Pink" ouverte, de capacité 10 avec 4 inscriptions confirmées dont celle de "ana@exemple.ca"
    Quand "ana@exemple.ca" annule son inscription à "Lac Pink"
    Alors son inscription est "annulée"
    Et la randonnée "Lac Pink" a 7 places restantes

  Scénario: Le premier de la liste d'attente prend la place libérée
    Étant donné une randonnée "Lac Pink" complète dont "ana@exemple.ca" a une inscription confirmée
    Et la liste d'attente de "Lac Pink" contient dans l'ordre "bob@exemple.ca", "cleo@exemple.ca"
    Quand "ana@exemple.ca" annule son inscription à "Lac Pink"
    Alors l'inscription de "bob@exemple.ca" devient "confirmée"
    Et un courriel l'informant de son inscription est envoyé à "bob@exemple.ca"
    Et "cleo@exemple.ca" est en position 1 de la liste d'attente
    Et la randonnée "Lac Pink" reste "complète"

  Scénario: La randonnée redevient ouverte lorsqu'il n'y a pas de liste d'attente
    Étant donné une randonnée "Lac Pink" complète dont "ana@exemple.ca" a une inscription confirmée
    Et la liste d'attente de "Lac Pink" est vide
    Quand "ana@exemple.ca" annule son inscription à "Lac Pink"
    Alors la randonnée "Lac Pink" redevient "ouverte" avec 1 place restante

  Scénario: Annulation d'une inscription en liste d'attente
    Étant donné une randonnée "Lac Pink" complète
    Et la liste d'attente de "Lac Pink" contient dans l'ordre "bob@exemple.ca", "ana@exemple.ca", "cleo@exemple.ca"
    Quand "ana@exemple.ca" annule son inscription à "Lac Pink"
    Alors son inscription est "annulée"
    Et "cleo@exemple.ca" est en position 2 de la liste d'attente

  Scénario: Annulation après la date limite d'inscription refusée
    Étant donné une randonnée "Lac Pink" fermée dont "ana@exemple.ca" a une inscription confirmée
    Quand "ana@exemple.ca" annule son inscription à "Lac Pink"
    Alors l'annulation est refusée avec le motif "date limite d'inscription passée"
    Et son inscription reste "confirmée"

  Scénario: Un randonneur ne peut pas annuler l'inscription d'un autre
    Étant donné une randonnée "Lac Pink" ouverte dont "bob@exemple.ca" a une inscription confirmée
    Quand "ana@exemple.ca" annule l'inscription de "bob@exemple.ca" à "Lac Pink"
    Alors l'annulation est refusée pour défaut d'autorisation
