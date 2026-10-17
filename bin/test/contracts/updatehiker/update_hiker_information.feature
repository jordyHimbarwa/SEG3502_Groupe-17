# language: fr
Fonctionnalité: UpdateHiker.updateHikerInformation
  Afin de garder mon profil à jour
  En tant que randonneur connecté
  Je veux modifier mes informations

  Contexte:
    Étant donné un randonneur connecté "ana@exemple.ca" de niveau "débutant"

  Scénario: Mise à jour réussie
    Quand "ana@exemple.ca" soumet ses informations avec le téléphone "613-555-0199" et le niveau "intermédiaire"
    Alors le téléphone de "ana@exemple.ca" est "613-555-0199"
    Et son niveau d'habileté est "intermédiaire"

  Scénario: Informations incomplètes
    Quand "ana@exemple.ca" soumet ses informations sans numéro de téléphone
    Alors la mise à jour est refusée avec le motif "informations incomplètes"
    Et ses informations restent inchangées

  Scénario: Un randonneur ne peut pas modifier le profil d'un autre
    Étant donné un randonneur "bob@exemple.ca"
    Quand "ana@exemple.ca" soumet des informations pour "bob@exemple.ca"
    Alors la mise à jour est refusée pour défaut d'autorisation
