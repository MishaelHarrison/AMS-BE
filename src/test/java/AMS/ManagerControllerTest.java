package AMS;

import AMS.models.Dto.CustomerOut;
import AMS.models.Dto.InitialAccountCreation;
import AMS.models.entities.Customer;
import AMS.models.entities.Role;
import AMS.models.entities.User;
import AMS.repos.AccountRepo;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ManagerControllerTest {

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
    void requestPANFoundTest(){
        User user = new User();
        user.setPassword(encoder.encode("pass"));
        user.setRole(libs.customer());
        user.setId(1L);
        Customer customer = new Customer();
        customer.setDOB(new Date(2020, Calendar.DECEMBER,25));
        customer.setCitizenId("5555-5555-5555-5555");
        customer.setAccounts(new ArrayList<>());
        customer.setAddress("20 Test Ln");
        customer.setEmail("E@mail.com");
        customer.setName("Testing Tom");
        customer.setUserData(user);
        customer.setPAN(12345L);
        customer.setId(1L);
        when(customerRepo.findByPAN(1L)).thenReturn(Optional.of(customer));

        libs.loginAdmin();

        CustomerOut ret = requestPAN(1L).getBody();

        Assertions.assertNotNull(ret);
        Assertions.assertAll(
                ()->Assertions.assertEquals(customer.getCitizenId(), ret.getCitizenId()),
                ()->Assertions.assertEquals(customer.getAddress(), ret.getAddress()),
                ()->Assertions.assertEquals(customer.getId(), ret.getCustomerId()),
                ()->Assertions.assertEquals(customer.getEmail(), ret.getEmail()),
                ()->Assertions.assertEquals(customer.getName(), ret.getName()),
                ()->Assertions.assertEquals(customer.getDOB(), ret.getDOB()),
                ()->Assertions.assertEquals(customer.getPAN(), ret.getPAN()),
                ()->Assertions.assertEquals(user.getId(), ret.getUserId())
        );
    }

    @Test
    void requestPANNotFoundTest(){
        when(customerRepo.findByPAN(2L)).thenReturn(Optional.empty());

        libs.loginAdmin();

        ResponseEntity<CustomerOut> ret = requestPAN(2L);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, ret.getStatusCode());
    }

    @Test
    void AccountCreationTest(){
        Customer retC = new Customer();
        User retU = new User();
        retC.setUserData(retU);
        retU.setId(1L);
        when(customerRepo.save(any(Customer.class))).thenReturn(retC);

        InitialAccountCreation input = new InitialAccountCreation();
        input.setDOB(new Date(2020, Calendar.DECEMBER, 25));
        input.setCitizenId("5555-5555-5555-5555");
        input.setName("Ronald Donald");
        input.setAddress("20 Test Ln");
        input.setEmail("E@mail.com");
        input.setPassword("pass");
        input.setBalance(100D);
        input.setPAN(1000L);

        libs.loginAdmin();

        Assertions.assertEquals(1L, sendInitialAccount(input).getBody());

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepo, times(1)).save(captor.capture());
        Assertions.assertAll(
                ()->Assertions.assertEquals(
                        input.getBalance(),
                        captor.getValue().getAccounts().stream().findFirst()
                                .orElseThrow(AssertionError::new).getBalance()
                ),
                ()->Assertions.assertTrue(encoder.matches(
                        input.getPassword(), captor.getValue().getUserData().getPassword()
                )),
                ()->Assertions.assertEquals(input.getCitizenId(), captor.getValue().getCitizenId()),
                ()->Assertions.assertEquals(input.getAddress(), captor.getValue().getAddress()),
                ()->Assertions.assertEquals(input.getEmail(), captor.getValue().getEmail()),
                ()->Assertions.assertEquals(input.getName(), captor.getValue().getName()),
                ()->Assertions.assertEquals(input.getDOB(), captor.getValue().getDOB()),
                ()->Assertions.assertEquals(input.getPAN(), captor.getValue().getPAN())
        );
    }

    private ResponseEntity<Long> sendInitialAccount(InitialAccountCreation input){
        return restTemplate.exchange(libs.endpoint("manager/newUser"), HttpMethod.POST, libs.getHeaders(input), Long.class);
    }

    private ResponseEntity<CustomerOut> requestPAN(Long id){
        return restTemplate.exchange(libs.endpoint("manager/getPAN/{id}"), HttpMethod.GET, libs.getHeaders(), CustomerOut.class, id);
    }
}
