package inventory;

import inventory.CompositePattern.Product;

public class CommandPattern {

    public interface CommandInterface {
        void execute();
        void undo();
    }

    public abstract static class AddCommand implements CommandInterface {
        @Override
        public void execute() {
            performExecute();
            String log = getExecuteLog();
            if (log != null) System.out.println("Command: " + log);
        }

        @Override
        public void undo() {
            performUndo();
            String log = getUndoLog();
            if (log != null) System.out.println("Undo: " + log);
        }

        protected abstract void performExecute();
        protected abstract void performUndo();
        protected abstract String getExecuteLog();
        protected abstract String getUndoLog();
    }

    public abstract static class RemoveCommand implements CommandInterface {
        @Override
        public void execute() {
            performExecute();
            String log = getExecuteLog();
            if (log != null) System.out.println("Command: " + log);
        }

        @Override
        public void undo() {
            performUndo();
            String log = getUndoLog();
            if (log != null) System.out.println("Undo: " + log);
        }

        protected abstract void performExecute();
        protected abstract void performUndo();
        protected abstract String getExecuteLog();
        protected abstract String getUndoLog();
    }

    public static class AddStockCommand extends AddCommand {
        private Product product;
        private int quantity;

        public AddStockCommand(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        @Override
        protected void performExecute() {
            product.restock(quantity);
        }

        @Override
        protected void performUndo() {
            int current = product.getStockLevel();
            product.setStockLevel(current - quantity);
        }

        @Override
        protected String getExecuteLog() {
            return null; // Logging handled in product.restock()
        }

        @Override
        protected String getUndoLog() {
            return "Removed " + quantity + " from " + product.getName();
        }
    }

    public static class RemoveStockCommand extends RemoveCommand {
        private Product product;
        private int quantity;

        public RemoveStockCommand(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        @Override
        protected void performExecute() {
            product.sell(quantity);
        }

        @Override
        protected void performUndo() {
            product.restock(quantity);
        }

        @Override
        protected String getExecuteLog() {
            return null; // Logging handled in product.sell()
        }

        @Override
        protected String getUndoLog() {
            return "Added back " + quantity + " to " + product.getName();
        }
    }

    public static class AddCategoryCommand extends AddCommand {
        private CompositePattern.ProductCategory parent;
        private CompositePattern.ProductCategory newCategory;
        private String name;

        public AddCategoryCommand(CompositePattern.ProductCategory parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        @Override
        protected void performExecute() {
            this.newCategory = new CompositePattern.ProductCategory(name);
            parent.add(newCategory);
        }

        @Override
        protected void performUndo() {
            if (newCategory != null) {
                parent.remove(newCategory);
            }
        }

        @Override
        protected String getExecuteLog() {
            return "Created Category '" + name + "' under '" + parent.getName() + "'";
        }

        @Override
        protected String getUndoLog() {
            return "Removed Category '" + name + "'";
        }
    }

    public static class AddProductTypeCommand extends AddCommand {
        private java.util.Map<String, FactoryPattern.IProductFactory> factories;
        private String typeName;
        private FactoryPattern.IProductFactory factory;

        public AddProductTypeCommand(java.util.Map<String, FactoryPattern.IProductFactory> factories, String typeName, FactoryPattern.IProductFactory factory) {
            this.factories = factories;
            this.typeName = typeName;
            this.factory = factory;
        }

        @Override
        protected void performExecute() {
            factories.put(typeName.toLowerCase(), factory);
        }

        @Override
        protected void performUndo() {
            factories.remove(typeName.toLowerCase());
        }

        @Override
        protected String getExecuteLog() {
            return "Registered Product Type '" + typeName + "'";
        }

        @Override
        protected String getUndoLog() {
            return "Unregistered Product Type '" + typeName + "'";
        }
    }

    public static class RemoveProductCommand extends RemoveCommand {
        private CompositePattern.ProductCategory parent;
        private CompositePattern.Product target;
        private int index;

        public RemoveProductCommand(CompositePattern.ProductCategory parent, CompositePattern.Product target) {
            this.parent = parent;
            this.target = target;
        }

        @Override
        protected void performExecute() {
            System.out.println("DEBUG: Executing RemoveProductCommand for " + target.getName());
            index = parent.getChildren().indexOf(target);
            if (index == -1) {
                System.out.println("DEBUG: Target product not found in parent's children list!");
            } else {
                System.out.println("DEBUG: Target found at index " + index + ". Removing...");
            }
            parent.remove(target);
        }

        @Override
        protected void performUndo() {
            if (index >= 0 && index <= parent.getChildren().size()) {
                parent.getChildren().add(index, target);
            } else {
                parent.add(target);
            }
        }

        @Override
        protected String getExecuteLog() {
            return "Removed Product '" + target.getName() + "'";
        }

        @Override
        protected String getUndoLog() {
            return "Restored Product '" + target.getName() + "'";
        }
    }

    public static class RemoveCategoryCommand extends RemoveCommand {
        private CompositePattern.ProductCategory parent;
        private CompositePattern.ProductCategory target;
        private int index;

        public RemoveCategoryCommand(CompositePattern.ProductCategory parent, CompositePattern.ProductCategory target) {
            this.parent = parent;
            this.target = target;
        }

        @Override
        protected void performExecute() {
            System.out.println("DEBUG: Executing RemoveCategoryCommand for " + target.getName());
            index = parent.getChildren().indexOf(target);
            if (index == -1) {
                System.out.println("DEBUG: Target category not found in parent's children list!");
            } else {
                System.out.println("DEBUG: Target found at index " + index + ". Removing...");
            }
            parent.remove(target);
        }

        @Override
        protected void performUndo() {
            if (index >= 0 && index <= parent.getChildren().size()) {
                parent.getChildren().add(index, target);
            } else {
                parent.add(target);
            }
        }

        @Override
        protected String getExecuteLog() {
            return "Removed Category '" + target.getName() + "'";
        }

        @Override
        protected String getUndoLog() {
            return "Restored Category '" + target.getName() + "'";
        }
    }

    public static class RemoveProductTypeCommand extends RemoveCommand {
        private java.util.Map<String, FactoryPattern.IProductFactory> factories;
        private String typeName;
        private FactoryPattern.IProductFactory factory;

        public RemoveProductTypeCommand(java.util.Map<String, FactoryPattern.IProductFactory> factories, String typeName) {
            this.factories = factories;
            this.typeName = typeName;
        }

        @Override
        protected void performExecute() {
            System.out.println("DEBUG: Executing RemoveProductTypeCommand for " + typeName);
            this.factory = factories.get(typeName.toLowerCase());
            if (this.factory == null) {
                System.out.println("DEBUG: Factory for " + typeName + " not found in map!");
            } else {
                System.out.println("DEBUG: Factory found. Removing...");
            }
            factories.remove(typeName.toLowerCase());
        }

        @Override
        protected void performUndo() {
            if (factory != null) {
                factories.put(typeName.toLowerCase(), factory);
            }
        }

        @Override
        protected String getExecuteLog() {
            return "Removed Product Type '" + typeName + "'";
        }

        @Override
        protected String getUndoLog() {
            return "Reregistered Product Type '" + typeName + "'";
        }
    }
}
