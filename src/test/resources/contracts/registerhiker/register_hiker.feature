# language: fr
Fonctionnalité: RegisterHiker.registerHiker
  Afin de pouvoir m'inscrire à des randonnées
  En tant que membre du club ayant reçu un code d'invitation
  Je veux m'enregistrer comme randonneur

  Contexte:
    Étant donné un code d'invitation valide "CAIRN-2026-0042"

  Scénario: Enregistrement d'un randonneur adulte
    Quand un visiteur s'enregistre avec le code "CAIRN-2026-0042", le courriel "ana@exemple.ca", une date de naissance donnant 30 ans, des informations complètes et la décharge acceptée
    Alors un randonneur identifié par "ana@exemple.ca" est créé
    Et son adhésion est "active"
    Et il peut s'inscrire à des randonnées

  Scénario: Enregistrement d'un randonneur mineur avec tuteur légal
    Quand un visiteur s'enregistre avec le code "CAIRN-2026-0042", une date de naissance donnant 15 ans, la décharge acceptée et le tuteur légal "Marie Tremblay"
    Alors un randonneur est créé avec une adhésion "active"
    Et le nom du tuteur légal "Marie Tremblay" est consigné

  Scénario: Un mineur sans tuteur légal est refusé
    Quand un visiteur s'enregistre avec le code "CAIRN-2026-0042", une date de naissance donnant 15 ans et aucun tuteur légal
    Alors l'enregistrement est refusé avec le motif "tuteur légal requis"
    Et aucun randonneur n'est créé

  Scénario: Décharge non acceptée
    Quand un visiteur s'enregistre avec le code "CAIRN-2026-0042" sans accepter la décharge
    Alors l'enregistrement est refusé avec le motif "décharge non acceptée"

  Scénario: Code d'invitation invalide
    Quand un visiteur s'enregistre avec le code "INCONNU"
    Alors l'enregistrement est refusé avec le motif "code d'invitation invalide"

  Scénario: Courriel déjà utilisé
    Étant donné un randonneur existant identifié par "ana@exemple.ca"
    Quand un visiteur s'enregistre avec le code "CAIRN-2026-0042" et le courriel "ana@exemple.ca"
    Alors l'enregistrement est refusé avec le motif "courriel déjà utilisé"

  Scénario: Informations incomplètes
    Quand un visiteur s'enregistre avec le code "CAIRN-2026-0042" sans personne-ressource en cas d'urgence
    Alors l'enregistrement est refusé avec le motif "informations incomplètes"
