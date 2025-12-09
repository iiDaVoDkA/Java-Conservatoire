# Diagrammes UML - Conservatoire Virtuel

Ce dossier contient tous les diagrammes UML du projet au format PlantUML.

## 📁 Liste des Fichiers

### Diagrammes de Classes
| Fichier | Description |
|---------|-------------|
| `class-diagram-person.puml` | Hiérarchie Person (Student, Teacher) |
| `class-diagram-service.puml` | Hiérarchie Service (CoursePackage, IndividualLesson, InstrumentRental) |
| `class-diagram-scheduling.puml` | Hiérarchie ScheduledActivity (Lesson, RoomBooking) |
| `class-diagram-resources.puml` | Classes Room et Instrument |
| `class-diagram-exam.puml` | Classe Exam et ExamRegistration |
| `class-diagram-billing.puml` | Classes Invoice et Payment |
| `class-diagram-complete.puml` | Vue d'ensemble complète |

### Diagrammes de Séquence
| Fichier | Description |
|---------|-------------|
| `sequence-schedule-lesson.puml` | Planification d'une leçon |
| `sequence-cancel-lesson.puml` | Annulation avec règle des 24h |

### Diagrammes d'Activité
| Fichier | Description |
|---------|-------------|
| `activity-exam-registration.puml` | Processus d'inscription à un examen |
| `activity-lesson-completion.puml` | Complétion d'une leçon |
| `activity-payment-process.puml` | Processus de paiement |

### Autres Diagrammes
| Fichier | Description |
|---------|-------------|
| `component-diagram.puml` | Architecture en composants |
| `usecase-diagram.puml` | Cas d'utilisation |

## 🔧 Comment Compiler

### Option 1: PlantUML en ligne
1. Aller sur [PlantUML Web Server](https://www.plantuml.com/plantuml/uml/)
2. Copier-coller le contenu du fichier `.puml`
3. Télécharger l'image PNG ou SVG

### Option 2: PlantUML CLI
```bash
# Installer PlantUML (macOS)
brew install plantuml

# Compiler un fichier
plantuml class-diagram-person.puml

# Compiler tous les fichiers
plantuml *.puml

# Générer en SVG (meilleure qualité)
plantuml -tsvg *.puml

# Générer en PDF
plantuml -tpdf *.puml
```

### Option 3: Extension VS Code
1. Installer l'extension "PlantUML" dans VS Code
2. Ouvrir un fichier `.puml`
3. `Alt+D` pour prévisualiser
4. Clic droit → "Export Current Diagram"

### Option 4: IntelliJ IDEA
1. Installer le plugin "PlantUML Integration"
2. Ouvrir le fichier `.puml`
3. Le diagramme s'affiche automatiquement

## 📥 Générer Toutes les Images

Script bash pour générer tous les diagrammes :

```bash
#!/bin/bash
cd /Users/hassen/Downloads/java-project/report/figures

# Créer dossier output
mkdir -p output

# Générer PNG
for file in *.puml; do
    plantuml -o output "$file"
done

# Ou générer SVG (recommandé pour LaTeX)
for file in *.puml; do
    plantuml -tsvg -o output "$file"
done

echo "Diagrammes générés dans le dossier output/"
```

## 📄 Utilisation dans LaTeX

```latex
\usepackage{graphicx}

% Pour PNG
\begin{figure}[H]
    \centering
    \includegraphics[width=0.9\textwidth]{figures/output/class-diagram-person.png}
    \caption{Diagramme de Classes - Hiérarchie Person}
\end{figure}

% Pour SVG (nécessite package svg)
\usepackage{svg}
\begin{figure}[H]
    \centering
    \includesvg[width=0.9\textwidth]{figures/output/class-diagram-person}
    \caption{Diagramme de Classes - Hiérarchie Person}
\end{figure}
```

## 🎨 Personnalisation

Les diagrammes utilisent un thème cohérent :
- **Classes** : Bleu (#E3F2FD, #1565C0)
- **Interfaces** : Vert (#E8F5E9, #2E7D32)
- **Classes abstraites** : Orange (#FFF3E0, #E65100)

Pour modifier les couleurs, éditez les paramètres `skinparam` au début de chaque fichier.

