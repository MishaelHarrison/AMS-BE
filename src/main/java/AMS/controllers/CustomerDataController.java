package AMS.controllers;

import AMS.exceptions.MissingLoggedUserException;
import AMS.models.Dto.*;
import AMS.security.annotations.UserFilter;
import AMS.services.CustomerDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@CrossOrigin("*")
@RequestMapping("data/")
public class CustomerDataController {

    @Autowired private CustomerDataService customerDataService;

    @ExceptionHandler(MissingLoggedUserException.class)
    public ResponseEntity<?> handleBadError(){
        return ResponseEntity.status(HttpStatus.CONFLICT).body("User data has been modified/removed since login");
    }

    @UserFilter
    @PutMapping("update")
    public ResponseEntity<CustomerOut> updateCustomer(@RequestBody CustomerUpdate data, Principal user){
        return ResponseEntity.ok(customerDataService.updateCustomer(data, Long.parseLong(user.getName())));
    }

    @UserFilter
    @PutMapping("update/password")
    public ResponseEntity<Void> updatePassword(@RequestBody PasswordUpdate data, Principal user){
        customerDataService.updatePassword(data, Long.parseLong(user.getName()));
        return ResponseEntity.ok().build();
    }

    @UserFilter
    @PostMapping("newAccount")
    public ResponseEntity<AccountOut> addAccount(@RequestBody MoneyRequest data, Principal user){
        return ResponseEntity.ok(customerDataService.createAccount(data, Long.parseLong(user.getName())));
    }
}
