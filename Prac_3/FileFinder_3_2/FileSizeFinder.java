package Prac_3.FileFinder_3_2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class FileSizeFinder extends RecursiveTask<Long> {
    private final File directory;
    private final long minSize;
    private static long taskCount;

    public FileSizeFinder(File directory, long minSize) {
        this.directory = directory;
        this.minSize = minSize;
        incrementTaskCount();
    }

    @Override
    protected Long compute() {
        long fileCount = 0;
        List<FileSizeFinder> subTasks = new ArrayList<>();

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Створення підзадачі для підкаталогів
                    FileSizeFinder subTask = new FileSizeFinder(file, minSize);
                    subTask.fork(); // Асинхронне виконання
                    subTasks.add(subTask);
                } else if (file.isFile() && file.length() > minSize) {
                    fileCount++;
                }
            }
        }

        // Підрахунок результатів підзадач
        for (FileSizeFinder subTask : subTasks) {
            fileCount += subTask.join();
        }

        return fileCount;
    }

    // Синхронізований метод для збільшення лічильника задач
    private static synchronized void incrementTaskCount() {
        taskCount++;
    }

    // Метод для отримання кількості створених задач
    public static synchronized long getTaskCount() {
        return taskCount;
    }
}
