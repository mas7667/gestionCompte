# GestionCompte

Application bancaire de démonstration développée avec Spring Boot. Elle couvre l’authentification, la gestion de comptes et les opérations financières de base, avec conteneurisation, observabilité et déploiement continu sur Render.

## Fonctionnalités

- Inscription et connexion avec mot de passe BCrypt et jeton JWT
- Consultation des comptes appartenant à l’utilisateur connecté
- Dépôts, retraits et virements atomiques
- Historique paginé des transactions
- Documentation OpenAPI/Swagger
- Métriques Actuator et Prometheus
- Tests unitaires avec JUnit et Mockito

## Stack vérifiée

- Java 17
- Spring Boot 4.1.0
- Spring Security, Spring Data JPA et Bean Validation
- PostgreSQL en production, H2 en développement/test
- JJWT 0.12.5 et Springdoc OpenAPI 2.5
- Docker, GitHub Actions, GHCR et Render

## Vérification

```bash
./mvnw verify
docker build -t gestion-compte .
```

La variable `JWT_SECRET` doit contenir une clé Base64 robuste en production. Les contrôleurs vérifient désormais que le compte source et l’historique demandés appartiennent à l’identité authentifiée.
