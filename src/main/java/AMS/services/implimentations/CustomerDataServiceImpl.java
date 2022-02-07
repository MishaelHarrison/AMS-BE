package AMS.services.implimentations;

import AMS.exceptions.MissingLoggedUserException;
import AMS.models.Dto.*;
import AMS.models.entities.Account;
import AMS.models.entities.Customer;
import AMS.models.entities.User;
import AMS.repos.CustomerRepo;
import AMS.repos.UserRepo;
import AMS.services.CustomerDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;

@Service
public class CustomerDataServiceImpl implements CustomerDataService {

    @Autowired private CustomerRepo customerRepo;
    @Autowired private UserRepo userRepo;

    @Autowired private PasswordEncoder encoder;

    @Override
    public CustomerOut updateCustomer(CustomerUpdate data, long id) {
        return DtoFromEnt(customerRepo.save(mapUpdate(customerRepo.findByUser(id)
                .orElseThrow(MissingLoggedUserException::new), data)));
    }

    @Override
    public void updatePassword(PasswordUpdate data, long id) {
        userRepo.save(mapUpdate(userRepo.findById(id).orElseThrow(MissingLoggedUserException::new), data));
    }

    @Override
    public AccountOut createAccount(MoneyRequest data, long id) {
        return DtoFromEnt(customerRepo.save(mapUpdate(
                        customerRepo.findByUser(id).orElseThrow(MissingLoggedUserException::new), data))
                .getAccounts().stream().min(Comparator.comparing(Account::getCreatedOn))
                .orElseThrow(()->new RuntimeException("Looks like i don't know what im doing")));
    }

    private Customer mapUpdate(Customer data, MoneyRequest update){
        Account account = new Account();
        account.setBalance(update.getAmount());
        data.getAccounts().add(account);
        return data;
    }

    private Customer mapUpdate(Customer data, CustomerUpdate update){
        data.setAddress(update.getAddress());
        data.setEmail(update.getEmail());
        data.setName(update.getName());
        return data;
    }

    private User mapUpdate(User data, PasswordUpdate update){
        data.setPassword(encoder.encode(update.getPassword()));
        return data;
    }

    private CustomerOut DtoFromEnt(Customer ent){
        CustomerOut ret = new CustomerOut();
        ret.setUserId(ent.getUserData().getId());
        ret.setCitizenId(ent.getCitizenId());
        ret.setAddress(ent.getAddress());
        ret.setCustomerId(ent.getId());
        ret.setEmail(ent.getEmail());
        ret.setName(ent.getName());
        ret.setDOB(ent.getDOB());
        ret.setPAN(ent.getPAN());
        return ret;
    }

    private AccountOut DtoFromEnt(Account ent){
        AccountOut ret = new AccountOut();
        ret.setTransactions(new ArrayList<>());//todo: change to actual
        ret.setBalance(ent.getBalance());
        ret.setId(ent.getId());
        return ret;
    }
}
