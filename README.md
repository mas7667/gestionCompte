# GestionCompte

API de gestion de comptes bancaires développée avec Spring Boot, dans le cadre d'un projet portfolio ciblant un poste de développeur junior Java.

## Stack technique

- **Java 17** / **Spring Boot 4**
- **Spring Data JPA** — persistance (H2 en développement)
- **Spring Security** avec authentification par **JWT**
- **Thymeleaf** + Tailwind CSS — pages web (inscription, connexion, tableau de bord)
- **JUnit 5** + **Mockito** — tests unitaires
- **GitHub Actions** — pipeline CI/CD (`dev` → `test` → `main`)
- **Docker** — image de déploiement

## Fonctionnalités

- Inscription et connexion avec authentification JWT
- Création automatique d'un compte à l'inscription
- Dépôt, retrait et virement entre comptes
- Historique paginé des transactions
- Gestion centralisée des erreurs (`GlobalExceptionHandler`)
- Réponses API via DTOs dédiés (aucune entité JPA n'est jamais exposée directement)

## Lancer le projet en local

### Prérequis

- Java 17
- Maven (ou le wrapper `mvnw` fourni)

### Démarrage

```bash
./mvnw spring-boot:run
```

L'application démarre sur `http://localhost:8080` avec une base H2 en mémoire (aucune configuration requise). La console H2 est accessible sur `/h2-console`.

Pages web disponibles :
- `/register` — inscription
- `/login` — connexion
- `/dashboard` — tableau de bord (dépôt, retrait, virement, historique)

### Lancer les tests

```bash
./mvnw test
```

### Build de l'image Docker

```bash
docker build -t gestion-compte .
docker run -p 8080:8080 gestion-compte
```

## Exemples d'appels API

### Inscription

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Jane Doe","email":"jane@example.com","password":"password123"}'
```

Réponse :

```json
{ "token": "eyJhbGciOi...", "accountNumber": "9306CCCC-0FF" }
```

### Connexion

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"jane@example.com","password":"password123"}'
```

Les appels suivants nécessitent le header `Authorization: Bearer <token>` obtenu à l'inscription ou la connexion.

### Dépôt

```bash
curl -X POST http://localhost:8080/api/transactions/deposit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"accountNumber":"9306CCCC-0FF","amount":50}'
```

### Retrait

```bash
curl -X POST http://localhost:8080/api/transactions/withdraw \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"accountNumber":"9306CCCC-0FF","amount":20}'
```

### Virement

```bash
curl -X POST http://localhost:8080/api/transactions/transfer \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"accountNumber":"9306CCCC-0FF","destinationAccountNumber":"AAAA1111-BBB","amount":10}'
```

### Consulter un compte

```bash
curl http://localhost:8080/api/accounts/9306CCCC-0FF \
  -H "Authorization: Bearer $TOKEN"
```

### Historique des transactions (paginé)

```bash
curl "http://localhost:8080/api/transactions/account/1?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

## Défi technique : atomicité du virement

Un virement entre deux comptes implique deux écritures distinctes (débit du compte source, crédit du compte destination) et l'enregistrement de deux transactions liées (`TRANSFER_OUT` / `TRANSFER_IN`). Sans précaution, une panne ou une exception après le débit mais avant le crédit laisserait la base de données dans un état incohérent — de l'argent disparaîtrait sans jamais atteindre le compte destinataire.

`TransactionService.transfer()` est annotée `@Transactional` : toutes les opérations (lecture des deux comptes, mise à jour des soldes, sauvegarde des deux transactions) s'exécutent dans une seule transaction de base de données. Si une exception survient à n'importe quelle étape — par exemple un solde insuffisant détecté après la lecture des comptes — Spring déclenche un rollback complet, annulant tout changement déjà appliqué en mémoire à la base. Le solde des deux comptes reste donc soit entièrement inchangé, soit entièrement mis à jour, jamais dans un état intermédiaire.

Ce comportement est couvert par les tests unitaires de `TransactionServiceTest`, qui vérifient notamment qu'un virement avec solde insuffisant ne déclenche aucune sauvegarde de transaction.

## Sécurité

- Mots de passe hachés avec BCrypt, jamais retournés par l'API
- Authentification stateless par JWT (expiration configurable via `app.jwt.expiration-ms`)
- Les réponses API utilisent des DTOs dédiés (`AccountResponse`, `TransactionResponse`) plutôt que les entités JPA, pour éviter d'exposer des relations internes ou des champs sensibles

## Pipeline CI/CD

Le pipeline GitHub Actions (`.github/workflows/ci.yml`) compile et teste le projet à chaque push, puis promeut automatiquement le code de `dev` vers `test` puis vers `main` après succès des tests, avec build et publication de l'image Docker sur GitHub Container Registry.