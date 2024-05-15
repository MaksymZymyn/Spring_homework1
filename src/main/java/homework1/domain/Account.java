package homework1.domain;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.*;
import java.util.UUID;

@Setter
@Getter
@EqualsAndHashCode(of = "number")
@JsonFilter("accountFilter")
public class Account {
    private Long id = null;
    private final String number = UUID.randomUUID().toString();
    private Currency currency;
    private Double balance = 0.0;
    private Customer customer;

    public Account(Currency currency, Customer customer) {
        this.currency = currency;
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", currency=" + currency +
                ", balance=" + balance +
                '}';
    }
}
