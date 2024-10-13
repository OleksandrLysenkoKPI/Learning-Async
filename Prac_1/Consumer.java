// Клас споживача  
public class Consumer implements Runnable {
    private final Warehouse warehouse;

    public Consumer(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public void run() {
        while (true) {
            try {
                warehouse.removeItem();
                Thread.sleep(1500); // Покупець забирає товар кожні 1.5 секунди  
            } catch (InterruptedException e) {
                System.out.println("Помилка у покупця: " + e.getMessage()); // Обробка помилки  
            }
        }
    }
}