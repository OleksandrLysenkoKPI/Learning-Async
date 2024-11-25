import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CompletableFutureExample {

    public static void main(String[] args) {
        Path filePath = Path.of("numbers.txt");

        // Таймер для всього процесу
        long startTime = System.nanoTime();

        // runAsync() – повідомлення про старт задач
        CompletableFuture<Void> startTask = CompletableFuture.runAsync(() -> {
            long taskStart = System.nanoTime();
            System.out.println("Запуск асинхронних задач...");
            System.out.printf("\u001B[36mrunAsync() завершено за %,d мс%n\u001B[0m", (System.nanoTime() - taskStart) / 1_000_000);
        });

        // supplyAsync() – створення файлу з випадковими числами
        CompletableFuture<List<Integer>> generateFileTask = CompletableFuture.supplyAsync(() -> {
            long taskStart = System.nanoTime();
            List<Integer> numbers = new Random().ints(40, 1, 101).boxed().collect(Collectors.toList());
            try {
                Files.write(filePath, numbers.stream().map(String::valueOf).collect(Collectors.toList()),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                System.out.println("Файл з випадковими числами створено.");
            } catch (IOException e) {
                throw new RuntimeException("Помилка запису у файл", e);
            }
            System.out.printf("\u001B[36msupplyAsync() завершено за %,d мс%n\u001B[0m", (System.nanoTime() - taskStart) / 1_000_000);
            return numbers;
        });

        // thenApplyAsync() – зчитування даних і фільтрація парних і непарних чисел
        CompletableFuture<List<List<Integer>>> filterNumbersTask = generateFileTask.thenApplyAsync(numbers -> {
            long taskStart = System.nanoTime();
            try {
                List<Integer> allNumbers = Files.lines(filePath)
                        .map(Integer::parseInt)
                        .toList();
                System.out.println("Нефільтровані числа: " + allNumbers);

                // Фільтрація парних і непарних чисел
                List<Integer> evenNumbers = allNumbers.stream()
                        .filter(n -> n % 2 == 0)
                        .toList();
                List<Integer> oddNumbers = allNumbers.stream()
                        .filter(n -> n % 2 != 0)
                        .toList();

                System.out.printf("\u001B[36mthenApplyAsync() завершено за %,d мс%n\u001B[0m", (System.nanoTime() - taskStart) / 1_000_000);
                return List.of(evenNumbers, oddNumbers); // Повертаємо списки у вигляді списку
            } catch (IOException e) {
                throw new RuntimeException("Помилка читання файлу", e);
            }
        });

        // thenAcceptAsync() – виведення парних і непарних чисел
        CompletableFuture<Void> printFilteredNumbersTask = filterNumbersTask.thenAcceptAsync(filteredNumbers -> {
            long taskStart = System.nanoTime();
            List<Integer> evenNumbers = filteredNumbers.get(0);
            List<Integer> oddNumbers = filteredNumbers.get(1);

            System.out.println("Парні числа: " + evenNumbers);
            System.out.println("Непарні числа: " + oddNumbers);

            System.out.printf("\u001B[36mthenAcceptAsync() завершено за %,d мс%n\u001B[0m", (System.nanoTime() - taskStart) / 1_000_000);
        });

        // thenRunAsync() – повідомлення про завершення всіх задач
        CompletableFuture<Void> finishTask = printFilteredNumbersTask.thenRunAsync(() -> {
            long taskStart = System.nanoTime();
            System.out.printf("\u001B[36mthenRunAsync() завершено за %,d мс%n\u001B[0m", (System.nanoTime() - taskStart) / 1_000_000);
            System.out.println("\u001B[32mУсі задачі завершено!\u001B[0m");
        });

        // Очікування завершення всіх задач
        CompletableFuture.allOf(startTask, finishTask).join();

        long totalTime = System.nanoTime() - startTime;
        System.out.printf("\u001B[36mУвесь процес завершено за %,d мс%n\u001B[0m", totalTime / 1_000_000);
    }
}
