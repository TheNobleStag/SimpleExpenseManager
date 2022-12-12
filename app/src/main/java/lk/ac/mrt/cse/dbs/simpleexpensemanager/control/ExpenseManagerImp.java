package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PermanentAccount;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PermanentTransaction;

public class ExpenseManagerImp extends ExpenseManager{
    @SuppressWarnings("FieldMayBeFinal")
    private Context context;

    public ExpenseManagerImp(Context context) {
        this.context = context;
        try{
            setup();
        }
        catch(ExpenseManagerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void setup() throws ExpenseManagerException {
        PermanentAccount acc = new PermanentAccount(this.context);
        PermanentTransaction tra = new PermanentTransaction(this.context);

        setAccountsDAO(acc);
        setTransactionsDAO(tra);
    }
}
