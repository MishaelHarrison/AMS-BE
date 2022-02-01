package AMS.security.components;

import AMS.models.entities.Role;
import AMS.models.entities.User;
import AMS.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class UserLoginProcessor implements UserDetailsService {

    @Autowired
    private UserRepo repo;

    private static Collection<? extends GrantedAuthority> generateAuthorities(String... authorities){
        return Arrays.stream(authorities).map(x->(GrantedAuthority) () -> x).collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String id) {
        User user;
        try {
            user = repo.findById(Long.parseLong(id)).orElse(null);
        }catch (NumberFormatException e){
            user = null;
        }
        if (user == null) {
            throw new UsernameNotFoundException(id);
        }
        return new MyUserPrincipal(user);
    }

    public static class MyUserPrincipal implements UserDetails {
        private final User user;

        public MyUserPrincipal(User user) {
            this.user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return user.getRole().equals(Role.manager())?generateAuthorities("ROLE_ADMIN"):generateAuthorities("ROLE_USER");
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getUsername() {
            return user.getId().toString();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}