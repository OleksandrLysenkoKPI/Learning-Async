// Головний клас  
public class Main {
    public static void main(String[] args) {
        Warehouse warehouse = new Warehouse(5); // Склад з місткістю 5 товарів  

        Thread supplierThread = new Thread(new Supplier(warehouse));
        Thread consumerThread = new Thread(new Consumer(warehouse));

        supplierThread.start();
        consumerThread.start();

        // Симуляція зміни робочих годин  
        while (true) {
            try {
                warehouse.setWorkingHours(false);
                System.err.println("=============Склад закрився=============");
                Thread.sleep(7000); // Склад закритий на 7 секунд  

                warehouse.setWorkingHours(true);
                System.err.println("=============Склад відкрився=============");
                Thread.sleep(10000); // Склад відкритий на 10 секунд  
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}