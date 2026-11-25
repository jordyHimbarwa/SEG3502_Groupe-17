# CAR-HMS — Back-end (Kotlin / Spring Boot)

Système de gestion des randonnées du Club de randonnée Cairn (document CAR-Req-01).
Ce dépôt contient la structure initiale du back-end : sous-domaines, commandes d'application, DTO de requête et contrats.

## Structure

```
src/main/kotlin/ca/cairn/hms/
├── domain/                     Un sous-paquetage par sous-domaine (README.md dans chacun)
│   ├── shared/                 Acteur, événements de domaine, émetteur, exceptions
│   ├── hike/                   Cœur — agrégat Randonnée, fabrique, dépôt, événements, façade
│   ├── signup/                 Cœur — agrégat Inscription, liste d'attente, façade
│   ├── hiker/                  Support — agrégat Randonneur, adhésion, certification, façade
│   ├── trail/                  Support — agrégat Sentier (copie locale des données de la CCN), façade
│   ├── notification/           Générique — réalisé par les services application.services
│   ├── identity/               Générique — réalisé par AuthenticationService
│   └── reporting/              Support — hors portée du livrable 2
├── application/
│   ├── usecases/               Une interface par cas d'utilisation
│   │   └── implementation/     Réalisations des cas d'utilisation
│   ├── services/               Services externes : courriel, météo, CCN, authentification, codes d'invitation
│   └── dtos/                   DTO de requête et de résultat
└── config/                     Horloge du système
src/test/kotlin/ca/cairn/hms/
├── stubs/                      Stubs des fabriques, dépôts, services et de l'émetteur d'événements
└── contracts/                  Configuration Cucumber + Spring, définitions d'étapes, données de test
src/test/resources/contracts/   Un contrat Cucumber (.feature) par commande
```

## Cas d'utilisation → commandes → contrats

| Cas d'utilisation (priorité) | Interface | Commande | DTO de requête | Contrat |
|---|---|---|---|---|
| Créer une randonnée (1) | `CreateHike` | `createHike` | `CreateHikeQuery` | `createhike/create_hike.feature` |
| Fermer une randonnée (2) | `CloseHike` | `closeHike` | `CloseHikeQuery` | `closehike/close_hike.feature` |
| S'inscrire à une randonnée (3) | `SignUpForHike` | `signUpForHike` | `SignUpForHikeQuery` | `signupforhike/sign_up_for_hike.feature` |
| Annuler une inscription (4) | `CancelSignUp` | `cancelSignUp` | `CancelSignUpQuery` | `cancelsignup/cancel_sign_up.feature` |
| Parcourir les randonnées (5) | `BrowseHikes` | `listOpenHikes` | — | `browsehikes/list_open_hikes.feature` |
| | | `searchOpenHikes` | `SearchOpenHikesQuery` | `browsehikes/search_open_hikes.feature` |
| | | `listHikesForSkillLevel` | `ListHikesForSkillLevelQuery` | `browsehikes/list_hikes_for_skill_level.feature` |
| Enregistrer un randonneur (6) | `RegisterHiker` | `registerHiker` | `RegisterHikerQuery`, `EmergencyContactDto` | `registerhiker/register_hiker.feature` |
| Mettre à jour un randonneur (7) | `UpdateHiker` | `updateHikerInformation` | `UpdateHikerInformationQuery` | `updatehiker/update_hiker_information.feature` |
| | | `updateMembershipStatus` | `UpdateMembershipStatusQuery` | `updatehiker/update_membership_status.feature` |
| | | `recordLeaderCertification` | `RecordLeaderCertificationQuery` | `updatehiker/record_leader_certification.feature` |
| Annuler une randonnée (8) | `CancelHike` | `cancelHike` | `CancelHikeQuery` | `cancelhike/cancel_hike.feature` |
| Envoyer un rappel de randonnée (9) | `SendHikeReminder` | `sendHikeReminder` | `SendHikeReminderQuery` | `sendhikereminder/send_hike_reminder.feature` |

Se connecter, Se déconnecter et Générer un rapport d'utilisation des sentiers sont exclus comme demandé.

## Choix de conception

- Les commandes déclenchées par le système (`closeHike`, `sendHikeReminder`) reçoivent l'identifiant de la randonnée ; un ordonnanceur les invoquera.
- Chaque DTO porte l'identifiant de l'acteur (`organizerId`, `administratorId`, `requesterId`, `hikerId`) pour consigner qui a effectué chaque étape du cycle de vie et vérifier l'autorisation.
- Difficulté des sentiers et niveau d'habileté sont deux énumérations alignées un pour un (facile ↔ débutant, …, très difficile ↔ expert).
- Ambiguïté signalée au client : le glossaire limite l'inscription à « son niveau ou un niveau au-dessus », le cas d'utilisation à « au plus un niveau au-dessus ». Les contrats retiennent la seconde lecture (un randonneur peut s'inscrire à une randonnée plus facile).

## Construction et contrats

```
./gradlew test
```

Les contrats sont rédigés en Gherkin français (`# language: fr`) et s'exécutent avec `./gradlew test`.
Le rapport Cucumber est produit dans `build/reports/cucumber/contracts.html`.

Les fabriques et les dépôts sont des interfaces du domaine ; leurs seules implémentations, à ce stade,
sont les stubs des sources de test. L'application ne peut donc pas encore démarrer avec `bootRun` :
les implémentations réelles viendront au livrable 3.

Le workflow `.github/workflows/contracts.yml` exécute les contrats ; il se déclenche manuellement
depuis l'onglet Actions.
