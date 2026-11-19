package bank.dto;
import java.math.BigDecimal;
import java.time.YearMonth;



public record InterestSummary(YearMonth month, int accountsPaid, int accountsAlreadyPaid, BigDecimal totalInterest) {
 
    
    
}
