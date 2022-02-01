package AMS.services.implimentations;

import AMS.models.Dto.CustomerDtoOut;
import AMS.models.Dto.ManagerDtoOut;
import AMS.models.Dto.RoleResponse;
import AMS.models.entities.Customer;
import AMS.models.entities.User;
import AMS.repos.CustomerRepo;
import AMS.repos.UserRepo;
import AMS.services.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired private UserRepo userRepo;
    @Autowired private CustomerRepo customerRepo;

    @Override
    public RoleResponse getRole(long id) {
        return new RoleResponse(userRepo.findById(id).orElseThrow(()->new UsernameNotFoundException("username not found")).getRole().getName());
    }

    @Override
    public Object getOwnInfo(long id) {
        User user = userRepo.findById(id).orElseThrow(()->new UsernameNotFoundException("username not found"));
        if (user.getRole().isCustomer()) return DtoFromEnt(customerRepo.findByUser(user.getId())
                .orElseThrow(()->new UsernameNotFoundException("user data not found")));
        if (user.getRole().isManager()) return DtoFromEnt(user);
        return ResponseEntity.noContent();
    }

    private ManagerDtoOut DtoFromEnt(User ent){
        ManagerDtoOut ret = new ManagerDtoOut();
        ret.setId(ent.getId());
        return ret;
    }

    private CustomerDtoOut DtoFromEnt(Customer ent){
        CustomerDtoOut ret = new CustomerDtoOut();
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
