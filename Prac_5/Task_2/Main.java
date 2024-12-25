package Prac_5.Task_2;

import java.util.concurrent.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Виклик методу для демонстрації вибору найкращого ПЗ
        selectBestSoftware();
    }

    public static void selectBestSoftware() {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Паралельне отримання даних про ціну, функціональність і підтримку для кожного ПЗ
        CompletableFuture<Map<String, Integer>> priceFuture = CompletableFuture.supplyAsync(() -> getPrices(), executor);
        CompletableFuture<Map<String, Integer>> functionalityFuture = CompletableFuture.supplyAsync(() -> getFunctionalities(), executor);
        CompletableFuture<Map<String, Integer>> supportFuture = CompletableFuture.supplyAsync(() -> getSupportScores(), executor);

        // Використання allOf() для отримання першого завершеного CompletableFuture
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(priceFuture, functionalityFuture, supportFuture);

        combinedFuture.thenRun(() -> {
            try {
                // Після того, як перший CompletableFuture завершується, чекаємо на всі результати
                Map<String, Integer> prices = priceFuture.get();
                Map<String, Integer> functionalities = functionalityFuture.get();
                Map<String, Integer> supportScores = supportFuture.get();

                // Підрахунок та вибір найкращого ПЗ
                String bestSoftware = selectBestOption(prices, functionalities, supportScores);
                System.out.println("Найкращий вибір: " + bestSoftware);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).join();

        executor.shutdown();
    }

    private static Map<String, Integer> getPrices() {
        simulateDelay();
        System.out.println("Потік " + Thread.currentThread().getName() + " обробляє отримання цін.");
        Map<String, Integer> prices = new HashMap<>();
        prices.put("Software A", 500);
        prices.put("Software B", 300);
        prices.put("Software C", 700);
        prices.put("Software D", 200);
        prices.put("Software E", 800);
        System.out.println("Ціни отримано: " + prices);
        return prices;
    }

    private static Map<String, Integer> getFunctionalities() {
        simulateDelay();
        System.out.println("Потік " + Thread.currentThread().getName() + " обробляє отримання функціональності.");
        Map<String, Integer> functionalities = new HashMap<>();
        functionalities.put("Software A", 8);
        functionalities.put("Software B", 7);
        functionalities.put("Software C", 9);
        functionalities.put("Software D", 5);
        functionalities.put("Software E", 2);
        System.out.println("Функціональність отримано: " + functionalities);
        return functionalities;
    }

    private static Map<String, Integer> getSupportScores() {
        simulateDelay();
        System.out.println("Потік " + Thread.currentThread().getName() + " обробляє отримання оцінок підтримки.");
        Map<String, Integer> supportScores = new HashMap<>();
        supportScores.put("Software A", 9);
        supportScores.put("Software B", 8);
        supportScores.put("Software C", 6);
        supportScores.put("Software D", 3);
        supportScores.put("Software E", 5);
        System.out.println("Оцінки підтримки отримано: " + supportScores);
        return supportScores;
    }

    private static String selectBestOption(Map<String, Integer> prices, Map<String, Integer> functionalities, Map<String, Integer> supportScores) {
        String bestOption = null;
        int bestScore = Integer.MIN_VALUE;

        for (String software : prices.keySet()) {
            int score = calculateScore(prices.get(software), functionalities.get(software), supportScores.get(software));
            System.out.println("Підрахунок для " + software + ": " + score);
            if (score > bestScore) {
                bestScore = score;
                bestOption = software;
            }
        }

        return bestOption;
    }

    private static int calculateScore(int price, int functionality, int support) {
        // Оцінка: висока функціональність і підтримка, низька ціна
        return functionality * 3 + support * 2 - price / 100;
    }

    private static void simulateDelay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
