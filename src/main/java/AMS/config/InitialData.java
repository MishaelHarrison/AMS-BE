package AMS.config;

import AMS.models.entities.Account;
import AMS.models.entities.Customer;
import AMS.models.entities.Role;
import AMS.models.entities.User;
import AMS.repos.AccountRepo;
import AMS.repos.CustomerRepo;
import AMS.repos.UserRepo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
public class InitialData implements InitializingBean{

    @Autowired
    UserRepo userRepo;
    @Autowired
    CustomerRepo customerRepo;
    @Autowired
    AccountRepo accountRepo;
    @Autowired
    PasswordEncoder encoder;

    @SafeVarargs
    private static <T> List<T> listOfThings(Supplier<T>... things){
        return Arrays.stream(things).map(Supplier::get).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public void afterPropertiesSet(){
        userRepo.saveAll(listOfThings(
                () -> {
                    User ret = new User();
                    ret.setPassword(encoder.encode("pass"));
                    ret.setRole(Role.manager());
                    return ret;
                }
        ));
        customerRepo.saveAll(listOfThings(
                ()->{
                    User user = new User();
                    user.setPassword(encoder.encode("pass"));
                    user.setRole(Role.customer());
                    Customer customer = new Customer();
                    customer.setDOB(new Date());
                    customer.setCitizenId("5555-5555-5555-5555");
                    customer.setAddress("20 Sesame St");
                    customer.setName("Ronald Donald");
                    customer.setEmail("E@mail.com");
                    customer.setUserData(user);
                    customer.setPAN(12345L);
                    return customer;
                }
        ));
        accountRepo.saveAll(listOfThings(
                ()->{
                    Account account = new Account();
                    Customer customer = customerRepo.findAll().stream().findFirst().orElse(null);
                    if (customer == null) return null;
                    account.setCustomerId(customer.getId());
                    account.setBalance(1000D);
                    return account;
                }
        ));
    }
}
