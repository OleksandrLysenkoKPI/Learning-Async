package Prac_3.MatrixSum_3_1;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.List;

public class WorkDealingMatrixSum {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        int rows, cols;

        while (true) {
            System.out.println("\u001B[36mВведіть кількість рядків у матриці:\u001B[0m");
            try {
                rows = scanner.nextInt();
                if (rows > 0)
                    break;
                else
                    System.out.println("Введіть додатнє число для рядків.");
            } catch (InputMismatchException e) {
                System.out.println("\u001B[31mНеправильний ввід\u001B[0m");
                scanner.next();
            }
        }

        while (true) {
            System.out.println("\u001B[36mВведіть кількість стовпців у матриці:\u001B[0m");
            try {
                cols = scanner.nextInt();
                if (cols > 0)
                    break;
                else
                    System.out.println("Введіть додатнє число для стовпців.");
            } catch (InputMismatchException e) {
                System.out.println("\u001B[31mНеправильний ввід\u001B[0m");
                scanner.next();
            }
        }
        int[][] matrix = generateMatrix(rows, cols);

        System.out.println("\n\u001B[33mЗгенерована матриця:\u001B[0m");
        printMatrix(matrix);

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Integer>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        // Для кожного стовпця створюється окреме завдання для підрахунку суми
        final int rowsFinal = rows;
        for (int col = 0; col < cols; col++) {
            final int column = col;
            futures.add(executor.submit(() -> { // Додаємо завдання у список
                int sum = 0;
                for (int row = 0; row < rowsFinal; row++) { // Обчислюємо суму стовпця
                    sum += matrix[row][column];
                }
                return sum;
            }));
        }

        int[] columnSums = new int[cols];
        for (int i = 0; i < futures.size(); i++) {
            columnSums[i] = futures.get(i).get(); // Чекає завершення кожного завдання і отримує результат
        }
        long endTime = System.currentTimeMillis();

        executor.shutdown();

        System.out.println("\u001B[32mСуми стовпців:\u001B[0m");
        for (int i = 0; i < columnSums.length; i++) {
            System.out.println("Стовпчик № " + (i +1) + ": " + columnSums[i]);
        }
        System.out.println("Час роботи (Work Dealing): " + (endTime - startTime) + "ms");
    }

    private static int[][] generateMatrix(int rows, int cols) {
        int[][] matrix = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = (int) (Math.random() * 10); // Генерує числа 0-9
            }
        }
        return matrix;
    }

    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }
}
