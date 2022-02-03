package AMS.services.implimentations;

import AMS.exceptions.AccountNotFoundException;
import AMS.exceptions.InsufficientFundsException;
import AMS.exceptions.NegativeMoneySubmittedException;
import AMS.models.entities.Account;
import AMS.models.entities.Transaction;
import AMS.repos.AccountRepo;
import AMS.repos.TransactionRepo;
import AMS.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    AccountRepo accountRepo;

    @Override
    public void deposit(Double amount, Long userId, Long accountId) {
        accountRepo.save(filterValidAccount(mapMoneyRemoval(accountRepo
                .findUsersAccount(accountId, userId).orElseThrow(AccountNotFoundException::new),
                0-filterValidMoney(amount), Transaction.TransactionSubType.Cash)));
    }

    @Override
    public void withdrawal(Double amount, Long userId, Long accountId) {
        accountRepo.save(filterValidAccount(mapMoneyRemoval(accountRepo
                        .findUsersAccount(accountId, userId).orElseThrow(AccountNotFoundException::new),
                filterValidMoney(amount), Transaction.TransactionSubType.Cash)));
    }

    @Override
    public void transfer(Double amount, Long userId, Long fromAccountId, Long toAccountId) {
        Account fromAccount = filterValidAccount(mapMoneyRemoval(accountRepo
                        .findUsersAccount(fromAccountId, userId).orElseThrow(AccountNotFoundException::new),
                filterValidMoney(amount), Transaction.TransactionSubType.Transfer));

        Account toAccount = filterValidAccount(mapMoneyRemoval(accountRepo
                        .findById(toAccountId).orElseThrow(AccountNotFoundException::new),
                0-filterValidMoney(amount), Transaction.TransactionSubType.Transfer));

        Stream.of(fromAccount, toAccount).forEach(accountRepo::save);
    }

    private Account mapMoneyRemoval(Account account, Double amount, Transaction.TransactionSubType type){
        Transaction transaction = new Transaction();
        transaction.setAmount(0-amount);
        transaction.setSubType(type);
        account.setBalance(account.getBalance()-amount);
        account.getTransactions().add(transaction);
        return account;
    }

    private Account filterValidAccount(Account transaction){
        if (transaction.getBalance() < 0) throw new InsufficientFundsException();
        return transaction;
    }

    private Double filterValidMoney(Double amount){
        if (amount < 0)throw new NegativeMoneySubmittedException();
        return  amount;
    }
}
