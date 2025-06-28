# 🤖 MecaBot - Chatbot d'Ingénierie Mécanique

Un chatbot intelligent spécialisé en ingénierie mécanique, développé en Java avec interface Android et backend Spring Boot, intégrant Dialogflow, OpenAI, Wikipedia et d'autres sources de données techniques.

## 🎯 Fonctionnalités Principales

### 📐 Calculs de Résistance des Matériaux
- **Contraintes normales** : σ = F/A
- **Contraintes de cisaillement** : τ = V/A
- **Déformations** : ε = ΔL/L₀
- **Flèche des poutres** : calculs selon différents types de charges
- **Flambement** : charge critique d'Euler
- **Torsion** : contraintes et angles de torsion
- **Fatigue** : calculs de durée de vie

### 🔧 Propriétés des Matériaux
- **Base de données intégrée** : acier, aluminium, béton, bois, plastiques
- **Propriétés mécaniques** : module d'Young, limite élastique, résistance ultime
- **Propriétés physiques** : densité, coefficient de Poisson
- **Propriétés thermiques** : conductivité, dilatation thermique

### 🌊 Mécanique des Fluides
- **Débits** : volumique et massique
- **Pertes de charge** : linéaires et singulières (Darcy-Weisbach)
- **Équation de Bernoulli** : applications pratiques
- **Nombre de Reynolds** : régimes d'écoulement

### 🔥 Thermodynamique
- **Puissance thermique** : P = m·c·ΔT/t
- **Cycles thermodynamiques** : Carnot, Otto, Diesel
- **Transferts thermiques** : conduction, convection, rayonnement
- **Rendements** : machines thermiques

### 🔄 Conversions d'Unités
- **Pression** : bar, psi, Pa, atm
- **Force** : N, kN, lbf, kgf
- **Contrainte** : MPa, psi, bar
- **Température** : °C, °F, K
- **Puissance** : W, kW, hp, ch

## 🏗️ Architecture

### Frontend (Android)
- **Interface moderne** style Messenger avec animations
- **Organisation par domaines** : Résistance des matériaux, Fluides, Thermodynamique, Matériaux
- **Bulles de chat stylisées** avec avatars et animations
- **Indicateur de frappe** animé
- **Responsive design** adapté aux différentes tailles d'écran

### Backend (Spring Boot)
- **API REST** pour la communication avec Dialogflow
- **Moteur de calcul** multi-domaines
- **Base de données matériaux** en JSON
- **Intégration d'APIs externes** : OpenAI, Wikipedia, Bing Search
- **Gestion des unités** et conversions automatiques

### Intelligence Artificielle
- **Dialogflow** pour la compréhension du langage naturel
- **OpenAI GPT** pour les explications détaillées
- **Wikipedia** pour les définitions et contextes
- **Bing Search** pour les ressources techniques

## 🚀 Installation

### Prérequis
- Android Studio (version récente)
- Java JDK 11 ou supérieur
- Compte Google Cloud (pour Dialogflow)
- Clé API OpenAI (optionnel)
- Clé API Bing Search (optionnel)

### Étapes d'Installation

1. **Cloner le repository**
   ```bash
   git clone https://github.com/votre-username/mecabot.git
   cd mecabot
   ```

