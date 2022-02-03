package AMS;

import AMS.models.entities.Role;
import AMS.models.entities.User;
import AMS.repos.UserRepo;
import AMS.security.models.LoginRequest;
import AMS.security.models.LoginResponse;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;
import java.util.Optional;

import static org.mockito.Mockito.when;

public class AMSTestLibs {

    private int port;
    private TestRestTemplate restTemplate;
    private UserRepo userRepo;
    private PasswordEncoder encoder;

    private HttpEntity<Object> headers;

    public AMSTestLibs(int port, TestRestTemplate restTemplate, UserRepo userRepo, PasswordEncoder encoder) {
        this.port = port;
        this.restTemplate = restTemplate;
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    private Role getRole(String name){
        Role role = new Role();
        role.setName(name);
        return role;
    }

    public Role customer(){return getRole("CUSTOMER");}
    public Role manager(){return getRole("MANAGER");}

    public void setupBaseUsers(){
        User user = new User();
        user.setPassword(encoder.encode("pass"));
        user.setRole(customer());
        user.setId(1L);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        User admin = new User();
        admin.setPassword(encoder.encode("pass"));
        admin.setRole(manager());
        admin.setId(2L);
        when(userRepo.findById(2L)).thenReturn(Optional.of(admin));
    }

    public HttpEntity<Object> getHeaders() {
        return headers;
    }

    public HttpEntity<Object> getHeaders(Object body) {
        return new HttpEntity<Object>(body, headers.getHeaders());
    }

    public void loginUser(){
        ResponseEntity<LoginResponse> res =  restTemplate.postForEntity(endpoint("login"), new LoginRequest("1", "pass"), LoginResponse.class, port);
        HttpHeaders header = new HttpHeaders();
        header.add("Authorization", "Bearer " + Objects.requireNonNull(res.getBody()).getJwt());
        headers = new HttpEntity<>(header);
    }

    public void loginAdmin(){
        ResponseEntity<LoginResponse> res =  restTemplate.postForEntity(endpoint("login"), new LoginRequest("2", "pass"), LoginResponse.class, port);
        HttpHeaders header = new HttpHeaders();
        header.add("Authorization", "Bearer " + Objects.requireNonNull(res.getBody()).getJwt());
        headers = new HttpEntity<>(header);
    }

    public String endpoint(String ext){
        return "http://localhost:" + port + "/" + ext;
    }
}
