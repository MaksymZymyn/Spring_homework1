package homework1.dao;

import homework1.domain.Account;
import homework1.domain.Customer;

import java.util.*;

public class CustomerUtils {

    private static long nextCustomerId = 0;

    public static String[] names = {"John Doe", "Alice Smith", "Michael Johnson", "Emily Brown", "Daniel Wilson"};

    public static List<Customer> createCustomers() {
        List<Customer> customers = new ArrayList<>();
        for (String name : names) {
            String email = getDefaultCustomerEmail(name);
            int age = getDefaultCustomerAge(name);
            nextCustomerId++;
            Customer newCustomer = new Customer(name, email, age);
            newCustomer.setId(nextCustomerId);
            customers.add(newCustomer);
        }
        return customers;
    }

    public static String getDefaultCustomerEmail(String name) {
        return name.toLowerCase().replace(" ", "") + "@example.com";
    }

    public static int getDefaultCustomerAge(String name) {
        return name.length() % 50 + 20;
    }
}
