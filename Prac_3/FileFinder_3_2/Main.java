package Prac_3.FileFinder_3_2;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import javax.swing.JFileChooser;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String filepath = getFileName();

        System.out.println("Введіть мінімальний розмір файлів (у байтах)");
        long minSize = scanner.nextLong();

        File rootDir = new File(filepath);

        ForkJoinPool pool = new ForkJoinPool();
        FileSizeFinder task = new FileSizeFinder(rootDir, minSize);

        System.out.println("Пошук розпочато");
        long fileCount = pool.invoke(task);

        System.out.println("Знайдено файлів: " + fileCount);
        System.out.println("Кількість створених підзадач: " + FileSizeFinder.getTaskCount());
        System.out.println("Кількість потоків у пулі: " + pool.getPoolSize());
    }

    public static String getFileName () {
        String filepath = "";
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            filepath = fileChooser.getSelectedFile().getAbsolutePath();
        }
        return filepath;
    }
}
