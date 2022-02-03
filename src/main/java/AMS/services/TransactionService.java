package AMS.services;

public interface TransactionService {
    void deposit(Double amount, Long userId, Long accountId);

    void withdrawal(Double amount, Long userId, Long accountId);

    void transfer(Double amount, Long userId, Long fromAccountId, Long toAccountId);
}
