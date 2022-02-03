package AMS.models.Dto;

import lombok.Data;

import java.util.List;

@Data
public class AccountOut {
    private Long id;
    private Double balance;
    private List<TransactionOut> transactions;
}
