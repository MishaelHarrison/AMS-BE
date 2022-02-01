package AMS.models.Dto;

import lombok.Data;

import java.util.Date;

@Data
public class InitialAccountCreation {
    private Double balance;
    private String CitizenId;
    private String password;
    private String address;
    private String email;
    private String name;
    private Date DOB;
    private Long PAN;
}
