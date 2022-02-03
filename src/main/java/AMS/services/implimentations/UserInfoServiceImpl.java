package AMS.services.implimentations;

import AMS.exceptions.MissingLoggedUserException;
import AMS.models.Dto.*;
import AMS.models.entities.Customer;
import AMS.models.entities.Transaction;
import AMS.models.entities.User;
import AMS.repos.CustomerRepo;
import AMS.repos.UserRepo;
import AMS.services.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired private UserRepo userRepo;
    @Autowired private CustomerRepo customerRepo;

    @Override
    public RoleResponse getRole(long id) {
        return new RoleResponse(userRepo.findById(id).orElseThrow(MissingLoggedUserException::new).getRole().getName());
    }

    @Override
    public Object getOwnInfo(long id) {
        User user = userRepo.findById(id).orElseThrow(MissingLoggedUserException::new);
        if (user.getRole().isCustomer()) return DtoFromEnt(customerRepo.findByUser(user.getId())
                .orElseThrow(MissingLoggedUserException::new));
        if (user.getRole().isManager()) return DtoFromEnt(user);
        return ResponseEntity.noContent();
    }

    @Override
    public CustomerExtendedInfo getExtendedInfo(long id) {
        return bigInfo(customerRepo.findByUser(id).orElseThrow(MissingLoggedUserException::new));
    }

    private CustomerExtendedInfo bigInfo(Customer ent){
        CustomerExtendedInfo ret = new CustomerExtendedInfo();
        ret.setAccounts(ent.getAccounts().stream().map(account->{
            AccountOut dto = new AccountOut();
            dto.setBalance(account.getBalance());
            dto.setId(account.getId());
            dto.setTransactions(account.getTransactions().stream()
                    .sorted(Comparator.comparing(Transaction::getTime)).limit(5).map(x->{
                TransactionOut transaction = new TransactionOut();
                transaction.setAmount(x.getAmount());
                return transaction;
            }).collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList()));
        ret.setUserId(ent.getUserData().getId());
        ret.setCitizenId(ent.getCitizenId());
        ret.setAddress(ent.getAddress());
        ret.setEmail(ent.getEmail());
        ret.setName(ent.getName());
        ret.setDOB(ent.getDOB());
        ret.setPAN(ent.getPAN());
        return ret;
    }

    private ManagerOut DtoFromEnt(User ent){
        ManagerOut ret = new ManagerOut();
        ret.setId(ent.getId());
        return ret;
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
}
