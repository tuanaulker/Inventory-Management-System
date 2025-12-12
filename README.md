# Inventory Management System

A robust, design-pattern-driven Inventory Management System featuring a hierarchical product structure, real-time web dashboard, and persistent storage.

## 🏗 Design Patterns Implemented

This project showcases the practical application of five key design patterns:

1.  **Composite Pattern**: Structures the inventory as a tree hierarchy (`Type` -> `Category` -> `Product`), allowing individual products and categories to be treated uniformly.
2.  **Factory Pattern**: Encapsulates product creation logic (`ElectronicProductFactory`, `ApparelProductFactory`), making it easy to introduce new product types.
3.  **Observer Pattern**: Enables real-time updates. The `InventoryManager` observes products and triggers alerts or logs when stock levels change (e.g., Low Stock, Out of Stock).
4.  **Command Pattern**: Encapsulates all inventory operations (Add Stock, Sell, Create Category, etc.) as command objects, enabling **Undo** functionality.
5.  **State Pattern**: Manages product lifecycle states (`InStock`, `LowStock`, `OutOfStock`) dynamically based on quantity.

## 🚀 Features

*   **Web-Based Dashboard**: A responsive GUI built with HTML/TailwindCSS to visualize the inventory tree.
*   **Hierarchical Organization**: Organize products deeply into Types and Categories.
*   **Stock Management**:
    *   Add and Sell stock with custom quantities.
    *   Automatic state transitions (e.g., product goes "Out of Stock" when quantity hits 0).
    *   **Undo** the last operation.
*   **Dynamic Creation**: Register new Product Types, Categories, and Products directly from the UI.
*   **Validation**: Prevents duplicate categories and enforces valid naming conventions.
*   **Persistence**: Automatically saves and loads inventory state from `inventory_db.txt`.

## 📂 Project Structure

The source code is located in `src/inventory/`:

*   **Core Logic**:
    *   `Main.java`: Entry point for initialization and CLI demo.
    *   `SimpleWebServer.java`: HTTP server handling API requests and serving the frontend.
    *   `Database.java`: Manages file I/O for `inventory_db.txt`.
*   **Pattern Implementations**:
    *   `CompositePattern.java`: Defines `ProductComponent`, `ProductCategory`, and `Product`.
    *   `FactoryPattern.java`: Defines factories for creating different product types.
    *   `ObserverPattern.java`: Handles event notifications and logging.
    *   `CommandPattern.java`: Implements the command execution and undo logic.
    *   `StatePattern.java`: Defines the various states of a product.
    *   `ElectronicProduct.java` / `ApparelProduct.java`: Concrete product implementations.

## 🛠 How to Run

### Prerequisites
*   Java Development Kit (JDK) 8 or higher.
*   `gson-2.10.1.jar` (Included in `src/`).

### Option 1: Quick Start (One-Liner)
Run this command in your terminal to compile and start the server immediately:

```bash
mkdir -p bin && javac -cp src/gson-2.10.1.jar -d bin src/inventory/*.java && echo "Starting Web Server..." && java -cp bin:src/gson-2.10.1.jar inventory.SimpleWebServer
```

### Option 2: Manual Compilation

1.  **Compile the project:**
    ```bash
    mkdir -p bin
    javac -cp src/gson-2.10.1.jar -d bin src/inventory/*.java
    ```

2.  **Run the Web Server:**
    ```bash
    java -cp bin:src/gson-2.10.1.jar inventory.SimpleWebServer
    ```
    Access the dashboard at: **[http://localhost:8000](http://localhost:8000)**

3.  **Run the CLI Demo:**
    ```bash
    java -cp bin:src/gson-2.10.1.jar inventory.Main
    ```

## 💾 Data Persistence
The system maintains a local database file named `inventory_db.txt`.
*   **Loading**: The application attempts to load this file on startup. If missing, it initializes a default inventory.
*   **Saving**: Changes made via the web interface or CLI are automatically saved to this file.

---
*Developed for Design Patterns Course - Fall 2025*
