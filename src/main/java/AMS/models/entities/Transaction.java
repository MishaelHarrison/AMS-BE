package AMS.models.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    private Long TransactionReferenceNumber;
    private Date time;
    private TransactionType type;
    private TransactionSubType subType;
    private double amount;

    public enum TransactionType{
        Debit, Credit
    }

    public enum TransactionSubType{
        Cash, Transfer
    }
}

//(Transaction ID, Transaction reference number, Date Time,
//Type (Debit/Credit), SubType (Cash/Transfer), Current Balance