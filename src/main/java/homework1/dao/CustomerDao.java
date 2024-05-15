package homework1.dao;

import homework1.domain.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class CustomerDao implements Dao<Customer> {
    private static long nextId = 1;
    private List<Customer> customers = new ArrayList<>();

    public CustomerDao() {
        initializeDefaultCustomers();
    }

    private void initializeDefaultCustomers() {
        for (Customer customer : CustomerUtils.createCustomers()) {
            save(customer);
        }
    }

    @Override
    public Customer save(Customer customer) {
        if(!customers.contains(customer)) {
            customer.setId(nextId++);
            customers.add(customer);
            log.info("Customer saved: {}", customer);
        }
        return customer;
    }

    @Override
    public boolean delete(Customer customer) {
        if (customers.contains(customer)) {
            customers.remove(customer);
            log.info("Customer deleted: {}", customer);
            return true;
        }
        return false;
    }

    @Override
    public void deleteAll(List<Customer> currentCustomers) {
        log.info("Deleted {} customers", currentCustomers.size());
        customers.removeAll(currentCustomers);
    }

    @Override
    public void saveAll(List<Customer> currentCustomers) {
        log.info("Saved {} customers", currentCustomers.size());
        customers.addAll(currentCustomers);
    }

    @Override
    public List<Customer> findAll() {
        log.info("Retrieved all customers");
        return customers;
    }

    @Override
    public boolean deleteById(Long id) {
        boolean removed = customers.removeIf(customer -> customer.getId().equals(id));
        if (removed) {
            log.info("Deleted customer with id {}", id);
        } else {
            log.error("Failed to delete customer with id {}", id);
        }
        return removed;
    }

    @Override
    public Customer getById(Long id) {
        log.info("Retrieving customer with id {}", id);
        return customers.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Customer with id " + id + " not found"));
    }
}
