import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    private static final Map<Integer, BigInteger> factorialMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        List<Future<ReturnPair>> futureList = Collections.synchronizedList(new ArrayList<>());


        futureList.add(executorService.submit(createFactorialTask(5)));
        futureList.add(executorService.submit(createFactorialTask(10)));
        futureList.add(executorService.submit(createFactorialTask(15)));

        for (Future<ReturnPair> future : futureList) {
            if (future.isCancelled()) {
                System.out.println("Task was cancelled");
            } else {
                ReturnPair res = future.get();
                System.out.println("Factorial of " + res.number + " is " + res.factorialResult);
            }
        }
        System.out.println("\n\nМеморизована таблиця факторіалів: ");
        factorialMap.forEach((key, value) -> System.out.println("Factorial of " + key + " is " + value));

        executorService.shutdown();
    }
    static class ReturnPair{
        int number;
        BigInteger factorialResult;

        public ReturnPair(int number, BigInteger factorialResult) {
            this.number = number;
            this.factorialResult = factorialResult;
        }
    }
    public static Callable<ReturnPair> createFactorialTask(int number) {
        return () -> {
            BigInteger factorial = getOrComputeFactorial(number);
            factorialMap.putIfAbsent(number, factorial);
            System.out.println("Thread finished: " + Thread.currentThread().getName());
            return new ReturnPair(number, factorial);
        };
    }

    public static BigInteger getOrComputeFactorial(int number) {
        Optional<Map.Entry<Integer, BigInteger>> nearestEntry = factorialMap.entrySet().stream()
                .filter(entry -> entry.getKey() <= number)
                .max(Map.Entry.comparingByKey());
        BigInteger result;
        int start;

        if (nearestEntry.isPresent()) {
            start = nearestEntry.get().getKey();
            result = nearestEntry.get().getValue();
        } else {
            start = 1;
            result = BigInteger.ONE;
        }

        for (int i = start + 1; i <= number; i++) {
            result = result.multiply(BigInteger.valueOf(i));
            factorialMap.putIfAbsent(i, result);
        }
        return result;
    }

}
