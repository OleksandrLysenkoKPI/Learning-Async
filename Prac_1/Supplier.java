// Клас постачальника  
public class Supplier implements Runnable {
    private final Warehouse warehouse;

    public Supplier(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(2000); // Постачальник додає товар кожні 2 секунди  
                warehouse.addItem();
            } catch (InterruptedException  e) {
                System.out.println("Помилка у постачальника: " + e.getMessage()); // Обробка помилки  
            }
        }
    }
}