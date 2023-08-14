import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static AtomicInteger threeCounter = new AtomicInteger();
    public static AtomicInteger fourCounter = new AtomicInteger();
    public static AtomicInteger fiveCounter = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        String[] texts = new String[100_000];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("abc", 3 + random.nextInt(3));
        }

        Thread threadPalindrome = new Thread(() -> {
            for (String s : texts) {
                String revers = new StringBuilder(s).reverse().toString();
                boolean flag = s.equals(revers);
                if (flag && s.length() == 3) {
                    threeCounter.incrementAndGet();
                } else if (flag && s.length() == 4) {
                    fourCounter.incrementAndGet();
                } else if (flag && s.length() == 5) {
                    fiveCounter.incrementAndGet();
                }
            }
        });

        // Поскольку "условие красоты", где все символы одинаковы, очевидно удовлетваряет условию полиндрома (пример: aaaaa переворачиваем-> aaaaa),
        // а также очевидно попадает в условие лексикографического порядка букв, следовательно эти слова будут учтены и в threadPalindrome и в threadIncrease,
        // следует вычесть их колчество вместо того чтобы прибавлять, чтобы получить правильные значения красивых слов.

        Thread threadSameSymbols = new Thread(() -> {
            for (String s : texts) {
                boolean flag = s.chars().allMatch(a -> s.charAt(0) == a);
                if (flag && s.length() == 3) {
                    threeCounter.decrementAndGet();
                } else if (flag && s.length() == 4) {
                    fourCounter.incrementAndGet();
                } else if (flag && s.length() == 5) {
                    fiveCounter.decrementAndGet();
                }
            }
        });

        Thread threadIncrease = new Thread(() -> {
            for (String s : texts) {
                char[] list = s.toCharArray();
                boolean flag = true;
                for (int i = 0; i < list.length - 1; i++) {
                    if (list[i + 1] < list[i]) {
                        flag = false;
                        break;
                    }
                }
                if (flag && s.length() == 3) {
                    threeCounter.incrementAndGet();
                } else if (flag && s.length() == 4) {
                    fourCounter.incrementAndGet();
                } else if (flag && s.length() == 5) {
                    fiveCounter.incrementAndGet();
                }
            }
        });

        threadPalindrome.start();
        threadSameSymbols.start();
        threadIncrease.start();

        threadPalindrome.join();
        threadSameSymbols.join();
        threadIncrease.join();

        System.out.println("Красивых слов с длиной 3: " + threeCounter);
        System.out.println("Красивых слов с длиной 4: " + fourCounter);
        System.out.println("Красивых слов с длиной 5: " + fiveCounter);
    }


    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}