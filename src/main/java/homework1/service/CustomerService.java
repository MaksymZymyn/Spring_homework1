package homework1.service;

import homework1.domain.Account;
import homework1.domain.Currency;
import homework1.domain.Customer;

import java.util.List;

public interface CustomerService {
    Customer save(Customer obj);

    boolean delete(Customer obj);

    void deleteAll(List<Customer> entities);

    void saveAll(List<Customer> entities);

    List<Customer> findAll();

    boolean deleteById(Long id);

    Customer getById(Long id);

    Customer update(Customer customer);

    void createAccount(Long customerId, Currency currency, double amount);

    void deleteAccount(Long customerId, String accountNumber);

    void assignAccountsToCustomers();
}
