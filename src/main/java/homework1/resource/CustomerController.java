package homework1.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import homework1.domain.Account;
import homework1.domain.Customer;
import homework1.dto.AccountDTO;
import homework1.dto.CustomerDTO;
import homework1.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customers")          /* http://localhost:9000/customers */
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class CustomerController {

    private final CustomerService customerService;
    private final ObjectMapper objectMapper;

    @ModelAttribute
    public void configureObjectMapper() {
        SimpleFilterProvider filters = new SimpleFilterProvider();
        filters.addFilter("customerFilter", SimpleBeanPropertyFilter.serializeAll());
        filters.addFilter("accountFilter", SimpleBeanPropertyFilter.filterOutAllExcept("number", "currency", "balance"));
        objectMapper.setFilterProvider(filters);
    }

    @Operation(summary = "Get all customers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all customers",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Customer.class))})
    })
    @GetMapping
    public List<Customer> getAllCustomers() {
        customerService.assignAccountsToCustomers();
        return customerService.findAll();
    }

    @Operation(summary = "Get a customer by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the customer",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Customer.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content)
    })
    @GetMapping("/{customerId}")
    public ResponseEntity<?> getById(@PathVariable Long customerId) {
        try {
            customerService.assignAccountsToCustomers();
            return ResponseEntity.ok(customerService.getById(customerId));
        } catch (RuntimeException e) {
            log.error("Customer not found with ID " + customerId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with ID " + customerId + " not found");
        }
    }

    @Operation(summary = "Create a new customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Customer.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid customer data supplied",
                    content = @Content)
    })
    @PostMapping
    public Customer create(@RequestBody CustomerDTO customerDTO) {
        Customer customer = new Customer(customerDTO.getName(), customerDTO.getEmail(), customerDTO.getAge());
        return customerService.save(customer);
    }

    @Operation(summary = "Update a customer by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Customer.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content)
    })
    @PutMapping("/id/{customerId}")
    public ResponseEntity<?> update(@PathVariable Long customerId,
                                    @RequestBody CustomerDTO customerDTO) {
        try {
            Customer currentCustomer = customerService.getById(customerId);
            if (customerDTO.getName() != null) {
                currentCustomer.setName(customerDTO.getName());
            }
            if (customerDTO.getEmail() != null) {
                currentCustomer.setEmail(customerDTO.getEmail());
            }
            if (customerDTO.getAge() != null) {
                currentCustomer.setAge(customerDTO.getAge());
            }

            Customer updatedCustomer = customerService.update(currentCustomer);
            if (updatedCustomer != null) {
                return ResponseEntity.ok(updatedCustomer);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with ID " + customerId + " not found");
            }
        } catch (IllegalArgumentException e) {
            log.error("Customer not found with ID " + customerId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with ID " + customerId + " not found");
        } catch (RuntimeException e) {
            log.error("An error occurred while updating the customer with ID " + customerId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the customer");
        }
    }

    @Operation(summary = "Delete a customer by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer deleted",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content)
    })
    @DeleteMapping("/{customerId}")
    public ResponseEntity <?> deleteById(@PathVariable Long customerId) {
        try {
            Customer customer = customerService.getById(customerId);
            customerService.deleteById(customer.getId());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e){
            log.error("Customer not found with ID " + customerId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with ID " + customerId + " not found");
        }
    }

    @Operation(summary = "Create an account for a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Customer.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid customer ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content)
    })
    @PostMapping("/{customerId}/accounts")
    public ResponseEntity<?> createAccount(@PathVariable Long customerId,
                                           @RequestBody AccountDTO accountDTO) {
        try {
            Customer customer = customerService.getById(customerId);
            customerService.createAccount(customer.getId(), accountDTO.getCurrency(), accountDTO.getBalance());
            customerService.update(customer);
            return ResponseEntity.ok(customer);
        } catch (RuntimeException e) {
            log.error("Customer not found with ID " + customerId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with ID " + customerId + " not found");
        }
    }

    @Operation(summary = "Delete an account from a customer by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account deleted",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid customer ID or account ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Account or customer not found",
                    content = @Content)
    })
    @DeleteMapping("/{customerId}/accounts/{accountNumber}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long customerId, @PathVariable String accountNumber) {
        try {
            Customer customer = customerService.getById(customerId);
            Account accountToDelete = null;
            for (Account account : customer.getAccounts()) {
                if (account.getNumber().equals(accountNumber)) {
                    accountToDelete = account;
                    break;
                }
            }
            if (accountToDelete != null) {
                customerService.deleteAccount(customer.getId(), accountToDelete.getNumber());
                customerService.update(customer);
                return ResponseEntity.ok("Account successfully deleted");
            } else {
                return ResponseEntity.badRequest().body("Account with number " + accountNumber + " not found");
            }
        } catch (IllegalArgumentException e) {
            log.error("Customer not found with ID " + customerId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with ID " + customerId + " not found");
        }
    }
}
