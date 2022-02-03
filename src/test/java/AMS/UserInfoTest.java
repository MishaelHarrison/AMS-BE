package AMS;

import AMS.models.Dto.CustomerOut;
import AMS.models.Dto.ManagerOut;
import AMS.models.Dto.RoleResponse;
import AMS.models.entities.Customer;
import AMS.models.entities.Role;
import AMS.models.entities.User;
import AMS.repos.CustomerRepo;
import AMS.repos.UserRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
class UserInfoTest {

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
	void getCustomerInfoTest(){
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
		when(customerRepo.findByUser(1L)).thenReturn(Optional.of(customer));

		libs.loginUser();

		CustomerOut ret = requestInfoUser().getBody();

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
	void getManagerInfoTest(){
		libs.loginAdmin();

		ManagerOut ret = requestInfoAdmin().getBody();

		Assertions.assertNotNull(ret);
		Assertions.assertEquals(2L, ret.getId());
	}

	@Test
	void requestRoleUserTest() {
		SuccessfulRoleRequestTest(false);
	}

	@Test
	void requestRoleAdminTest() {
		SuccessfulRoleRequestTest(true);
	}

	private void SuccessfulRoleRequestTest(boolean isAdmin){
		if (isAdmin) libs.loginAdmin();
		else libs.loginUser();

		RoleResponse role = requestRole().getBody();

		Assertions.assertNotNull(role);
		Assertions.assertEquals(isAdmin?"MANAGER":"CUSTOMER", role.getRole());
	}

	private ResponseEntity<RoleResponse> requestRole(){
		return restTemplate.exchange(libs.endpoint("info/role"), HttpMethod.GET, libs.getHeaders(), RoleResponse.class);
	}

	private ResponseEntity<CustomerOut> requestInfoUser(){
		return restTemplate.exchange(libs.endpoint("info/self"), HttpMethod.GET, libs.getHeaders(), CustomerOut.class);
	}

	private ResponseEntity<ManagerOut> requestInfoAdmin(){
		return restTemplate.exchange(libs.endpoint("info/self"), HttpMethod.GET, libs.getHeaders(), ManagerOut.class);
	}

}
