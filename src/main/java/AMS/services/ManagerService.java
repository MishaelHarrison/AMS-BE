package AMS.services;

import AMS.models.Dto.CustomerOut;
import AMS.models.Dto.InitialAccountCreation;

public interface ManagerService {
    CustomerOut getPan(Long PAN);

    Long createUser(InitialAccountCreation account);
}
