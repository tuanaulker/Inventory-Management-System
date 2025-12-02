# Project Development Requirements

This document outlines the necessary tools and libraries required for developers to build, run, and test this project.

## 1. Core Prerequisites

### Java Development Kit (JDK)
*   **Version**: JDK 8 or higher is required.
*   **Recommended**: JDK 17 or JDK 21 (LTS versions).
*   **Download**: [Adoptium Temurin](https://adoptium.net/) or [Oracle JDK](https://www.oracle.com/java/technologies/downloads/).
*   **Verification**: Run `java -version` and `javac -version` in your terminal to verify installation.

### Operating System
*   The project is platform-independent and runs on **Windows**, **macOS**, and **Linux**.
*   **macOS/Linux**: The provided commands in README use standard shell syntax (`mkdir`, `rm`, `cp`).
*   **Windows**: You may need to use PowerShell or adjust file path separators (e.g., `\` instead of `/`) if running commands manually.

## 2. External Libraries (Dependencies)

This project is designed to be lightweight and uses the standard Java library for the main application. However, testing requires external libraries.

### Runtime Dependencies
*   **None**. The application uses the built-in `com.sun.net.httpserver` package for the web server and standard IO for the database.

### Test Dependencies (Optional)
To compile and run the unit tests (`src/inventory/FactoryTest.java`), you need the **JUnit 5 (Jupiter)** platform.

If you are not using an IDE (like IntelliJ or Eclipse) that bundles JUnit, you must download the following JARs and add them to your classpath:

1.  `junit-jupiter-api-5.x.x.jar`
2.  `junit-jupiter-engine-5.x.x.jar`
3.  `junit-platform-commons-1.x.x.jar`
4.  `opentest4j-1.x.x.jar`
5.  `apiguardian-api-1.x.x.jar`

**Or simply use the standalone console runner:**
*   `junit-platform-console-standalone-1.x.x.jar`

## 3. Development Environment

### Recommended IDEs
While not strictly required, using an IDE simplifies dependency management and code navigation:
*   **Visual Studio Code**: Install the "Extension Pack for Java".
*   **IntelliJ IDEA**: Community or Ultimate edition.
*   **Eclipse IDE**: For Java Developers.

### Project Structure
The project follows a standard source layout but does not use a build tool like Maven or Gradle by default.
*   **Source**: `src/inventory/*.java`
*   **Output**: `bin/` (Created during compilation)
*   **Data**: `inventory_db.txt` (Created at runtime)
