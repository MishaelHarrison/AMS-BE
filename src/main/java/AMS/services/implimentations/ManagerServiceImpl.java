package AMS.services.implimentations;

import AMS.models.Dto.CustomerOut;
import AMS.models.Dto.InitialAccountCreation;
import AMS.models.entities.Account;
import AMS.models.entities.Customer;
import AMS.models.entities.Role;
import AMS.models.entities.User;
import AMS.repos.CustomerRepo;
import AMS.services.ManagerService;
import AMS.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;

@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired private CustomerRepo customerRepo;

    @Autowired private PasswordEncoder encoder;

    @Autowired private RoleService roleService;

    @Override
    public CustomerOut getPan(Long PAN) {
        return DtoFromEnt(customerRepo.findByPAN(PAN).orElse(null));
    }

    @Override
    public Long createUser(InitialAccountCreation info) {
        try {
            return customerRepo.save(EntFromDto(info)).getUserData().getId();
        }catch (Exception e){
            return null;
        }
    }

    private Customer EntFromDto(InitialAccountCreation dto){
        if(dto==null) return null;
        Customer customer = new Customer();
        Account account = new Account();
        User user = new User();
        customer.setAccounts(Collections.singletonList(account));
        user.setPassword(encoder.encode(dto.getPassword()));
        customer.setCitizenId(dto.getCitizenId());
        customer.setAddress(dto.getAddress());
        account.setBalance(dto.getBalance());
        customer.setEmail(dto.getEmail());
        customer.setName(dto.getName());
        customer.setPAN(dto.getPAN());
        customer.setDOB(dto.getDOB());
        user.setRole(roleService.customer());
        customer.setUserData(user);
        return customer;
    }

    private CustomerOut DtoFromEnt(Customer ent){
        if(ent==null) return null;
        CustomerOut ret = new CustomerOut();
        ret.setUserId(ent.getUserData().getId());
        ret.setCitizenId(ent.getCitizenId());
        ret.setAddress(ent.getAddress());
        ret.setCustomerId(ent.getId());
        ret.setEmail(ent.getEmail());
        ret.setName(ent.getName());
        ret.setPAN(ent.getPAN());
        ret.setDOB(ent.getDOB());
        return ret;
    }
}
