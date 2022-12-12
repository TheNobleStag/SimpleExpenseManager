package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;

import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PermanentAccount implements AccountDAO {
    private final DBHandler db;

    public PermanentAccount(Context context) {
        this.db=DBHandler.getDBHandler(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        return db.getAccountNumbersList();
    }

    @Override
    public List<Account> getAccountsList() {
        return db.getAccountsList();
    }

    @Override
    public Account getAccount(String accountNo) {
        return db.getAccount(accountNo);
    }

    @Override
    public void addAccount(Account account) {
        db.addAccount(account);
    }

    @Override
    public void removeAccount(String accountNo) {
        db.removeAccount(accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) {
        db.updateBalance(accountNo,expenseType,amount);
    }
}
