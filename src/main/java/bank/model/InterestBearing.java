package bank.model;

import java.math.BigDecimal;
import java.time.YearMonth;

/**only for savings and investment accounts */
public interface InterestBearing {
  BigDecimal computeInterestperMonth();
  /** Pays the current month's interest but does nothing if it was already paid. */
  void InterestPayment();
/**pays the interest for a specific month, which is added to the account balance. The 
 * next month's interest is calculated based on the new balance. 
 */
  boolean InterestPayment(YearMonth currentMonth);

/** Returns the month when interest was last calculated. */
  YearMonth getLastInterestCalculation();
}