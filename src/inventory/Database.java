package inventory;

import java.io.*;
import java.util.*;
import inventory.CompositePattern.*;
import inventory.FactoryPattern.*;

public class Database {
    private static final String DB_FILE = "inventory_db.txt";

    public static void save(ProductCategory root) {
        try (PrintWriter writer = new PrintWriter(new File(DB_FILE))) {
            writeRecursive(writer, root, "null");
            System.out.println("Database saved to " + DB_FILE);
        } catch (IOException e) {
            System.err.println("Error saving database: " + e.getMessage());
        }
    }

    private static void writeRecursive(PrintWriter writer, ProductComponent component, String parentName) {
        if (component instanceof ProductCategory) {
            ProductCategory cat = (ProductCategory) component;
            writer.println("C," + cat.getName() + "," + parentName);
            for (ProductComponent child : cat.getChildren()) {
                writeRecursive(writer, child, cat.getName());
            }
        } else if (component instanceof Product) {
            Product p = (Product) component;
            String productType = p.getClass().getSimpleName();
            writer.println("P," + p.getName() + "," + p.getPrice() + "," + p.getStockLevel() + "," + p.getThreshold() + "," + parentName + "," + productType);
        }
    }

    public static ProductCategory load() {
        File file = new File(DB_FILE);
        if (!file.exists()) {
            return null;
        }

        Map<String, ProductCategory> categories = new HashMap<>();
        ProductCategory root = null;

        Map<String, IProductFactory> factories = new HashMap<>();
        factories.put("ElectronicProduct", new ElectronicProductFactory());
        factories.put("ApparelProduct", new ApparelProductFactory());

        IProductFactory defaultFactory = new ElectronicProductFactory();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String type = parts[0];
                String name = parts[1];

                if (parts.length < 5) continue;

                if (type.equals("C")) {
                    String parentName = parts[parts.length - 1];
                    ProductCategory cat = new ProductCategory(name);
                    categories.put(name, cat);

                    if (parentName.equals("null")) {
                        root = cat;
                    } else {
                        ProductCategory parent = categories.get(parentName);
                        if (parent != null) {
                            parent.add(cat);
                        }
                    }
                } else if (type.equals("P")) {
                    int price = Integer.parseInt(parts[2]);
                    int stock = Integer.parseInt(parts[3]);
                    int threshold = Integer.parseInt(parts[4]);

                    String parentName = parts[5];
                    String productType = (parts.length > 6) ? parts[6] : "ElectronicProduct";
                    IProductFactory selectedFactory = factories.getOrDefault(productType, defaultFactory);
                    Product p = selectedFactory.createProduct(name, price, stock, threshold);
                    ProductCategory parent = categories.get(parentName);
                    if (parent != null) {
                        parent.add(p);
                    }
                }
            }
            return root;
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading database: " + e.getMessage());
            return null;
        }
    }
}
