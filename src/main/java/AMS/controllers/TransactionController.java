package AMS.controllers;

import AMS.exceptions.AccountNotFoundException;
import AMS.exceptions.InsufficientFundsException;
import AMS.exceptions.NegativeMoneySubmittedException;
import AMS.models.Dto.MoneyRequest;
import AMS.security.annotations.UserFilter;
import AMS.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@CrossOrigin("*")
@RequestMapping("transaction/")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @ExceptionHandler({InsufficientFundsException.class, NegativeMoneySubmittedException.class})
    public ResponseEntity<Void> handleBadMath(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<String> handleNoAccount(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot find account");
    }

    @UserFilter
    @PostMapping("deposit/{id}")
    public ResponseEntity<Void> deposit(@RequestBody MoneyRequest request, @PathVariable("id") Long id, Principal user){
        transactionService.deposit(request.getAmount(), Long.parseLong(user.getName()), id);
        return ResponseEntity.ok().build();
    }

    @UserFilter
    @PostMapping("withdrawal/{id}")
    public ResponseEntity<Void> withdrawal(@RequestBody MoneyRequest request, @PathVariable("id") Long id, Principal user){
        transactionService.withdrawal(request.getAmount(), Long.parseLong(user.getName()), id);
        return ResponseEntity.ok().build();
    }

    @UserFilter
    @PostMapping("transfer/{fromId}/{toId}")
    public ResponseEntity<Void> transfer(@RequestBody MoneyRequest request, @PathVariable("fromId") Long fromId, @PathVariable("toId") Long toId, Principal user){
        transactionService.transfer(request.getAmount(), Long.parseLong(user.getName()), fromId, toId);
        return ResponseEntity.ok().build();
    }
}
