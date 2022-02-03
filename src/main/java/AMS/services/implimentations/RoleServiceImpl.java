package AMS.services.implimentations;

import AMS.models.entities.Role;
import AMS.repos.RoleRepo;
import AMS.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleRepo roleRepo;

    @Override
    public Role manager() {
        return generateRole("MANAGER");
    }

    @Override
    public Role customer() {
        return generateRole("CUSTOMER");
    }

    private Role generateRole(String name){
        return roleRepo.findByName(name).orElseGet(()->{
            Role role = new Role();
            role.setName(name);
            return roleRepo.save(role);
        });
    }
}
