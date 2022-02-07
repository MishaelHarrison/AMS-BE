package AMS.models.Dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class AccountOut {
    private Long id;
    private Double balance;
    private List<TransactionOut> transactions;
}
