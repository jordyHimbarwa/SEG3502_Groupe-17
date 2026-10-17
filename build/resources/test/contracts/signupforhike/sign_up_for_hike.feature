# language: fr
Fonctionnalité: SignUpForHike.signUpForHike
  Afin de participer à une randonnée
  En tant que randonneur connecté ayant une adhésion active
  Je veux m'inscrire à une randonnée ouverte

  Contexte:
    Étant donné un randonneur connecté "ana@exemple.ca" de niveau "intermédiaire" avec une adhésion "active"

  Scénario: Inscription confirmée lorsqu'une place est disponible
    Étant donné une randonnée "Lac Pink" de difficulté "modéré", ouverte, de capacité 10 avec 3 inscriptions confirmées
    Quand "ana@exemple.ca" s'inscrit à "Lac Pink"
    Alors son inscription est "confirmée" avec l'heure à laquelle elle a été faite
    Et la randonnée "Lac Pink" reste "ouverte" avec 6 places restantes
    Et un courriel de confirmation est envoyé à "ana@exemple.ca"

  Scénario: La dernière place rend la randonnée complète
    Étant donné une randonnée "Lac Pink" de difficulté "modéré", ouverte, de capacité 10 avec 9 inscriptions confirmées
    Quand "ana@exemple.ca" s'inscrit à "Lac Pink"
    Alors son inscription est "confirmée"
    Et la randonnée "Lac Pink" devient "complète"
    Et l'étape "complète" est consignée avec l'utilisateur "ana@exemple.ca"

  Scénario: Mise en liste d'attente lorsque la randonnée est complète
    Étant donné une randonnée "Lac Pink" de difficulté "modéré", complète, avec 2 inscriptions en liste d'attente
    Quand "ana@exemple.ca" s'inscrit à "Lac Pink"
    Alors son inscription est "en liste d'attente" en position 3
    Et un courriel indiquant la position 3 dans la liste d'attente est envoyé à "ana@exemple.ca"

  Plan du scénario: Inscription refusée
    Étant donné <situation>
    Quand "ana@exemple.ca" s'inscrit à "Wolf"
    Alors l'inscription est refusée avec le motif "<motif>"
    Et aucune inscription n'est créée

    Exemples:
      | situation                                                                  | motif                      |
      | une randonnée "Wolf" de difficulté "très difficile", ouverte               | niveau d'habileté insuffisant |
      | une randonnée "Wolf" de difficulté "modéré", fermée                        | randonnée non ouverte      |
      | une randonnée "Wolf" de difficulté "modéré", annulée                       | randonnée non ouverte      |
      | une adhésion "suspendue" et une randonnée "Wolf" de difficulté "modéré", ouverte | adhésion inactive    |
      | une adhésion "expirée" et une randonnée "Wolf" de difficulté "modéré", ouverte   | adhésion inactive    |

  Scénario: Inscription en double refusée
    Étant donné une randonnée "Lac Pink" de difficulté "modéré", ouverte
    Et "ana@exemple.ca" a déjà une inscription "confirmée" à "Lac Pink"
    Quand "ana@exemple.ca" s'inscrit à "Lac Pink"
    Alors l'inscription est refusée avec le motif "déjà inscrit"
