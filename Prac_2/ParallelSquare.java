import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

public class ParallelSquare {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Діапазон та кількість елементів
        double lowerBound = 0.5;
        double upperBound = 99.5;
        int numElements = new Random().nextInt(21) + 40;

        // Генерування масиву випадкових чисел
        List<Double> randomNumbers = GenRandNum(lowerBound, upperBound, numElements);
        System.out.println("Кількість згенерованих чисел: " + numElements);
        System.out.println("Згенеровані числа: " + randomNumbers);

        // Визначення розміру і кількості чанків
        int chunkSize = 20;
        int numChunks = (int) Math.ceil((double) randomNumbers.size() / chunkSize);
        System.out.println("Розмір чанку: " + chunkSize);
        System.out.println("Кількість чанків: " + numChunks);

        // ExecutorService для керування потоками
        ExecutorService executor = Executors.newFixedThreadPool(numChunks);
        List<Future<Set<Double>>> futures = new ArrayList<>();

        // Початок відліку часу
        long startTime = System.currentTimeMillis();

        // Розбиття масиву на частини і їхня обробка в окремому потоці
        for (int i = 0; i < randomNumbers.size(); i+= chunkSize) {
            Callable<Set<Double>> task = GetSetCallable(i, chunkSize, randomNumbers);

            // Додаємо завдання до списку futures для подальшого збору результатів
            Future<Set<Double>> future = executor.submit(task);
            futures.add(future);
        }

        // Збір результатів і перевірка isDone та isCancelled
        for (Future<Set<Double>> future : futures) {
            if (!future.isCancelled()) {
                while (!future.isDone()) {
                    System.out.println("\u001B[36mЗавдання ще не виконано у потоці " + Thread.currentThread().getName() + "\u001B[0m");
                    Thread.sleep(2);
                }
                System.out.println("\u001B[32mЗавдання було виконано у потоці " + Thread.currentThread().getName() + "\u001B[0m");
                Set<Double> squares = future.get();
                System.out.println("Квадрати чисел у підмасиві: " + squares);
                System.out.println("Кількість елементів у підмасиві: " + squares.size());
            } else {
                System.err.println("Завдання було перервано");
            }
        }

        // Кінець відліку часу
        long endTime = System.currentTimeMillis();
        PrintTime(startTime, endTime); // Вивід часу

        // Завершення роботи ExecutorService
        executor.shutdown();
    }

    // Метод для обробки підмасивів в окремих потоках
    private static Callable<Set<Double>> GetSetCallable(int i, int chunkSize, List<Double> randomNumbers) {
        int start = i;
        int end = Math.min(i + chunkSize, randomNumbers.size());

        // Callable, який обчислює квадрати чисел у підмасиві
        Callable<Set<Double>> task = () -> {
            Set<Double> resultSet = new CopyOnWriteArraySet<>();
            String threadName = Thread.currentThread().getName();
            for (int j = start; j < end; j ++) {
                resultSet.add(Math.pow(randomNumbers.get(j), 2));
                System.out.println("Потік " + threadName + " обробляє елемент " + j);
            }
            return resultSet;
        };
        return task;
    }

    // Метод для генерації випадкових чисел
    public static List<Double> GenRandNum (double lowerBound, double upperBound, int numElements) {
        List<Double> numbers = new ArrayList<Double>();
        Random rand = new Random();

        for (int i = 0; i < numElements; i++) {
            double randomValue = lowerBound + (upperBound - lowerBound) * rand.nextDouble();
            numbers.add(randomValue);
        }
        return numbers;
    }

    // Метод для форматування часу і його виводу
    public static void PrintTime (long startTime, long endTime) {
        long elapsedTime = endTime - startTime;

        Duration duration = Duration.ofMillis(elapsedTime);

        long hours = duration.toHours();
        long minutes = duration.toMinutes();
        long seconds = duration.toSeconds();
        long millis = duration.toMillis();

        String formattedTime = String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, millis);
        System.out.println("Час виконання програми: " + formattedTime);
    }
}