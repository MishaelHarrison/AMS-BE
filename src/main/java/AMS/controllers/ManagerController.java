package AMS.controllers;

import AMS.models.Dto.CustomerOut;
import AMS.models.Dto.InitialAccountCreation;
import AMS.security.annotations.AdminFilter;
import AMS.services.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@CrossOrigin("*")
@RequestMapping("manager/")
public class ManagerController {

    @Autowired private ManagerService managerService;

    @AdminFilter
    @GetMapping("getPAN/{id}")
    public ResponseEntity<CustomerOut> getPan(@PathVariable Long id){
        CustomerOut ret = managerService.getPan(id);
        if (ret!=null) return ResponseEntity.ok(ret);
        return ResponseEntity.notFound().build();
    }

    @AdminFilter
    @PostMapping("newUser")
    public ResponseEntity<Long> createInitialUser(@RequestBody InitialAccountCreation account){
        Long ret = managerService.createUser(account);
        if(ret != null) return ResponseEntity.created(URI.create("ill/fill/this/in/later")).body(ret);
        return ResponseEntity.badRequest().build();
    }
}
