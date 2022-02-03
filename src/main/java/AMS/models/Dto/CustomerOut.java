package AMS.models.Dto;

import lombok.Data;

import java.util.Date;

@Data
public class CustomerOut {

    private String CitizenId;
    private String address;
    private String email;
    private String name;
    private Long customerId;
    private Long userId;
    private Long PAN;
    private Date DOB;

}
