package Prac_4.Task_1;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.nio.file.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        // Пул потоків для асинхронних операцій
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // Список шляхів до файлів
        List<Path> directories = List.of(
                Paths.get("Prac_4/Task_1/text1.txt"),
                Paths.get("Prac_4/Task_1/text2.txt"),
                Paths.get("Prac_4/Task_1/text3.txt")
        );

        // Запуск таймера
        long startTime = System.currentTimeMillis();

        CompletableFuture<List<String>> processedContents = CompletableFuture.supplyAsync(() ->
                // Зчитування кожного файлу через readFile
                directories.stream()
                        .map(Main::readFile)
                        .collect(Collectors.toList()),
                executor // виконання на пулі потоків
        ).thenApplyAsync(contents ->
                // обробка кожного рядка -- видалення усіх літер верхнього та нижнього регістрів
                contents.stream()
                        .map(content -> content.replaceAll("[a-zA-Z]", ""))
                        .collect(Collectors.toList()),
                executor // виконання на пулі потоків
        );

        // Асинхронний вивід обробленого вмісту
        processedContents.thenAcceptAsync(contents -> {
            // Вивід оброблених рядків на екран
            contents.forEach(content -> System.out.println("Оброблений зміст: " + content));
            // Вивід часу
            System.out.println("Обробка завершена за: " + (System.currentTimeMillis() - startTime) + " мс");
        }, executor);

        // Повідомлення, про завершення всіх задач
        processedContents.thenRunAsync(() -> System.out.println("Всі завдання завершені.\n"), executor);

        /* Очікування завершення всіх задач
         (інакше головний потік завершить роботу швидше за всі інші потоки) */
        processedContents.join();
        // Завершення роботи пулу потоків
        executor.shutdown();
    }


    // Метод для зчитування тексту з файлу
    private static String readFile(Path file) {
        long startTime = System.currentTimeMillis();
        try {
            String content = Files.readString(file);
            System.out.println("Зчитування з файлу " + file + " за " + (System.currentTimeMillis() - startTime) + " мс");
            return content;
        } catch (IOException e) {
            System.err.println("Помилка під час зчитування з файлу " + file + ": " + e.getMessage());
            return "";
        }
    }

}

