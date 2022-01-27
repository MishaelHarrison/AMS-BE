package AMS.models.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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

    public static Role user(){
        return new Role("USER", 1L);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(name, role.name);
    }
}
