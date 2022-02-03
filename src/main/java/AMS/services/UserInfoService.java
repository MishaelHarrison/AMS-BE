package AMS.services;

import AMS.models.Dto.CustomerExtendedInfo;
import AMS.models.Dto.RoleResponse;

public interface UserInfoService {
    RoleResponse getRole(long id);

    Object getOwnInfo(long id);

    CustomerExtendedInfo getExtendedInfo(long id);
}
