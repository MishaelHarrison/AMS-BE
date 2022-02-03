package AMS.config;

import AMS.models.entities.Account;
import AMS.models.entities.Customer;
import AMS.models.entities.Role;
import AMS.models.entities.User;
import AMS.repos.AccountRepo;
import AMS.repos.CustomerRepo;
import AMS.repos.RoleRepo;
import AMS.repos.UserRepo;
import AMS.services.RoleService;
import org.hibernate.Session;
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

    @Autowired
    RoleService roleService;

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
                    ret.setRole(roleService.manager());
                    return ret;
                }
        ));
        customerRepo.saveAll(listOfThings(
                ()->{
                    User user = new User();
                    Account account = new Account();
                    Customer customer = new Customer();
                    user.setPassword(encoder.encode("pass"));
                    customer.setCitizenId("5555-5555-5555-5555");
                    customer.setAddress("20 Sesame St");
                    customer.setName("Ronald Donald");
                    customer.setEmail("E@mail.com");
                    user.setRole(roleService.customer());
                    customer.setDOB(new Date());
                    customer.setUserData(user);
                    account.setBalance(1000D);
                    customer.setPAN(12345L);
                    customer.setAccounts(Collections.singletonList(account));
                    return customer;
                }
        ));
    }
}
