package AMS.controllers;

import AMS.exceptions.MissingLoggedUserException;
import AMS.models.Dto.CustomerExtendedInfo;
import AMS.models.Dto.RoleResponse;
import AMS.security.annotations.AdminOrUserFilter;
import AMS.security.annotations.UserFilter;
import AMS.services.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.builders.ResponseBuilder;

import java.security.Principal;

@RestController
@CrossOrigin("*")
@RequestMapping("/info")
public class UserInfoController {

    @Autowired private UserInfoService service;

    @ExceptionHandler(MissingLoggedUserException.class)
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

    @UserFilter
    @GetMapping("/self/extended")
    public ResponseEntity<CustomerExtendedInfo> getExtendedInfo(Principal user){
        return ResponseEntity.ok(service.getExtendedInfo(Long.parseLong(user.getName())));
    }

}
