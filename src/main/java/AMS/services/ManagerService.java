package AMS.services;

import AMS.models.Dto.CustomerDtoOut;
import AMS.models.Dto.InitialAccountCreation;

public interface ManagerService {
    CustomerDtoOut getPan(Long PAN);

    Long createUser(InitialAccountCreation account);
}
