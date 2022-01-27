package AMS.models.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    private String password;

    @ManyToOne
    private Role role;
}

//(User Id, Password, Role)
