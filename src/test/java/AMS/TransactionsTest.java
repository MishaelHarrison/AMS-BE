package AMS;

import AMS.models.Dto.ManagerOut;
import AMS.models.Dto.MoneyRequest;
import AMS.models.entities.Account;
import AMS.models.entities.Transaction;
import AMS.repos.AccountRepo;
import AMS.repos.CustomerRepo;
import AMS.repos.TransactionRepo;
import AMS.repos.UserRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionsTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private UserRepo userRepo;
    @MockBean
    private CustomerRepo customerRepo;
    @MockBean
    private AccountRepo accountRepo;
    @MockBean
    private TransactionRepo transactionRepo;

    @Autowired
    private PasswordEncoder encoder;

    private AMSTestLibs libs;

    @BeforeEach
    void setUp(){
        libs = new AMSTestLibs(port, restTemplate, userRepo, encoder);
        libs.setupBaseUsers();
    }

    @Test
    void depositTest(){
        libs.loginUser();
        Account ret = setupAccount(1000D, 1L, 1L);

        MoneyRequest body = new MoneyRequest();
        body.setAmount(100D);
        ResponseEntity<Void> response = deposit(1L, body);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepo, times(1)).save(captor.capture());
        Transaction transaction = captor.getValue().getTransactions().stream().findFirst().orElseThrow(AssertionError::new);
        Assertions.assertAll(
                ()->Assertions.assertEquals(1100D, captor.getValue().getBalance()),
                ()->Assertions.assertEquals(Transaction.TransactionSubType.Cash, transaction.getSubType()),
                ()->Assertions.assertEquals(100D, transaction.getAmount())
        );
    }

    @Test
    void withdrawalTest(){
        libs.loginUser();
        Account ret = setupAccount(1000D, 1L, 1L);

        MoneyRequest body = new MoneyRequest();
        body.setAmount(100D);
        ResponseEntity<Void> response = withdrawal(1L, body);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepo, times(1)).save(captor.capture());
        Transaction transaction = captor.getValue().getTransactions().stream().findFirst().orElseThrow(AssertionError::new);
        Assertions.assertAll(
                ()->Assertions.assertEquals(900D, captor.getValue().getBalance()),
                ()->Assertions.assertEquals(Transaction.TransactionSubType.Cash, transaction.getSubType()),
                ()->Assertions.assertEquals(-100D, transaction.getAmount())
        );
    }

    @Test
    void transferTest(){
        libs.loginUser();
        Account ret = setupAccount(1000D, 1L, 1L);
        Account ret2 = new Account();
        ret2.setTransactions(new ArrayList<>());
        ret2.setBalance(1000D);
        ret2.setId(2L);
        when(accountRepo.findById(2L)).thenReturn(Optional.of(ret2));

        MoneyRequest body = new MoneyRequest();
        body.setAmount(100D);
        ResponseEntity<Void> response = transfer(1L, 2L, body);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepo, times(2)).save(captor.capture());
        Account fromAccount = captor.getAllValues().stream().filter(x->x.getId()==1L).findFirst().orElseThrow(AssertionError::new);
        Account toAccount = captor.getAllValues().stream().filter(x->x.getId()==2L).findFirst().orElseThrow(AssertionError::new);
        Transaction fromTransaction = fromAccount.getTransactions().stream().findFirst().orElseThrow(AssertionError::new);
        Transaction toTransaction = toAccount.getTransactions().stream().findFirst().orElseThrow(AssertionError::new);
        Assertions.assertAll(
                ()->Assertions.assertEquals(900D, fromAccount.getBalance()),
                ()->Assertions.assertEquals(1100D, toAccount.getBalance()),
                ()->Assertions.assertEquals(Transaction.TransactionSubType.Transfer, fromTransaction.getSubType()),
                ()->Assertions.assertEquals(-100D, fromTransaction.getAmount()),
                ()->Assertions.assertEquals(Transaction.TransactionSubType.Transfer, toTransaction.getSubType()),
                ()->Assertions.assertEquals(100D, toTransaction.getAmount())
        );
    }

    private Account setupAccount(Double balance, Long accountId, Long userId){
        Account ret = new Account();
        ret.setCreatedOn(new Date(2020, Calendar.DECEMBER, 25));
        ret.setTransactions(new ArrayList<>());
        ret.setBalance(balance);
        ret.setId(accountId);
        when(accountRepo.findUsersAccount(eq(accountId), eq(userId))).thenReturn(Optional.of(ret));
        return ret;
    }

    private ResponseEntity<Void> deposit(Long to, MoneyRequest body){
        return restTemplate.exchange(libs.endpoint("transaction/deposit/{to}"), HttpMethod.POST, libs.getHeaders(body), Void.class, to);
    }

    private ResponseEntity<Void> withdrawal(Long from, MoneyRequest body){
        return restTemplate.exchange(libs.endpoint("transaction/withdrawal/{from}"), HttpMethod.POST, libs.getHeaders(body), Void.class, from);
    }

    private ResponseEntity<Void> transfer(Long from, Long to, MoneyRequest body){
        return restTemplate.exchange(libs.endpoint("transaction/transfer/{from}/{to}"), HttpMethod.POST, libs.getHeaders(body), Void.class, from, to);
    }
}
