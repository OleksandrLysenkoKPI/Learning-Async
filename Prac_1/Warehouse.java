import java.util.concurrent.Semaphore;

// Клас складу  
public class Warehouse {
    private int items = 0;
    private final int capacity;
    private final Semaphore supplierSemaphore;
    private final Semaphore consumerSemaphore;
    private boolean workingHours;

    public Warehouse(int capacity) {
        this.capacity = capacity;
        this.supplierSemaphore = new Semaphore(capacity);  // Спочатку склад порожній, тому всі місця доступні  
        this.consumerSemaphore = new Semaphore(0); // Спочатку немає товарів, тому дозволи для покупця відсутні  
        this.workingHours = false;
    }

    // Додавання товарів на склад  
    public synchronized void addItem() throws InterruptedException {
        while (items >= capacity) { // Якщо склад переповнений  
            System.out.println("\u001B[31mСклад переповнений! Постачальник чекає...\u001B[0m");
            wait(); // Чекаємо, поки не з'явиться вільне місце  
        }

        supplierSemaphore.acquire();  // Зменшує кількість дозволів (місць на складі)  
        items++;
        System.out.println("Постачальник додав товар. Товарів на складі: " + items);
        consumerSemaphore.release();  // Збільшує кількість дозволів для покупця (тобто додається товар).  
    }

    // Збір товарів зі складу  
    public synchronized void removeItem() throws InterruptedException {
        while (!workingHours) {  // Якщо склад закритий  
            System.out.println("\u001B[35mНеробочі години. Покупець чекає.\u001B[0m");
            wait(); // У неробочі години покупець чекає  
        }

        while (items <= 0) { // Якщо склад порожній  
            System.out.println("\u001B[31mСклад порожній! Покупець чекає...\u001B[0m");
            wait(); // Чекаємо, поки не з'явиться товар  
        }

        consumerSemaphore.acquire();  // Зменшує кількість дозволів для покупців (товар забирається)  
        items--;
        System.out.println("Покупець забрав товар. Товарів на складі: " + items);
        supplierSemaphore.release();  // Додає дозвіл для постачальника (звільняється місце на складі)  
    }

    // Метод для встановлення стану робочих годин  
    public synchronized void setWorkingHours(boolean workingHours) {
        this.workingHours = workingHours;
        if (workingHours) {
            notifyAll();  // Сигналізуємо всім потокам, що чекають, що склад відкрито  
        }
    }

    // Метод для перевірки чи зараз робочі години  
    public boolean isWorkingHours() {
        return workingHours;
    }
}