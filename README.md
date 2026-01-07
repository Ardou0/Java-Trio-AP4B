# Trio - Adaptation UTBM du Jeu de Cartes

Ce projet est une adaptation numérique du jeu de société **Trio**, développée en Java avec l'interface graphique **JavaFX**. Il permet de jouer en mode Solo (contre des IA) ou en mode Équipe.

## Prérequis Technique : Java 21

**Ce projet a été développé et compilé sous Java 21.**

Pour exécuter le projet (que ce soit via l'IDE ou via l'exécutable `.jar`), il est impératif d'avoir une **JRE (Java Runtime Environment) ou un JDK en version 21** installé sur votre machine.

Si vous tentez de lancer l'application avec une version antérieure (Java 8, 11, 17...), vous rencontrerez des erreurs de compilation ou d'exécution (ex: `UnsupportedClassVersionError`).

## Architecture du Projet (MVC)

Le projet respecte strictement le patron de conception **Modèle-Vue-Contrôleur (MVC)** pour séparer la logique métier, l'interface utilisateur et la gestion des événements.

### Structure des packages (`fr.utbm.ap4b`)

#### 1. Le Modèle (`fr.utbm.ap4b.model`)
Il contient les données et les règles, sans aucune connaissance de l'interface graphique.

*   **`Game.java`** : La classe centrale. Elle gère la machine à états du jeu (tours, phases de jeu, validation des actions, conditions de victoire).
*   **`Card.java`** : Représente une carte unique. Utilise un `UUID` pour distinguer physiquement deux cartes ayant la même valeur (ex: il y a trois "7" différents).
*   **`Actor.java` / `Joueur.java` / `IA.java`** : Gestion des participants. L'interface `Actor` permet de traiter de la même façon un humain et une IA.
*   **`DrawPile.java`** : Gère la pioche et la distribution aléatoire.
*   **`CompletedTrios.java`** : Stocke les trios validés pour calculer le score et déterminer le vainqueur.
*   **`JoueurEquipe.java`** : Extension de joueur pour gérer les spécificités du mode équipe (lien avec un coéquipier).

#### 2. La Vue (`fr.utbm.ap4b.view`)
Gère l'affichage et l'interface utilisateur (JavaFX). Elle ne prend aucune décision métier.

*   **`TrioApp.java`** : Point d'entrée de l'application JavaFX. Configure la fenêtre principale.
*   **`GameMainPage.java`** : La vue principale du plateau de jeu. Affiche les cartes, les boutons d'action et les messages.
*   **`ModeSelectionPage.java`** : Menu d'accueil pour choisir entre Solo et Équipe.
*   **`EndGamePage.java`** : Écran de fin de partie affichant le vainqueur.
*   **`DrawPilePage.java`** : Vue spécifique pour afficher les cartes disponibles dans la pioche.
*   **`TrioSoloPage.java` / `TrioTeamPage.java`** : Tableaux des scores (trios formés).

#### 3. Le Contrôleur (`fr.utbm.ap4b.controller`)
Fait le lien entre la Vue et le Modèle. Il intercepte les clics de l'utilisateur et met à jour le modèle.

*   **`GameController.java`** : Il reçoit les clics (révéler une carte, choisir un adversaire), vérifie via le Modèle si l'action est possible, et demande à la Vue de se rafraîchir.
*   **`SwapController.java`** : Contrôleur dédié à la phase d'échange de cartes (spécifique au mode équipe).
*   **`MenuController.java`** : Gère la navigation dans les menus.
*   **`EndGameController.java`** : Gère la logique de fin de partie (rejouer, quitter).

### Point d'entrée
*   **`Main.java`** : Classe lanceur simple qui appelle `TrioApp`. Elle est nécessaire pour garantir le bon chargement des composants JavaFX lors de la création d'un exécutable (Fat Jar).

## Installation et Lancement

### Via Maven
Le projet utilise Maven pour la gestion des dépendances.

```bash
# Nettoyer et lancer l'application
mvn clean javafx:run
```

### Via IntelliJ IDEA
1. Ouvrir le projet.
2. S'assurer que le SDK du projet est bien réglé sur **Java 21** (File > Project Structure > Project SDK).
3. Lancer la classe `fr.utbm.ap4b.Main`.

## Fonctionnalités
*   **Mode Solo** : Chaque joueur joue pour sa propre victoire.
*   **Mode Équipe** : Jouez avec un partenaire. Inclut des phases d'échange de cartes tactique.
*   **Règles du jeu** : Mode normal ou piquant, réalisation de 3 trios ou 2 trios lié selon le mode de jeu ou le trio de 7 pour gagner.
