package homework1.domain;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
@JsonFilter("customerFilter")
public class Customer {
    private Long id = null;
    private String name;
    private String email;
    private Integer age;
    private List<Account> accounts = new ArrayList<>();

    public Customer(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }
}
