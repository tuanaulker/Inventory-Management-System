# Project Development Requirements

This document outlines the necessary tools and libraries required for developers to build, run and test this project.

## 1. Core Prerequisites

### Java Development Kit (JDK)
*   **Version**: JDK 8 or higher is required.
*   **Recommended**: JDK 17 or JDK 21 (LTS versions).
*   **Download**: [Adoptium Temurin](https://adoptium.net/) or [Oracle JDK](https://www.oracle.com/java/technologies/downloads/).
*   **Verification**: Run `java -version` and `javac -version` in your terminal to verify installation.

### Operating System
*   The project is platform-independent and runs on **Windows**, **macOS** and **Linux**.
*   **macOS/Linux**: The provided commands in README use standard shell syntax (`mkdir`, `javac`, `java`).
*   **Windows**: Use Command Prompt or PowerShell. Adjust classpath separators if needed (`;` instead of `:`).

## 2. External Libraries (Dependencies)

### Runtime Dependencies
*   **Gson 2.10.1**: Used for JSON serialization in the web API. The JAR file (`gson-2.10.1.jar`) is included in the `src/` directory.

### Test Dependencies
*   **None**: This project does not include automated unit tests. All testing is performed through the web interface and CLI demonstration.

## 3. Development Environment

### Recommended IDEs
While not strictly required, using an IDE simplifies development:
*   **Visual Studio Code**: Install the "Extension Pack for Java".
*   **IntelliJ IDEA**: Community or Ultimate edition.
*   **Eclipse IDE**: For Java Developers.

### Project Structure
The project follows a standard source layout:
*   **Source**: `src/inventory/*.java`
*   **Web Assets**: `src/web/index.html`, `src/web/style.css`
*   **Library**: `src/gson-2.10.1.jar`
*   **Output**: `bin/` (Created during compilation)
*   **Data**: `inventory_db.txt` (Created at runtime)

## 4. Build and Run

### Compilation
```bash
mkdir -p bin
javac -cp src/gson-2.10.1.jar -d bin src/inventory/*.java
```

### Execution
**Web Server:**
```bash
java -cp bin:src/gson-2.10.1.jar inventory.SimpleWebServer
```
*Note: On Windows, use `;` instead of `:` in classpath.*

**CLI Demo:**
```bash
java -cp bin:src/gson-2.10.1.jar inventory.Main
```

## 5. Browser Requirements

The web interface requires a modern web browser with JavaScript enabled:
*   Chrome 90+
*   Firefox 88+
*   Safari 14+
*   Edge 90+

The interface uses:
*   Tailwind CSS (loaded from CDN)
*   Lucide Icons (loaded from CDN)
*   Modern JavaScript (ES6+)