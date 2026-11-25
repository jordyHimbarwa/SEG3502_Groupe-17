<div align="center">

# Livrable 2 : Domaine

## CAR-HMS — Système de gestion d'événements de randonnée Cairn

**Équipe : Groupe 17**

SEG 3502 — Architecture et conception de logiciel · Automne 2026
Professeur : Kalonji Kalala
Date : 4 décembre 2026

| Membre | Nom d'utilisateur GitHub | Numéro d'étudiant |
|---|---|---|
| Jordy-Ardy Himbarwa | jordyHimbarwa | 300253195 |
| Mena Girgis | _À compléter_ | 300293273 |

</div>

---

## 2. Sommaire exécutif

Ce document présente la conception tactique du domaine de CAR-HMS et son implémentation testée par les contrats Cucumber du livrable 1.
Il s'adresse à l'équipe de développement et au correcteur du cours. Il contient le modèle du domaine de chaque sous-domaine,
les réalisations des cas d'utilisation, les hypothèses et décisions de conception, et la déclaration d'utilisation de l'IA générative.

## 3. Description de l'équipe et des rôles

| Membre | Tâches réalisées | Pourcentage | Heures |
|---|---|---|---|
| Jordy-Ardy Himbarwa | _À compléter_ | _%_ | _h_ |
| Mena Girgis | _À compléter_ | _%_ | _h_ |

## 4. Hypothèses et décisions de conception

> _À rédiger par l'équipe. L'énoncé interdit l'IA générative pour cette section._

## 5. Modèle du domaine

> _À réaliser par l'équipe : un diagramme de classes UML par sous-domaine (Mermaid recommandé).
> L'énoncé interdit l'IA générative pour cette section._

## 6. Réalisations des cas d'utilisation

> _À réaliser par l'équipe : diagrammes de séquence de niveau 1 et de niveau 2 pour chaque cas d'utilisation,
> sauf Se connecter, Se déconnecter et Générer le rapport d'utilisation des sentiers.
> L'énoncé interdit l'IA générative pour cette section._

### Intégration continue (bonus)

- Première exécution où tous les contrats échouent : _lien à ajouter_
- Exécution où tous les contrats réussissent : _lien à ajouter_

## 7. Déclaration d'utilisation de l'IA générative

**Outil :** Claude Code (extension VS Code), modèle Claude Opus 5.5 d'Anthropic (`claude-opus-5-5`).

**Parties concernées :** uniquement l'implémentation (section 2 Code et section 3 Bonus de l'énoncé).
Aucune IA générative n'a été utilisée pour le modèle du domaine, les réalisations de cas d'utilisation
ni les décisions de conception présentés dans ce document.

**Ce qui a été demandé à l'outil :** produire le code à partir des livrables 1 et 2, soit :

- les entités, objets-valeurs, événements, fabriques, dépôts et façades des paquetages `domain.*` ;
- les implémentations des commandes dans `application.usecases.implementation` et les interfaces des services externes dans `application.services` ;
- les stubs (fabriques, dépôts, services, émetteur d'événements) et les définitions d'étapes Cucumber dans `src/test` ;
- le workflow GitHub Actions `.github/workflows/contracts.yml`.

**Modification d'un contrat du livrable 1 :** `UpdateHikerInformationQuery` contient maintenant `requesterId`,
pour vérifier qu'un randonneur ne modifie que son propre profil (hypothèse 2 du livrable 1).

**Vérification :**

- exécution de `./gradlew test` : tous les scénarios des 13 contrats réussissent ;
- test de mutation manuel : la suppression du courriel de promotion fait échouer le scénario correspondant de Annuler une inscription ;
- _à compléter par l'équipe : relecture du code et vérification de sa conformité avec les diagrammes des sections 5 et 6._

## 8. Références

- CAR-Req-01, *Document des exigences initiales du système CAR-HMS*.
- SEG 3502, *Livrable 1 : domaine initial*, Groupe 17, automne 2026.
- SEG 3502, *Livrable 2 : Domaine*, énoncé, automne 2026.
- Cucumber, *Cucumber-JVM et cucumber-spring*, https://cucumber.io/docs/
- LLM Guidelines, *Declare usage*, https://llm-guidelines.org/guidelines/declare-usage/
- Dépôt GitHub du projet : https://github.com/jordyHimbarwa/SEG3502_Groupe-17
