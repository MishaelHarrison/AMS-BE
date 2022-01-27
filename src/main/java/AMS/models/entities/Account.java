package AMS.models.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    private double balance;

    @Column(name = "Customer_Id")
    private Long CustomerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Customer_Id", updatable = false, insertable = false)
    private Customer customer;

}
