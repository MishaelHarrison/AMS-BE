package AMS.models.entities;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "customerId")
    @ToString.Exclude
    private List<Account> accounts;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Customer customer = (Customer) o;
        return Id != null && Objects.equals(Id, customer.Id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}