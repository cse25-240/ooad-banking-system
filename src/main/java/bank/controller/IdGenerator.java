package bank.controller;
import java.util.Random;
import java.util.function.Predicate;

public class IdGenerator {
    

    private final Random random;

    public IdGenerator() {
        this(new Random());
    }

    public IdGenerator(Random random) {
        this.random = random;
    }

    /** something like CNUM-483920. */
 public String nextCustomerId(Predicate<String> alreadyUsed) {
   String id;
   do {
    id = "CNUM-" + (100000 + random.nextInt(900000));
     } while (alreadyUsed.test(id));
        return id;
    }

    /** For example SAV-48392015. The prefix says which kind of account it is. */
    public String nextAccountNumber(String prefix, Predicate<String> alreadyUsed) {
        String number;
        do {
            number = prefix + "-" + (10000000 + random.nextInt(90000000));
        } while (alreadyUsed.test(number));
        return number;
    }
 

}