2. **Configuration Dialogflow**
   - Créer un agent sur [Dialogflow Console](https://dialogflow.cloud.google.com/)
   - Configurer les intents et entités (voir section Configuration)
   - Activer le webhook avec l'URL de votre backend

3. **Configuration des APIs**
   - Obtenir une clé OpenAI sur [OpenAI Platform](https://platform.openai.com/api-keys)
   - Obtenir une clé Bing Search sur [Azure Portal](https://portal.azure.com/)
   - Mettre à jour les clés dans `WebhookController.java`

4. **Lancer l'application Android**
   ```bash
   # Ouvrir le projet dans Android Studio
   # Synchroniser Gradle
   # Lancer sur émulateur ou appareil
   ```

5. **Lancer le backend Spring Boot**
   ```bash
   cd mechanique/app
   ./gradlew bootRun
   ```

## ⚙️ Configuration

### Dialogflow
Créer les intents suivants :
- `CalculContrainte` : calculs de contraintes mécaniques
- `CalculDebit` : calculs de débit fluide
- `CalculPerteCharge` : pertes de charge
- `CalculPuissanceThermique` : puissance thermique
- `ConversionUnite` : conversions d'unités
- `InfoMateriau` : informations sur les matériaux

### Entités Dialogflow
- `@unite_force` : N, kN, lbf, kgf
- `@unite_surface` : mm², cm², m²
- `@unite_longueur` : mm, cm, m
- `@materiau` : acier, aluminium, béton, bois, plastique
- `@valeur` : nombre (système)

## 📱 Utilisation

### Interface Android
1. **Sélectionner un domaine** dans le spinner
2. **Taper une question** dans le champ de saisie
3. **Envoyer** avec le bouton ou la touche Entrée
4. **Consulter la réponse** enrichie du chatbot

### Exemples de Questions
- "Calcule la contrainte pour 5000N sur 200mm²"
- "Débit d'eau dans un tuyau DN50 à 3m/s"
- "Module d'Young de l'acier"
- "Convertir 25 bar en psi"
- "Puissance thermique pour 10kg d'eau, ΔT = 20K en 600s"

## 🛠️ Technologies Utilisées

### Frontend
- **Android** : Java, XML Layouts
- **Animations** : LayoutTransition, ObjectAnimator
- **UI/UX** : Material Design, bulles de chat stylisées

### Backend
- **Spring Boot** : Framework Java
- **REST API** : Communication HTTP
- **JSON** : Base de données matériaux
- **HTTP Client** : Appels APIs externes

### Intelligence Artificielle
- **Dialogflow** : NLP et compréhension
- **OpenAI GPT** : Explications intelligentes
- **Wikipedia API** : Définitions et contextes
- **Bing Search API** : Recherche web

### Outils de Développement
- **Android Studio** : IDE principal
- **Gradle** : Build automation
- **Git** : Version control

## 📊 Structure du Projet

```
mechanique/
├── app/
│   ├── src/main/
│   │   ├── java/com/todolist/mechanique/
│   │   │   ├── MainActivity.java          # Interface Android
│   │   │   └── webhook/
│   │   │       └── WebhookController.java # Backend Spring Boot
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml     # Layout principal
│   │   │   └── drawable/                  # Ressources graphiques
│   │   └── AndroidManifest.xml
│   ├── build.gradle                       # Configuration Gradle
│   └── materiaux.json                     # Base données matériaux
└── README.md
```

## 🔧 Développement

### Ajouter un Nouveau Calcul
1. Créer l'intent Dialogflow correspondant
2. Ajouter la logique dans `WebhookController.java`
3. Tester avec l'interface Android

### Ajouter un Nouveau Matériau
1. Ajouter les propriétés dans `materiaux.json`
2. Tester avec la question "InfoMateriau"

### Personnaliser l'Interface
1. Modifier `activity_main.xml` pour le layout
2. Modifier `MainActivity.java` pour la logique
3. Ajouter des ressources dans `res/drawable/`

## 🤝 Contribution

Les contributions sont les bienvenues ! Pour contribuer :

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/AmazingFeature`)
3. Commit les changements (`git commit -m 'Add some AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## 📝 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## 🙏 Remerciements

- **Dialogflow** pour la compréhension du langage naturel
- **OpenAI** pour les explications intelligentes
- **Wikipedia** pour les définitions et contextes
- **Engineering Toolbox** et **MatWeb** pour les ressources techniques
- **Spring Boot** pour le framework backend
- **Android** pour la plateforme mobile

## 📞 Support

Pour toute question ou problème :
- Ouvrir une issue sur GitHub
- Consulter la documentation Dialogflow
- Vérifier la configuration des APIs

---

**MecaBot** - Votre assistant intelligent en ingénierie mécanique ! 🚀
