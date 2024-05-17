package homework1.service;

import homework1.dao.AccountDao;
import homework1.dao.CustomerDao;
import java.util.List;

import homework1.domain.Account;
import homework1.domain.Currency;
import homework1.domain.Customer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Data
@Slf4j
public class DefaultCustomerService implements CustomerService {

    private final CustomerDao customerDao;
    private final AccountDao accountDao;
    private boolean accountsModified;

    @Override
    public Customer save(Customer customer) {
        return customerDao.save(customer);
    }

    @Override
    public boolean delete(Customer customer) {
        return customerDao.delete(customer);
    }

    @Override
    public void deleteAll(List<Customer> customers) {
        customerDao.deleteAll(customers);
    }

    @Override
    public void saveAll(List<Customer> customers) {
        customerDao.saveAll(customers);
    }

    @Override
    public List<Customer> findAll() {
        return customerDao.findAll();
    }

    @Override
    public boolean deleteById(Long id) {
        Customer customer = customerDao.getById(id);
        if (customer != null) {
            // Видаляємо всі акаунти, пов'язані з клієнтом
            List<Account> customerAccounts = customer.getAccounts();
            for (Account account : customerAccounts) {
                accountDao.delete(account);
            }

            // Видаляємо самого клієнта
            boolean deletedCustomer = customerDao.deleteById(id);

            // Оновлюємо дані клієнта у всіх інших акаунтах
            if (deletedCustomer) {
                accountDao.updateCustomerData(customer);
            }

            return deletedCustomer;
        } else {
            throw new IllegalArgumentException("Customer with id " + id + " not found");
        }
    }

    @Override
    public Customer getById(Long id) {
        return customerDao.getById(id);
    }

    @Override
    public Customer update(Customer customer) {
        Customer existingCustomer = customerDao.getById(customer.getId());
        if (existingCustomer != null) {
            existingCustomer.setName(customer.getName());
            existingCustomer.setEmail(customer.getEmail());
            existingCustomer.setAge(customer.getAge());

            // Оновлення даних клієнта в акаунтах
            List<Account> updatedAccounts = accountDao.updateCustomerData(existingCustomer);

            // Збереження оновленого клієнта
            Customer savedCustomer = customerDao.save(existingCustomer);

            // Логування оновлення акаунтів
            for (Account account : updatedAccounts) {
                log.info("Updated account for customer {}: {}", existingCustomer.getId(), account);
            }

            return savedCustomer;
        } else {
            return null;
        }
    }

    @Override
    public void createAccount(Long customerId, Currency currency, double amount) {
        Customer customer = customerDao.getById(customerId);

        if (customer == null) {
            throw new IllegalArgumentException("Customer not found with id: " + customerId);
        }

        Account account = new Account(currency, customer);
        account.setBalance(amount);

        // Перевірка наявності акаунта з таким же номером
        boolean accountExists = customer.getAccounts().stream()
                .anyMatch(existingAccount -> existingAccount.getNumber().equals(account.getNumber()));

        if (accountExists) {
            throw new IllegalArgumentException("Account with number " + account.getNumber() + " already exists for customer with id: " + customerId);
        }

        // Додавання нового акаунта до клієнта
        List<Account> customerAccounts = customer.getAccounts();
        customerAccounts.add(account);

        // Збереження акаунта в accountDao
        accountDao.save(account);
    }

    @Override
    public void deleteAccount(Long customerId, String accountNumber) {
        Customer customer = customerDao.getById(customerId);
        if (customer != null) {
            // Знаходимо акаунт для видалення
            Account accountToDelete = customer.getAccounts().stream()
                    .filter(account -> account.getNumber().equals(accountNumber))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Account with number " + accountNumber + " not found"));

            // Видаляємо акаунт з акаунтів клієнта
            boolean removed = customer.getAccounts().removeIf(account -> account.getNumber().equals(accountNumber));
            if (removed) {
                // Оновлюємо accountDao
                accountDao.delete(accountToDelete);
                setAccountsModified(true);
            }
        } else {
            throw new IllegalArgumentException("Customer not found with id: " + customerId);
        }
    }

    @Override
    public void assignAccountsToCustomers() {
        if (isAccountsModified()) {
            log.warn("Skipping account assignment as accounts were modified.");
            return;
        }

        List<Account> allAccounts = accountDao.findAll();
        List<Customer> allCustomers = customerDao.findAll();

        for (Account account : allAccounts) {
            Customer customer = account.getCustomer();
            if (customer != null) {
                for (Customer cust : allCustomers) {
                    if (cust.getId().equals(customer.getId())) {
                        boolean accountExists = cust.getAccounts().stream()
                                .anyMatch(existingAccount -> existingAccount.getNumber().equals(account.getNumber()));

                        if (!accountExists) {
                            cust.getAccounts().add(account);
                        } else {
                            log.warn("Account with number " + account.getNumber() + " already exists for customer with id: " + customer.getId());
                        }
                        break;
                    }
                }
            }
        }
    }
}
