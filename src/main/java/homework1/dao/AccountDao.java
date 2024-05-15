package homework1.dao;

import homework1.domain.Account;
import homework1.domain.Currency;
import homework1.domain.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class AccountDao implements Dao<Account> {
    private static long nextId = 1;
    private List<Account> accounts = new ArrayList<>();

    public AccountDao() {
        initializeDefaultAccounts();
    }

    private void initializeDefaultAccounts() {
        List<Customer> customers = CustomerUtils.createCustomers();
        for (Customer customer : customers) {
            Account account = new Account(getCurrencyForCustomer(customer.getName()), customer);
            save(account);
        }
    }

    private static Currency getCurrencyForCustomer(String name) {
        String[] names = CustomerUtils.names;
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(name)) {
                return Currency.values()[i];
            }
        }
        throw new IllegalArgumentException("Unknown customer name: " + name);
    }

    @Override
    public Account save(Account account) {
        if (!accounts.contains(account)) {
            account.setId(nextId++);
            accounts.add(account);
            log.info("Account saved: {}", account);
        } else {
            log.error("Account already exists: {}", account);
        }
        return account;
    }

    @Override
    public boolean delete(Account account) {
        if (accounts.contains(account)) {
            accounts.remove(account);
            log.info("Account deleted: {}", account);
            return true;
        } else {
            log.error("Account not found for deletion: {}", account);
            return false;
        }
    }

    @Override
    public void deleteAll(List<Account> currentAccounts) {
        log.info("Deleted {} accounts", currentAccounts.size());
        accounts.removeAll(currentAccounts);
    }

    @Override
    public void saveAll(List<Account> currentAccounts) {
        log.info("Saved {} accounts", currentAccounts.size());
        accounts.addAll(currentAccounts);
    }

    @Override
    public List<Account> findAll() {
        log.info("Retrieved all accounts");
        return accounts;
    }

    @Override
    public boolean deleteById(Long id) {
        boolean removed = accounts.removeIf(account -> account.getId().equals(id));
        if (removed) {
            log.info("Deleted account with id {}", id);
        } else {
            log.error("Failed to delete account with id {}", id);
        }
        return removed;
    }

    @Override
    public Account getById(Long id) {
        log.info("Retrieving account with id {}", id);
        return accounts.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Account with id " + id + " not found"));
    }

    public Account findByNumber(String accountNumber) {
        log.info("Retrieving account by number {}", accountNumber);
        return accounts.stream()
                .filter(account -> account.getNumber().equals(accountNumber))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Account with number " + accountNumber + " not found"));
    }

    private boolean isMatchingCustomerId(Account account, Long customerId) {
        Customer currentCustomer = account.getCustomer();
        return currentCustomer != null && currentCustomer.getId().equals(customerId);
    }

    private void updateCustomerDataInAccount(Account account, Customer customer) {
        Customer currentCustomer = account.getCustomer();
        currentCustomer.setName(customer.getName());
        currentCustomer.setEmail(customer.getEmail());
        currentCustomer.setAge(customer.getAge());
        log.info("Updated customer data in account with id {}", account.getId());
    }

    public List<Account> updateCustomerData(Customer customer) {
        log.info("Updating customer data for customer with id {}", customer.getId());
        return findAll().stream()
                .filter(a -> isMatchingCustomerId(a, customer.getId()))
                .peek(account -> updateCustomerDataInAccount(account, customer))
                .collect(Collectors.toList());
    }
}
