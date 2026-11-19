package bank.dao;
import bank.model.Transaction;
import java.util.List;
import java.util.Optional;


public interface TransactionDAO {
    void save(String accountNumber, Transaction transaction);

    List<Transaction> findByAccount(String accountNumber);

    Optional<Transaction> findById(String transactionId);

}
