# Parking Service

API REST Spring Boot permettant à un client mobile ou Web de rechercher les parkings proches d'une position, avec leur disponibilité en temps réel.

L'implémentation fournie utilise les [données ouvertes de Grand Poitiers](https://data.grandpoitiers.fr/datasets/mobilites-stationnement-des-parkings-en-temps-reel/table), actualisées toutes les minutes. L'API publique est indépendante de ce fournisseur et de son format.

## Démarrer le projet

Prérequis : JDK 21 ou version ultérieure.

Sous Linux ou macOS :

```shell
./mvnw spring-boot:run
```

Sous Windows :

```powershell
.\mvnw.cmd spring-boot:run
```

L'application écoute par défaut sur `http://localhost:8080`. Son état est disponible sur `GET /actuator/health`.

La documentation interactive Swagger est disponible sur :

- `http://localhost:8080/swagger-ui.html` pour explorer et appeler l'API ;
- `http://localhost:8080/v3/api-docs` pour le contrat OpenAPI JSON généré.

Pour lancer les tests :

```shell
./mvnw test
```

## API

### Rechercher les parkings proches

```http
GET /api/v1/parkings?latitude=46.5802&longitude=0.3404&radiusMeters=5000
```

Paramètres :

| Paramètre | Obligatoire | Contraintes | Description |
|---|---:|---|---|
| `latitude` | oui | -90 à 90 | Latitude WGS84 de l'utilisateur |
| `longitude` | oui | -180 à 180 | Longitude WGS84 de l'utilisateur |
| `radiusMeters` | non | 1 à 50 000, défaut 5 000 | Rayon de recherche en mètres |

Réponse `200 OK`, triée de la plus courte à la plus longue distance :

```json
{
  "count": 1,
  "parkings": [
    {
      "id": "3",
      "name": "THEATRE",
      "location": {
        "latitude": 46.58383455409422,
        "longitude": 0.33779491061805567
      },
      "capacity": 320,
      "availableSpaces": 106,
      "occupancyRate": 66.875,
      "distanceMeters": 440,
      "updatedAt": "2026-10-05T20:24:07Z"
    }
  ]
}
```

Une requête invalide renvoie `400 Bad Request`. Une indisponibilité ou une réponse illisible du fournisseur renvoie `503 Service Unavailable` au format `application/problem+json`, sans exposer ses détails techniques.

## Architecture et choix

Le code suit une architecture par fonctionnalité avec trois responsabilités :

```text
parking/api                 contrat HTTP stable, validation et DTO de réponse
parking/application         cas d'usage de recherche et port ParkingProvider
parking/domain              modèle normalisé et calcul géographique
parking/infrastructure      adaptateurs propres aux fournisseurs externes
```

`ParkingProvider` est la frontière importante du projet. Le cas d'usage ne connaît ni Data Fair, ni les noms de champs de Poitiers. `PoitiersParkingProvider` traduit le JSON amont vers le modèle interne ; le contrôleur traduit ensuite ce modèle vers le contrat public versionné. Cela permet aux formats amont et au contrat mobile d'évoluer indépendamment.

Les classes et méthodes qui portent le contrat ou une décision métier sont documentées avec Javadoc. Chaque champ statique possède un commentaire expliquant son rôle. Les noms expriment l'intention métier (`fetchAllParkings`, `providerResponse`, `nearbyParkingResponses`, `MAX_PROVIDER_RECORDS`) et évitent les termes génériques lorsqu'un nom plus précis est disponible.

La distance est calculée côté serveur avec la formule de Haversine. Les résultats hors rayon et les parkings dépourvus de coordonnées sont écartés, puis triés par distance. La source actuelle contient deux parkings sans géolocalisation (`GARE EFFIA` et `CORDELIERS`) : ils ne peuvent donc pas apparaître dans une recherche de proximité.

Les données externes sont considérées comme non fiables : champs obligatoires contrôlés, coordonnées analysées explicitement, capacité et disponibilité cohérentes, enregistrements invalides ignorés avec un avertissement. Les appels sortants ont des délais de connexion et de lecture courts. L'URL est une configuration serveur fixe, jamais une entrée utilisateur.

## Ajouter une ville ou un fournisseur

Un nouveau format nécessite un nouvel adaptateur qui implémente `ParkingProvider`, avec ses DTO d'intégration et son mapping. Il peut être activé par une propriété, sur le même principe que :

```java
@Component
@ConditionalOnProperty(name = "parking.provider", havingValue = "nouvelle-ville")
final class NouvelleVilleParkingProvider implements ParkingProvider {
    // Appel de la source et traduction vers List<Parking>
}
```

La configuration par défaut peut être surchargée par variables d'environnement :

| Propriété | Variable | Valeur par défaut |
|---|---|---|
| `parking.provider` | `PARKING_PROVIDER` | `poitiers` |
| `parking.providers.poitiers.base-url` | `PARKING_PROVIDERS_POITIERS_BASE_URL` | `https://data.grandpoitiers.fr` |
| `parking.providers.poitiers.dataset-path` | `PARKING_PROVIDERS_POITIERS_DATASET_PATH` | chemin Data Fair du jeu de données |
| `parking.providers.poitiers.connect-timeout` | `PARKING_PROVIDERS_POITIERS_CONNECT_TIMEOUT` | `2s` |
| `parking.providers.poitiers.read-timeout` | `PARKING_PROVIDERS_POITIERS_READ_TIMEOUT` | `3s` |

Si plusieurs villes devaient être servies simultanément, j'ajouterais un identifiant de zone dans la configuration de déploiement ou une résolution interne par coordonnées. Je préserverais le contrat de réponse ; le choix du fournisseur resterait côté serveur.

## Tests

La suite couvre :

- le calcul de distance et les bornes géographiques ;
- le filtrage et le tri du cas d'usage ;
- le contrat JSON du contrôleur et les erreurs de paramètres ;
- le mapping du format Poitiers, y compris un parking sans coordonnées et un enregistrement incohérent ;
- le démarrage du contexte Spring complet ;
- la génération du contrat OpenAPI et l'accès à Swagger UI.

## Limites identifiées et suite en production

Le périmètre de l'exercice ne comprend ni persistance ni authentification : cette API ne manipule aucune donnée privée et ne propose aucune écriture. Pour un service exploité en production, je compléterais selon les objectifs de disponibilité et de charge :

- cache court aligné sur la fréquence de mise à jour de la source, avec dernière valeur valide en repli ;
- circuit breaker et métriques sur latence, erreurs et fraîcheur des données ;
- limite explicite de taille de réponse amont au niveau du client HTTP ;
- tests de contrat exécutés régulièrement contre chaque fournisseur ;
- image OCI, pipeline CI, analyse de dépendances et déploiement redondé.

Je n'ai pas ajouté ces éléments afin de garder un service simple et proportionné au besoin actuel. Le port fournisseur constitue le point d'extension nécessaire sans introduire une abstraction pour chaque classe.
