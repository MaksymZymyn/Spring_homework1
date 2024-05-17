package homework1.service;

import homework1.domain.Account;
import homework1.domain.Customer;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    Account save(Account obj);

    boolean delete(Account obj);

    void deleteAll(List<Account> entities);

    void saveAll(List<Account> entities);

    List<Account> findAll();

    boolean deleteById(Long id);

    Account getById(Long id);

    Account findByNumber(String accountNumber);

    Account deposit(String number, double amount);

    boolean withdraw(String accountNumber, double amount);

    void transfer(String from, String to, double amount);
}
