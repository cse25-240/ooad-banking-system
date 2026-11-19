package bank.controller;
import java.math.BigDecimal;
import java.util.regex.Pattern;

import bank.exception.InvalidAmountException;
public class Amounts {
   private static final Pattern PLAIN = Pattern.compile("\\d{1,12}(\\.\\d{1,2})?");

    private Amounts() {
    }

    public static BigDecimal parse(String text) throws InvalidAmountException {
        String trimmed = text == null ? "" : text.trim();
        if (!PLAIN.matcher(trimmed).matches()) {
            throw new InvalidAmountException("Enter an amount such as 250 or 250.50 (digits only, at most two decimals).");
        }
        return new BigDecimal(trimmed);
    }
 

}
