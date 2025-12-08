package inventory;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inventory.CompositePattern.*;
import inventory.FactoryPattern.*;
import inventory.StatePattern.*;
import inventory.ObserverPattern.*;
import inventory.CommandPattern.*;

public class FactoryTest {

    private IProductFactory electronicFactory;
    private IProductFactory apparelFactory;
    private InventoryManager manager;

    @BeforeEach
    void setUp() {
        electronicFactory = new ElectronicProductFactory();
        apparelFactory = new ApparelProductFactory();
        manager = new InventoryManager();
    }


    @Test
    void testElectronicFactoryCreatesCorrectProductType() {
        Product p = electronicFactory.createProduct("LaptopX", 1200, 10, 5);
        assertNotNull(p);
        assertTrue(p instanceof inventory.ElectronicProduct, "Electronic factory should produce ElectronicProduct.");
    }

    @Test
    void testApparelFactoryCreatesCorrectProductType() {
        Product p = apparelFactory.createProduct("Shirt", 30, 20, 5);
        assertNotNull(p);
        assertTrue(p instanceof inventory.ApparelProduct, "Apparel factory should produce ApparelProduct.");
    }

    @Test
    void testStateTransitions() {
        Product p = electronicFactory.createProduct("TestItem", 100, 10, 5);
        assertTrue(p.getState() instanceof InStock);

        p.sell(6);
        assertEquals(4, p.getStockLevel());
        assertTrue(p.getState() instanceof LowStock);

        p.sell(4);
        assertEquals(0, p.getStockLevel());
        assertTrue(p.getState() instanceof OutOfStock);

        p.sell(1);
        assertEquals(0, p.getStockLevel(), "OutOfStock should block further sales.");

        p.restock(10);
        assertEquals(10, p.getStockLevel());
        assertTrue(p.getState() instanceof InStock);
    }

    @Test
    void testObserverNotification() {
        Product p = electronicFactory.createProduct("ObservedItem", 100, 10, 5);
        p.registerObs(manager);

        p.setStockLevel(2);
        assertTrue(p.getState() instanceof LowStock, "State must transition to LowStock.");
    }

    @Test
    void testCompositeValue() {
        ProductCategory root = new ProductCategory("Root");

        Product p1 = electronicFactory.createProduct("E-P1", 100, 2, 0);
        Product p2 = apparelFactory.createProduct("A-P2", 50, 4, 0);

        root.add(p1);
        root.add(p2);

        assertEquals(400, root.getValue(), "Composite value calculation failed.");
    }

    @Test
    void testCommandExecutionAndUndo() {
        Product p = electronicFactory.createProduct("CmdItem", 100, 10, 5);

        CommandInterface sellCmd = new RemoveStock(p, 2);
        manager.executeCommand(sellCmd);
        assertEquals(8, p.getStockLevel(), "Execution of RemoveStock failed.");

        manager.undoLastCommand();
        assertEquals(10, p.getStockLevel(), "Undo of RemoveStock failed.");

        CommandInterface addCmd = new AddStock(p, 5);
        manager.executeCommand(addCmd);
        assertEquals(15, p.getStockLevel(), "Execution of AddStock failed.");

        manager.undoLastCommand();
        assertEquals(10, p.getStockLevel(), "Undo of AddStock failed.");
    }

    @Test
    void testFailedCommandIsNotUndoable() {
        Product p = electronicFactory.createProduct("FailCmd", 100, 0, 5);

        CommandInterface sellFail = new RemoveStock(p, 1);
        manager.executeCommand(sellFail);

        assertEquals(0, p.getStockLevel(), "Stock should remain 0 after failed sale.");
    }
}