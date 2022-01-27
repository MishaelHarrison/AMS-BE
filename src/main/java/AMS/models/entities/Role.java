package AMS.models.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Role(String name){
        this.name = name;
    }

    public static Role manager(){
        return new Role("MANAGER");
    }

    public static Role user(){
        return new Role("USER");
    }

}
