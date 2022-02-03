package AMS;

import AMS.models.Dto.*;
import AMS.models.entities.Account;
import AMS.models.entities.Customer;
import AMS.models.entities.User;
import AMS.repos.CustomerRepo;
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

import java.util.*;

import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerDataTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private UserRepo userRepo;
    @MockBean
    private CustomerRepo customerRepo;

    @Autowired
    private PasswordEncoder encoder;

    private AMSTestLibs libs;

    @BeforeEach
    void setUp(){
        libs = new AMSTestLibs(port, restTemplate, userRepo, encoder);
        libs.setupBaseUsers();
    }

    @Test
    void updateCustomerTest(){
        libs.loginUser();
        when(customerRepo.findByUser(1L)).thenReturn(Optional.of(new Customer()));
        when(customerRepo.save(any())).thenAnswer(x->{
            Customer ret = x.getArgument(0, Customer.class);
            ret.setUserData(new User());
            return ret;
        });

        CustomerUpdate update = new CustomerUpdate();
        update.setAddress("42 Testing Ln");
        update.setName("Ronald Donald");
        update.setEmail("E@mail.com");
        ResponseEntity<CustomerOut> ret = putCustomerData(update);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepo, times(1)).save(captor.capture());
        Assertions.assertAll(
                ()->Assertions.assertEquals(update.getAddress(), captor.getValue().getAddress()),
                ()->Assertions.assertEquals(update.getEmail(), captor.getValue().getEmail()),
                ()->Assertions.assertEquals(update.getName(), captor.getValue().getName())
        );
        Assertions.assertNotNull(ret.getBody());
        Assertions.assertAll(
                ()->Assertions.assertEquals(update.getAddress(), ret.getBody().getAddress()),
                ()->Assertions.assertEquals(update.getEmail(), ret.getBody().getEmail()),
                ()->Assertions.assertEquals(update.getName(), ret.getBody().getName())
        );
    }

    @Test
    void updatePasswordTest(){
        libs.loginUser();

        PasswordUpdate update = new PasswordUpdate();
        update.setPassword("newPass");
        ResponseEntity<Void> ret = putPassword(update);

        Assertions.assertTrue(ret.getStatusCode().is2xxSuccessful());
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepo, times(1)).save(captor.capture());
        Assertions.assertTrue(encoder.matches(update.getPassword(), captor.getValue().getPassword()));
    }

    @Test
    void addAccountTest(){
        libs.loginUser();
        Customer customer = new Customer();
        customer.setAccounts(new ArrayList<>());
        when(customerRepo.findByUser(1L)).thenReturn(Optional.of(customer));
        when(customerRepo.save(any())).thenAnswer(x->{
            Customer savedCustomer = new Customer();
            Account account = new Account();
            account.setCreatedOn(new Date(2020, Calendar.DECEMBER, 25));
            savedCustomer.setAccounts(Collections.singletonList(account));
            account.setId(1L);
            account.setBalance(x.getArgument(0, Customer.class).getAccounts().stream()
                    .findFirst().orElseThrow(AssertionError::new).getBalance());
            return savedCustomer;
        });

        MoneyRequest data = new MoneyRequest();
        data.setAmount(100D);
        ResponseEntity<AccountOut> ret = postAccount(data);

        Assertions.assertNotNull(ret.getBody());
        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepo, times(1)).save(captor.capture());
        Assertions.assertEquals(data.getAmount(),
                captor.getValue().getAccounts().stream().findFirst()
                        .orElseThrow(AssertionError::new).getBalance());
        Assertions.assertEquals(data.getAmount(), ret.getBody().getBalance());
    }

    private ResponseEntity<AccountOut> postAccount(MoneyRequest data){
        return restTemplate.exchange(libs.endpoint("data/newAccount"), HttpMethod.POST, libs.getHeaders(data), AccountOut.class);
    }

    private ResponseEntity<CustomerOut> putCustomerData(CustomerUpdate data){
        return restTemplate.exchange(libs.endpoint("data/update"), HttpMethod.PUT, libs.getHeaders(data), CustomerOut.class);
    }

    private ResponseEntity<Void> putPassword(PasswordUpdate data){
        return restTemplate.exchange(libs.endpoint("data/update/password"), HttpMethod.PUT, libs.getHeaders(data), Void.class);
    }
}
