package Prac_3.MatrixSum_3_1;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

public class WorkStealingMatrixSum {
    static class ColumnSumTask extends RecursiveTask<int[]> {
        private final int[][] matrix;
        private final int startCol;
        private final int endCol;

        public ColumnSumTask(int[][] matrix, int startCol, int endCol) {
            this.matrix = matrix;
            this.startCol = startCol;
            this.endCol = endCol;
        }

        @Override
        protected int[] compute() {
            if (endCol - startCol <= 1) { // Базовий випадок
                int[] columnSums = new int[endCol - startCol];
                for (int col = startCol; col < endCol; col++) {
                    int sum = 0;
                    for (int row = 0; row < matrix.length; row++) {
                        sum += matrix[row][col];
                    }
                    columnSums[col - startCol] = sum;
                }
                return columnSums;
            } else { // Рекурсивний поділ задачі
                int mid = (startCol + endCol) / 2;
                ColumnSumTask leftTask = new ColumnSumTask(matrix, startCol, mid);
                ColumnSumTask rightTask = new ColumnSumTask(matrix, mid, endCol);

                leftTask.fork(); // Ліва підзадача додається до черги потоку
                int[] rightResult = rightTask.compute(); // Права підзадача виконується в поточному потоці
                int[] leftResult = leftTask.join(); // Очікуємо завершення лівої підзадачі

                return mergeResults(leftResult, rightResult);
            }
        }

        private int[] mergeResults(int[] left, int[] right) {
            int[] result = new int[left.length + right.length];
            System.arraycopy(left, 0, result, 0, left.length);
            System.arraycopy(right, 0, result, left.length, right.length);
            return result;
        }
    }

    public static void main(String[] args) {
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

        ForkJoinPool pool = new ForkJoinPool();

        long startTime = System.currentTimeMillis(); // Створює пул потоків

        ColumnSumTask task = new ColumnSumTask(matrix, 0, matrix[0].length);
        int[] columnSums = pool.invoke(task); // Автоматично використовує Work Stealing

        long endTime = System.currentTimeMillis();

        System.out.println("\u001B[32mСуми стовпців:\u001B[0m");
        for (int i = 0; i < columnSums.length; i++) {
            System.out.println("Стовпчик № " + (i +1) + ": " + columnSums[i]);
        }
        System.out.println("Час роботи (Work Stealing): " + (endTime - startTime) + "ms");
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
