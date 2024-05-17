package homework1.dto;

import homework1.domain.Currency;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class AccountDTO {
    private Currency currency;
    private double balance;
}
