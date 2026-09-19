# Code Checker

Code Checker is a JavaFX desktop application for reviewing source code quality. It analyzes a code file with a set of quality rules, reports violations, and calculates a quality score. The application also includes user accounts, developer profiles, analysis history, reports, a leaderboard, achievements, and settings.

## Features

- Analyze source code for common quality problems.
- Detect overly long methods.
- Detect excessive nesting depth.
- Check naming conventions.
- Detect missing null checks.
- Detect duplicate logic.
- Calculate a score from 0 to 100 based on violation severity.
- Save developer profiles and analysis reports.
- Register users, log in, change passwords, and delete accounts.
- View the home dashboard, profile, leaderboard, achievements, and settings screens.

## Technologies

- Java 25
- JavaFX 21
- Maven
- JUnit 5
- Java modules

## Download the Project

### Option 1: Clone with Git

Install Git for Windows, open PowerShell, and run:

```powershell
git clone https://github.com/shadabmarwat00-collab/code-checker.git
cd code-checker
```

### Option 2: Download ZIP

1. Open the repository on GitHub:
   https://github.com/shadabmarwat00-collab/code-checker
2. Click the green **Code** button.
3. Select **Download ZIP**.
4. Extract the ZIP file.
5. Open the extracted `code-checker` folder in VS Code or IntelliJ IDEA.

## Requirements

Install the following before running the application:

- JDK 25
- Git, if cloning with Git
- Internet access for Maven to download dependencies on the first run

Check Java with:

```powershell
java -version
```

The output should show Java 25.

## Run on Windows

Open a terminal in the project folder:

```powershell
cd E:\projectOOP
```

Set `JAVA_HOME` for the current terminal and start the JavaFX application:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.2"
.\mvnw.cmd javafx:run
```

The application window should open with the login and registration screen.

### Alternative Command Prompt Command

If you are using Command Prompt instead of PowerShell, run:

```cmd
cd /d E:\projectOOP
set JAVA_HOME=C:\Program Files\Java\jdk-25.0.2
mvnw.cmd javafx:run
```

## Build and Test

To compile the project and run tests:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.2"
.\mvnw.cmd clean test
```

To create a package without running tests:

```powershell
.\mvnw.cmd package -DskipTests
```

## Project Structure

```text
src/main/java/com/example/projectoop/
  exceptions/   Application-specific exceptions
  models/       Users, profiles, sessions, and violations
  rules/        Code-quality analysis rules
  services/     Parsing, analysis, scoring, profiles, users, and reports
  ui/           JavaFX screens and application UI

profiles/       Saved developer profiles
reports/        Generated analysis reports
data/           Local application data
```

## Troubleshooting

### `JAVA_HOME not found`

Set `JAVA_HOME` before running Maven:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.2"
```

Update the path if Java is installed in a different folder.

### `java is not recognized`

Install JDK 25 and restart VS Code. Then verify:

```powershell
java -version
```

### Maven downloads dependencies

The first build may take a few minutes while Maven downloads JavaFX and other dependencies. Wait for the download to finish and run the command again if necessary.

### The application does not open

Make sure you run `javafx:run` from the project root, the folder containing `pom.xml`, and that no previous Maven process is still running.

## GitHub Updates

After making changes to the project:

```powershell
cd E:\projectOOP
git add .
git commit -m "Update project"
git push
```
