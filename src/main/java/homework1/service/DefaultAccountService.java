package homework1.service;

import homework1.dao.AccountDao;
import homework1.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultAccountService implements AccountService {

    private final AccountDao accountDao;

    @Override
    public Account save(Account account) {
        return accountDao.save(account);
    }

    @Override
    public boolean delete(Account account) {
        return accountDao.delete(account);
    }

    @Override
    public void deleteAll(List<Account> accounts) {
        accountDao.deleteAll(accounts);
    }

    @Override
    public void saveAll(List<Account> accounts) {
        accountDao.saveAll(accounts);
    }

    @Override
    public List<Account> findAll() {
        return accountDao.findAll();
    }

    @Override
    public boolean deleteById(Long id) {
        return accountDao.deleteById(id);
    }

    @Override
    public Account getById(Long id) {
        return accountDao.getById(id);
    }

    @Override
    public Account findByNumber(String accountNumber) {
        return accountDao.findByNumber(accountNumber);
    }

    @Override
    public Account deposit(String number, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }

        Account account = accountDao.findByNumber(number);
        if (account != null) {
            double balance = account.getBalance();
            account.setBalance(balance + amount);
            return account;
        } else {
            throw new IllegalArgumentException("Account with number " + number + " not found");
        }
    }

    @Override
    public boolean withdraw(String accountNumber, double amount) {
        Account account = accountDao.findByNumber(accountNumber);
        if (account.getBalance() >= amount && amount > 0) {
            account.setBalance(account.getBalance() - amount);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void transfer(String fromAccountNumber, String toAccountNumber, double amount) {
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new IllegalArgumentException("From and To account numbers cannot be the same");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than 0");
        }

        Account fromAccount = accountDao.findByNumber(fromAccountNumber);
        Account toAccount = accountDao.findByNumber(toAccountNumber);

        if (fromAccount.getBalance() >= amount) {
            fromAccount.setBalance(fromAccount.getBalance() - amount);
            toAccount.setBalance(toAccount.getBalance() + amount);
        } else {
            throw new IllegalArgumentException("Insufficient balance in the from account");
        }
    }
}
