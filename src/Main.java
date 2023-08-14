import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static AtomicInteger threeCounter = new AtomicInteger();
    public static AtomicInteger fourCounter = new AtomicInteger();
    public static AtomicInteger fiveCounter = new AtomicInteger();
    public static String[] texts;

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        texts = new String[100_000];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("abc", 3 + random.nextInt(3));
        }

        // Поскольку "условие красоты", где все символы одинаковы, очевидно удовлетваряет условию полиндрома (пример: aaaaa переворачиваем-> aaaaa),
        // а также очевидно попадает в условие лексикографического порядка букв, следовательно эти слова будут учтены и в threadPalindrome и в threadIncrease,
        // следует вычесть их колчество вместо того чтобы прибавлять, чтобы получить правильные значения красивых слов.

        Thread threadPalindrome = new Thread(Main::checkPalindrome);
        Thread threadSameSymbols = new Thread(Main::checkSameSymbols);
        Thread threadLexicographicOrder = new Thread(Main::checkLexicographicOrder);

        threadPalindrome.start();
        threadSameSymbols.start();
        threadLexicographicOrder.start();

        threadPalindrome.join();
        threadSameSymbols.join();
        threadLexicographicOrder.join();

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

    public static void checkPalindrome () {
        for (String word : texts) {
            String revers = new StringBuilder(word).reverse().toString();
            boolean flag = word.equals(revers);
            atomicIncrement(flag, word);
        }
    }

    public static void checkSameSymbols () {
        for (String word : texts) {
            boolean flag = word.chars().allMatch(a -> word.charAt(0) == a);
            atomicDecrement(flag, word);
        }
    }
    public static void checkLexicographicOrder () {
        for (String word : texts) {
            char[] list = word.toCharArray();
            boolean flag = true;
            for (int i = 0; i < list.length - 1; i++) {
                if (list[i + 1] < list[i]) {
                    flag = false;
                    break;
                }
            }
           atomicIncrement(flag, word);
        }
    }
    public static void atomicIncrement (boolean flag, String word) {
        if (flag && word.length() == 3) {
            threeCounter.incrementAndGet();
        } else if (flag && word.length() == 4) {
            fourCounter.incrementAndGet();
        } else if (flag && word.length() == 5) {
            fiveCounter.incrementAndGet();
        }
    }
    public static void atomicDecrement (boolean flag, String word) {
        if (flag && word.length() == 3) {
            threeCounter.decrementAndGet();
        } else if (flag && word.length() == 4) {
            fourCounter.incrementAndGet();
        } else if (flag && word.length() == 5) {
            fiveCounter.decrementAndGet();
        }
    }
}