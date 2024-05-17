package homework1.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import homework1.domain.Account;
import homework1.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")         /* http://localhost:9000/accounts */
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class AccountController {

    private final AccountService accountService;
    private final ObjectMapper objectMapper;

    @ModelAttribute
    public void configureObjectMapper() {
        SimpleFilterProvider filters = new SimpleFilterProvider();
        filters.addFilter("accountFilter", SimpleBeanPropertyFilter.serializeAll());
        filters.addFilter("customerFilter", SimpleBeanPropertyFilter.serializeAllExcept("accounts"));
        objectMapper.setFilterProvider(filters);
    }

    @Operation(summary = "Get all accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all accounts",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Account.class))})
    })
    @GetMapping
    public List<Account> getAll() {
        return accountService.findAll();
    }

    @Operation(summary = "Get an account by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the account",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Account.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content)
    })
    @GetMapping("/{accountNumber}")
    public ResponseEntity<?> getByNumber(@PathVariable String accountNumber) {
        try {
            return ResponseEntity.ok(accountService.findByNumber(accountNumber));
        } catch (RuntimeException e){
            log.error("Account with number " + accountNumber + " not found", e);
            return ResponseEntity.badRequest().body("Account with number " + accountNumber + " not found");
        }
    }

    @Operation(summary = "Deposit an amount to an account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deposit successful",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Account.class))}),
            @ApiResponse(responseCode = "400", description = "Account not found",
                    content = @Content)
    })
    @PutMapping("/deposit/{accountNumber}")
    public ResponseEntity<?> deposit(@PathVariable String accountNumber, @RequestBody double amount) {
        try {
            Account updatedAccount = accountService.deposit(accountNumber, amount);
            return ResponseEntity.ok(updatedAccount);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                log.error("Account with number " + accountNumber + " not found", e);
                return ResponseEntity.badRequest().body("Account with number " + accountNumber + " not found");
            } else {
                log.error("Amount for deposit must be greater than 0", e);
                return ResponseEntity.badRequest().body("Amount for deposit must be greater than 0");
            }
        }
    }

    @Operation(summary = "Withdraw an amount from an account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Withdrawal successful",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Account.class))}),
            @ApiResponse(responseCode = "400", description = "Insufficient balance or account not found",
                    content = @Content)
    })
    @PutMapping("/withdrawal/{accountNumber}")
    public ResponseEntity<?> withdraw(@PathVariable String accountNumber,
                                      @RequestBody double amount) {
        try {
            boolean withdrawalSuccessful = accountService.withdraw(accountNumber, amount);
            if (withdrawalSuccessful) {
                return ResponseEntity.ok("Withdrawal successful");
            } else {
                return ResponseEntity.badRequest().body("Insufficient balance");
            }
        } catch (IllegalArgumentException e) {
            log.error("Error withdrawing amount: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Transfer an amount from one account to another")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfer successful",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Account.class))}),
            @ApiResponse(responseCode = "400", description = "Account not found or insufficient balance",
                    content = @Content)
    })
    @PutMapping("/transfer/{fromAccountNumber}/{toAccountNumber}")
    public ResponseEntity<?> transfer(@PathVariable String fromAccountNumber,
                                      @PathVariable String toAccountNumber,
                                      @RequestBody double amount) {
        try {
            accountService.transfer(fromAccountNumber, toAccountNumber, amount);
            return ResponseEntity.ok("Transfer successful");
        } catch (IllegalArgumentException e) {
            log.error("Error transferring amount: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
