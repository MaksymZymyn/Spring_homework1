package homework1.domain;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.*;
import java.util.UUID;

@Setter
@Getter
@EqualsAndHashCode(of = "number")
@ToString(exclude = "customer")
@JsonFilter("accountFilter")
public class Account {
    private Long id = null;
    private final String number = UUID.randomUUID().toString();
    private Currency currency;
    private double balance = 0.0;
    private Customer customer;

    public Account(Currency currency, Customer customer) {
        this.currency = currency;
        this.customer = customer;
    }
}
