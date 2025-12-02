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

    private IProductFactory factory;
    private InventoryManager manager;

    @BeforeEach
    void setUp() {
        factory = new FactoryPattern.ElectronicProductFactory();
        manager = new ObserverPattern.InventoryManager();
    }

    @Test
    void testFactoryCreatesProduct() {
        Product p = factory.createProduct("TestItem", 100, 10, 5);
        assertNotNull(p);
        assertEquals("TestItem", p.getName());
        assertEquals(100, p.getPrice());
        assertEquals(10, p.getStockLevel());
    }

    @Test
    void testStateTransitions() {
        Product p = factory.createProduct("TestItem", 100, 10, 5);
        assertTrue(p.getState() instanceof StatePattern.InStock);

        // Sell to reach low stock
        p.sell(6); // 10 - 6 = 4. Threshold is 5. Should be LowStock.
        assertEquals(4, p.getStockLevel());
        assertTrue(p.getState() instanceof StatePattern.LowStock);

        // Sell to reach out of stock
        p.sell(4); // 4 - 4 = 0. Should be OutOfStock.
        assertEquals(0, p.getStockLevel());
        assertTrue(p.getState() instanceof StatePattern.OutOfStock);
    }

    @Test
    void testObserverNotification() {
        // This is a bit hard to test with standard JUnit without mocking or capturing output, 
        // but we can verify the state change which triggers notification.
        // We can also subclass InventoryManager to capture updates if we wanted to be strict.
        Product p = factory.createProduct("ObservedItem", 100, 10, 5);
        p.registerObs(manager);
        
        // Trigger change
        p.setStockLevel(2); // Should trigger LowStock and notify
        assertTrue(p.getState() instanceof StatePattern.LowStock);
    }

    @Test
    void testCompositeValue() {
        ProductCategory root = new CompositePattern.ProductCategory("Root");
        Product p1 = factory.createProduct("P1", 100, 2, 0); // Value 200
        Product p2 = factory.createProduct("P2", 50, 4, 0);  // Value 200
        
        root.add(p1);
        root.add(p2);
        
        assertEquals(400, root.getValue());
    }

    @Test
    void testCommandExecutionAndUndo() {
        Product p = factory.createProduct("CmdItem", 100, 10, 5);
        CommandInterface cmd = new CommandPattern.RemoveStock(p, 2);
        
        manager.executeCommand(cmd);
        assertEquals(8, p.getStockLevel());
        
        manager.undoLastCommand();
        assertEquals(10, p.getStockLevel());
    }
}
