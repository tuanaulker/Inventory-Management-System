# Inventory Management System - Design Patterns Demo

This project demonstrates the implementation of several design patterns in a Java-based Inventory Management System, featuring a web-based dashboard.

## Design Patterns Implemented

1.  **State Pattern**: Manages the state of a product (`InStock`, `LowStock`, `OutOfStock`) based on inventory levels.
2.  **Composite Pattern**: Organizes products into categories and hierarchies (`ProductCategory`, `Product`), allowing uniform treatment of individual objects and compositions.
3.  **Observer Pattern**: Notifies the `InventoryManager` (Observer) when a `Product` (Subject) changes state.
4.  **Command Pattern**: Encapsulates stock operations (`AddStock`, `RemoveStock`) as objects, supporting execution and undo functionality.
5.  **Factory Pattern**: Provides an interface (`IProductFactory`) for creating product objects, decoupling client code from specific product instantiation.

## Project Structure

The source code is located in `src/inventory/`.

*   `SimpleWebServer.java`: A lightweight HTTP server providing a web dashboard.
*   `Database.java`: Handles loading and saving inventory data to `inventory_db.txt`.
*   `StatePattern.java`: Contains the `State` interface and concrete states.
*   `CompositePattern.java`: Contains the `ProductComponent` interface, `ProductCategory`, and `Product`.
*   `ObserverPattern.java`: Contains the `ObserverInterface` and `InventoryManager`.
*   `CommandPattern.java`: Contains the `CommandInterface` and concrete commands.
*   `FactoryPattern.java`: Contains the `IProductFactory` interface and `ElectronicProductFactory`.
*   `Main.java`: A CLI entry point demonstrating the usage of all patterns.

## How to Run

### Prerequisites
*   Java Development Kit (JDK) 8 or higher.

### 1. Compile the Project
We use a `bin` directory to keep compiled classes separate from source code.

```bash
# Create bin directory if it doesn't exist
mkdir -p bin

# Compile all source files to the bin directory
javac -d bin src/inventory/*.java
```
*Note: If you encounter errors related to JUnit (tests), you can exclude `FactoryTest.java` or ensure JUnit is in your classpath.*

### 2. Run the Web Server (Recommended)
The web server provides a visual interface to interact with the inventory.

```bash
java -cp bin inventory.SimpleWebServer
```
Once started, open your browser and navigate to: **[http://localhost:8000](http://localhost:8000)**

### 3. Run the CLI Demo
To see a console-based demonstration of the patterns in action:

```bash
java -cp bin inventory.Main
```

## Data Persistence
The application stores inventory data in `inventory_db.txt`. This file is automatically loaded when the server starts and updated when changes are made.

## One Line Command to execute project
```bash
cd [FolderName] && mkdir -p bin && javac -d bin src/inventory/CommandPattern.java src/inventory/CompositePattern.java src/inventory/Database.java src/inventory/FactoryPattern.java src/inventory/Main.java src/inventory/ObserverPattern.java src/inventory/SimpleWebServer.java src/inventory/StatePattern.java && echo "Compilation successful. Starting Web Server..." && java -cp bin inventory.SimpleWebServer
```