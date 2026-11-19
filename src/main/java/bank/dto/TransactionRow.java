package bank.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionRow(String transactionId, LocalDateTime timestamp, String type,
    BigDecimal amount, BigDecimal balanceAfter, String description) {

        
    
}
