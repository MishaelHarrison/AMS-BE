package AMS.services;

import AMS.models.Dto.*;

public interface CustomerDataService {
    CustomerOut updateCustomer(CustomerUpdate data, long id);

    void updatePassword(PasswordUpdate data, long id);

    AccountOut createAccount(MoneyRequest data, long id);
}
