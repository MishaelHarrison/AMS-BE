package AMS.controllers;

import AMS.models.Dto.RoleResponse;
import AMS.security.annotations.AdminOrUserFilter;
import AMS.services.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ResponseBuilder;

import java.security.Principal;

@RestController
@RequestMapping("/info")
public class UserInfoController {

    @Autowired private UserInfoService service;

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleBadError(){
        return ResponseEntity.status(HttpStatus.CONFLICT).body("User data has been modified/removed since login");
    }

    @AdminOrUserFilter
    @GetMapping("/role")
    public ResponseEntity<RoleResponse> getRole(Principal user){
        return ResponseEntity.ok(service.getRole(Long.parseLong(user.getName())));
    }

    @AdminOrUserFilter
    @GetMapping("/self")
    public ResponseEntity<?> getOwnInfo(Principal user){
        return ResponseEntity.ok(service.getOwnInfo(Long.parseLong(user.getName())));
    }

}
