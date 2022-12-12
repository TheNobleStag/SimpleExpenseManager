package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DBHandler extends SQLiteOpenHelper {
    private static final Integer DB_VERSION = 1;
    private static final String DB_NAME = "200073D";
    private static final String BANK_NAME = "BankName";
    private static final String ACCOUNT_TABLE = "Accounts";
    private static final String ID_COL = "AccountNo";
    private static final String NAME_COL = "Name";
    private static final String BALANCE_COL = "Balance";

    private static final String LOG_TABLE = "Transactions";
    private static final String LOG_ID = "ID";
    private static final String TYPE_COL = "Type";
    private static final String DATE_COL = "Time";
    private static final String AMOUNT_COL = "Amount";

    private static DBHandler handler = null;
    private DBHandler(Context context){
        super(context, DB_NAME,null,DB_VERSION);
    }

    public static DBHandler getDBHandler(Context context){
        if (handler==null){
            handler = new DBHandler(context);
        }
        return handler;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query1 = "CREATE TABLE " + ACCOUNT_TABLE + " ("
                + BANK_NAME + " TEXT NOT NULL, "
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NAME_COL + " TEXT NOT NULL,"
                + BALANCE_COL + " REAL CHECK(" + BALANCE_COL + " >0))";

        String query2 = "CREATE TABLE " + LOG_TABLE + " ("
                + LOG_ID + " INTERGER PRIMARY KEY AUTOINCREMENT,"
                + ID_COL + " INTEGER NOT NULL,"
                + DATE_COL + " DATE NOT NULL,"
                + TYPE_COL + " TEXT NOT NULL,"
                + AMOUNT_COL + "REAL NOT NULL,"
                + " FOREIGN KEY (" + ID_COL + ") REFERENCES " + ACCOUNT_TABLE + "(" + ID_COL + "))";
        db.execSQL(query1);
        db.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LOG_TABLE);
        onCreate(db);
    }

    public void removeAccount(String accountNo) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(ACCOUNT_TABLE, ID_COL + "=?",new String[]{accountNo});

        db.close();
    }

    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Account account = getAccount(accountNo);

        switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }
        values.put(ID_COL, account.getAccountNo());
        values.put(BANK_NAME, account.getBankName());
        values.put(NAME_COL, account.getAccountHolderName());
        values.put(AMOUNT_COL, account.getBalance());

        db.update(ACCOUNT_TABLE, values, ID_COL + " =? ", new String[]{accountNo});
        db.close();
    }

    public void addAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(NAME_COL, account.getAccountHolderName());
        values.put(ID_COL, account.getAccountNo());
        values.put(BALANCE_COL, account.getBalance());
        values.put(BANK_NAME, account.getBankName());

        db.insert(ACCOUNT_TABLE, null, values);

        db.close();
    }

    public void addNewLog(Date date, String accountNo, ExpenseType expenseType, double amount) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DATE_COL, String.valueOf(date));
        values.put(ID_COL, accountNo);
        values.put(TYPE_COL, String.valueOf(expenseType));
        values.put(AMOUNT_COL, amount);

        db.insert(LOG_TABLE, null, values);

        db.close();
    }

    public List<String> getAccountNumbersList() {
        Cursor accCursor = this.getReadableDatabase().rawQuery("SELECT " + ID_COL + " FROM " + ACCOUNT_TABLE, null);
        List<String> accList= new ArrayList<>();
        if (accCursor.moveToFirst()){
            do{
                accList.add(accCursor.getString(0));
            }
            while(accCursor.moveToNext());
        }
        accCursor.close();
        return accList;
    }

    public List<Account> getAccountsList() {
        Cursor accCursor = this.getReadableDatabase().rawQuery("SELECT * FROM " + ACCOUNT_TABLE, null);
        List<Account> accList= new ArrayList<>();
        if (accCursor.moveToFirst()){
            do{
                accList.add(new Account(accCursor.getString(1),
                        accCursor.getString(0),
                        accCursor.getString(2),
                        accCursor.getDouble(3)));
            }
            while (accCursor.moveToNext());
        }
        accCursor.close();
        return accList;
    }

    public Account getAccount(String accountNo) {
        Cursor accCursor = this.getReadableDatabase().rawQuery("SELECT * FROM " + ACCOUNT_TABLE + " WHERE " + ID_COL + "=?", new String[]{accountNo});
        Account acc = new Account(accCursor.getString(1),
                accCursor.getString(0),
                accCursor.getString(2),
                accCursor.getDouble(3));
        accCursor.close();
        return acc;
    }

    public List<Transaction> getAllTransactions() throws ParseException {
        Cursor accCursor = this.getReadableDatabase().rawQuery("SELECT * FROM " + LOG_TABLE, null);
        List<Transaction> accList = new ArrayList<>();
        if (accCursor.moveToFirst()) {
            do {
                ExpenseType T;
                @SuppressLint("SimpleDateFormat") Date date = new SimpleDateFormat("dd/MM/yyyy").parse(accCursor.getString(1));

                if (Objects.equals(accCursor.getString(3), "EXPENSE")){
                    T = ExpenseType.EXPENSE;
                }
                else{
                    T = ExpenseType.INCOME;
                }
                accList.add(new Transaction(date,
                        accCursor.getString(2),
                        T,
                        accCursor.getDouble(4)));
            }
            while (accCursor.moveToNext());
        }
        accCursor.close();
        return accList;
    }

    public List<Transaction> getPaginatedTransactions(int limit) throws ParseException {
        List<Transaction> temp = getAllTransactions();
        int tempLimit = temp.size()-limit;
        return temp.subList(tempLimit,temp.size());
    }
}
