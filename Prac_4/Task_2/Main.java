package Prac_4.Task_2;

import java.util.concurrent.*;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // ExecutorService для управління потоками
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // Генерація послідовності дійсних чисел
        double[] sequence = generateRandomSequence(20);
        System.out.println("Початкова послідовність: ");
        for (double num : sequence) {
            System.out.printf("%.3f ", num);
        }
        System.out.println();

        // Асинхронне виконання обчислення
        CompletableFuture<Double> result = CompletableFuture.supplyAsync(() -> {
            double sum = 0;
            for (int i = 0; i < sequence.length - 1; i++) {
                double product = sequence[i] * sequence[i + 1];
                sum += product;
                System.out.printf("Крок %d: %.3f * %.3f = %.3f\n", i + 1, sequence[i], sequence[i + 1], product);  // Виведення на кожному кроці
            }
            return sum;
        }, executor);

        // Обробка результату після виконання обчислення
        CompletableFuture<Void> allOf = result.thenApplyAsync(res -> {
            System.out.printf("Сума всіх добутків: %.3f\n", res);
            return res;
        }, executor)
                .thenAcceptAsync(res -> {
            System.out.printf("Завершення обчислення з результатом: %.3f\n", res);
        }, executor)
                .thenRunAsync(() -> {
            long endTime = System.currentTimeMillis();
            System.out.println("Час виконання операцій: " + (endTime - startTime) + " мс");
        }, executor)
                .exceptionally(ex -> {
            System.out.println("Помилка: " + ex.getMessage());
            return null;
        }).handle((res, ex) -> {
            if (ex != null) {
                System.err.println("Обробка помилки: " + ex.getMessage());
            } else {
                System.out.println("Обчислення завершено успішно.");
            }
            return null;
        });

        // Очікування на завершення всіх завдань
        allOf.join();

        // Закриття ExecutorService після завершення всіх задач
        executor.shutdown();
    }

    // Метод для генерації випадкової послідовності чисел
    public static double[] generateRandomSequence(int length) {
        double[] sequence = new double[length];
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sequence[i] = random.nextDouble() * 10;
        }
        return sequence;
    }
}
