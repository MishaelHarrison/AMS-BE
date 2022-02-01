package AMS.security.controllers;

import AMS.security.components.JwtUtil;
import AMS.security.models.LoginRequest;
import AMS.security.models.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
public class SecurityController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginRequest loginInfo)throws Exception{
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginInfo.getUsername(), loginInfo.getPassword())
            );
        } catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(LoginResponse.withMessage("Incorrect username or password"));
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginInfo.getUsername());
        String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(LoginResponse.withToken(jwt));
    }
}
