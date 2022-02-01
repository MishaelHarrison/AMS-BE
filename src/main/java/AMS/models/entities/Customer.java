package AMS.models.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    private Long PAN;
    private String CitizenId;
    private String name;
    private String address;
    private String email;
    private Date DOB;

    @OneToOne(cascade = CascadeType.ALL)
    private User userData;

    @OneToMany
    @JoinColumn
    private List<Account> accounts;

}