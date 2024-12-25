package Prac_5.Task_1;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        // Словник слів
        Set<String> slangWords = new HashSet<>(Arrays.asList("cool", "bruh", "bruh—science", "yolo", "lit"));
        Set<String> academicWords = new HashSet<>(Arrays.asList("theorem", "theorems", "hypothesis", "algorithm", "algorithms", "analysis"));

        // Читання тексту з файлу
        List<String> words = readWordsFromFile("Prac_5/Task_1/text.txt");

        // Завдання 1: Знайти сленгові слова
        CompletableFuture<List<String>> slangTask = CompletableFuture.supplyAsync(() -> {
            System.out.println("Сленгові слова обробляє потік: " + Thread.currentThread().getName());
            List<String> slangList = new ArrayList<>();
            for (String word : words) {
                if (slangWords.contains(word.toLowerCase())) {
                    slangList.add(word);
                }
            }
            System.out.println("Сленгові слова знайдено: " + slangList);
            return slangList;
        });

        // Завдання 2: Знайти академічні слова
        CompletableFuture<List<String>> academicTask = CompletableFuture.supplyAsync(() -> {
            System.out.println("Академічні слова обробляє потік: " + Thread.currentThread().getName());
            List<String> academicList = new ArrayList<>();
            for (String word : words) {
                if (academicWords.contains(word.toLowerCase())) {
                    academicList.add(word);
                }
            }
            System.out.println("Академічні слова знайдено: " + academicList);
            return academicList;
        });

        // Використання anyOf() для виконання обох завдань паралельно, але з раннім завершенням
        CompletableFuture<Object> anyTask = CompletableFuture.anyOf(slangTask, academicTask);

        // Додаткові перевірки на завершення роботи кожного із завдань для демонстрації роботи методу anyOf()
        anyTask.thenRun(() -> {
            System.out.println("Перше завершене завдання:");
            if (slangTask.isDone()) {
                System.out.println("Сленгові слова");
            } else if (academicTask.isDone()) {
                System.out.println("Академічні слова");
            }
        });

        // Комбінування результатів після завершення хоча б одного завдання
        CompletableFuture<List<String>> combinedTask = anyTask.thenCompose(ignored ->
                slangTask.thenCombine(academicTask, (slangList, academicList) -> {
                    System.out.println("Комбінує результати потік: " + Thread.currentThread().getName());
                    List<String> combinedList = new ArrayList<>();
                    Iterator<String> slangIterator = slangList.iterator();
                    Iterator<String> academicIterator = academicList.iterator();

                    while (slangIterator.hasNext() || academicIterator.hasNext()) {
                        if (slangIterator.hasNext()) {
                            combinedList.add(slangIterator.next());
                        }
                        if (academicIterator.hasNext()) {
                            combinedList.add(academicIterator.next());
                        }
                    }
                    return combinedList;
                })
        );

        // Виведення результату
        combinedTask.thenAccept(combinedList -> {
            System.out.println("\nОб'єднаний список слів:");
            combinedList.forEach(System.out::println);
        });

        // Чекає завершення всіх завдань
        try {
            combinedTask.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // Метод для читання слів з файлу
    private static List<String> readWordsFromFile(String fileName) {
        List<String> words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                words.addAll(Arrays.asList(line.split("\\s+")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }
}
