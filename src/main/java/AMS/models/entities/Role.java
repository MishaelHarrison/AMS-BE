package AMS.models.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
public class Role {

    @Id
    private Long id;

    private String name;

    private Role(String name, Long id){
        this.name = name;
        this.id = id;
    }

    public static Role manager(){
        return new Role("MANAGER", 2L);
    }

    public static Role customer(){
        return new Role("CUSTOMER", 1L);
    }

    public boolean isCustomer(){
        return name.equals("CUSTOMER");
    }

    public boolean isManager(){
        return name.equals("MANAGER");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(name, role.name);
    }
}
