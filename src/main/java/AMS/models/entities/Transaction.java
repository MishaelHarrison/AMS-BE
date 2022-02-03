package AMS.models.entities;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    private Long TransactionReferenceNumber;
    private Date time;
    private TransactionType type;
    private TransactionSubType subType;
    private double amount;

    @ManyToOne
    private Account account;

    @PrePersist
    private void preCreation(){
        time = new Date();
    }

    public enum TransactionType{
        Debit, Credit
    }

    public enum TransactionSubType{
        Cash, Transfer
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Transaction that = (Transaction) o;
        return Id != null && Objects.equals(Id, that.Id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}