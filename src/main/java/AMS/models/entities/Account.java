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
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    private double balance;

    private Date createdOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId", insertable = false, updatable = false)
    @ToString.Exclude
    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn
    @ToString.Exclude
    private List<Transaction> transactions;

    @PrePersist
    private void onCreation(){
        createdOn = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Account account = (Account) o;
        return Id != null && Objects.equals(Id, account.Id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
