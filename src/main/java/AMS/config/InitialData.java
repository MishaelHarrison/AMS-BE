package AMS.config;

import AMS.models.entities.Role;
import AMS.models.entities.User;
import AMS.repos.UserRepo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
public class InitialData implements InitializingBean{

    @Autowired
    UserRepo userRepo;
    @Autowired
    PasswordEncoder encoder;

    @SafeVarargs
    private static <T> List<T> listOfThings(Supplier<T>... things){
        return Arrays.stream(things).map(Supplier::get).collect(Collectors.toList());
    }

    @Override
    public void afterPropertiesSet(){
        userRepo.saveAll(listOfThings(
                () -> {
                    User ret = new User();
                    ret.setPassword(encoder.encode("pass"));
                    ret.setRole(Role.user());
                    return ret;
                },
                () -> {
                    User ret = new User();
                    ret.setPassword(encoder.encode("pass"));
                    ret.setRole(Role.manager());
                    return ret;
                }
        ));
    }
}
