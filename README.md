# ALANYA

**Application militaire de communication**, permettant l'envoi de messages, de fichiers et d'appels audio/vidéo sécurisés.

---

## Sommaire

* [Description](#description)
* [Fonctionnalités](#fonctionnalités)
* [Architecture](#architecture)
* [Technologies](#technologies)
* [Installation](#installation)
* [Configuration](#configuration)
* [Utilisation](#utilisation)
* [Structure du projet](#structure-du-projet)
* [Développement et tests](#développement-et-tests)
* [Persistance et base de données](#persistance-et-base-de-données)
* [Sécurité](#sécurité)
* [Contribuer](#contribuer)
* [Licence](#licence)

---

## Description

ALANYA est une application de communication destinée à un usage militaire ou sécurisé. Elle propose :

* **Messagerie instantanée** (texte)
* **Transfert de fichiers**
* **Appels audio/vidéo**

Chaque utilisateur peut gérer son carnet de contacts, lancer des discussions sécurisées, et passer des appels en toute confidentialité.

---

## Fonctionnalités

1. **Authentification**

   * Système de login et d'inscription sécurisé
2. **Gestion des contacts**

   * Recherche d’utilisateurs par ID
   * Ajout, suppression et affichage des contacts
3. **Messagerie**

   * Envoi et réception de messages texte
   * Historique de conversation scrollable
4. **Transfert de fichiers**

   * Envoi et réception de pièces jointes
5. **Appels audio/vidéo**

   * Appels audio avec timer
   * (Optionnel) Appels vidéo intégrés
6. **Interface**

   * UI JavaFX moderne, responsive
7. **Sécurité**

   * Communication chiffrée (TLS/SSL)
   * Authentification forte

---

## Architecture

L’application suit le pattern **MVC** :

* **View**  : JavaFX (UI) dans `views/`
* **Controller** : Liaison UI ↔ logique métier dans `controllers/`
* **Model / Service** : Logique métier, accès aux données dans `services/` et `dao/`
* **MainApp** : Point d’entrée unique (classe JavaFX `Application`)

Le client JavaFX dialogue avec le serveur via une API REST sécurisée (ou WebSocket) pour :

* Authentification (`/login`, `/register`)
* Gestion des utilisateurs (`/users/{id}`)
* Gestion des contacts (`/contacts`)
* Envoi de messages (`/messages`)
* Transfert de fichiers (`/files`)
* Signaling des appels audio/vidéo

---

## Technologies

* **Langage** : Java 11+
* **UI**       : JavaFX
* **Build**    : Maven
* **Base**     : PostgreSQL (serveur central)
* **API**      : REST (Spring Boot ou JAX-RS)
* **Sécurité** : TLS/SSL, éventuellement JWT pour les tokens

---

## Installation

1. **Cloner le dépôt** :

   ```bash
   git clone https://github.com/ton-org/alanya.git
   cd alanya
   ```

2. **Configurer la base de données** :

   * Installer PostgreSQL
   * Créer la base `alanya_db`
   * Exécuter le script SQL `schema.sql` dans `resources/db/`

3. **Configurer les propriétés** :

   * Copier `application.example.properties` → `application.properties`
   * Ajuster `db.url`, `db.user`, `db.password`

4. **Compiler** :

   ```bash
   mvn clean install
   ```

---

## Configuration

**application.properties**

```properties
server.port=8080
db.url=jdbc:postgresql://localhost:5432/alanya_db
db.user=alanya_user
db.password=secret
jwt.secret=UneCleSuperSecrete
```

---

## Utilisation

1. **Lancer le serveur** :

   ```bash
   mvn spring-boot:run
   ```

2. **Lancer le client** :

   ```bash
   mvn javafx:run -DmainClass=views.MainApp
   ```

3. **Se connecter / s’inscrire** via l’interface initiale.

4. **Ajouter des contacts** en saisissant leur ID, puis débuter une conversation ou appel.

---

## Structure du projet

```text
src/
├─ main/
│  ├─ java/
│  │   ├─ controllers/  # Logique de liaison UI
│  │   ├─ services/     # Services métier et accès DAO
│  │   ├─ models/       # Entités (Utilisateur, Contact, Message)
│  │   └─ views/        # UI JavaFX
│  └─ resources/
│      ├─ db/          # scripts SQL (schema.sql, data.sql)
│      └─ styles/      # CSS JavaFX
└─ test/
```

---

## Développement et tests

* **Tests unitaires** : JUnit 5 dans `src/test/java/services/`
* **Coverage** : SonarQube ou Jacoco
* **CI/CD** : GitHub Actions ou GitLab CI

---

## Persistance et base de données

* **Utilisateur** (`user_id`, `username`, `password_hash`, …)
* **Contact** (`id`, `user_id`, `contact_id`, `date_added`)
* **Message** (`id`, `from_id`, `to_id`, `content`, `timestamp`)
* **File** (`id`, `message_id`, `filename`, `blob`, `timestamp`)

Les scripts SQL sont dans `resources/db/schema.sql`.

---

## Sécurité

* **TLS/SSL** pour chiffrer les échanges client ↔ serveur
* **JWT** pour l’authentification stateless
* **Hashing** des mots de passe (BCrypt)

---

## Contribuer

1. Fork le dépôt
2. Créer une branche `feature/ma-fonctionnalité`
3. Commit & push
4. Ouvrir une Pull Request

---

## Licence

MIT © Ton Organisation
