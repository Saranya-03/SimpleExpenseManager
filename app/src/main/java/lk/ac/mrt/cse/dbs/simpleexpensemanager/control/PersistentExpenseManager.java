package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.*;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

public class PersistentExpenseManager extends ExpenseManager {
    Context context;
    public PersistentExpenseManager(Context context)  {
        this.context=context;
        try {
            setup();
        } catch (ExpenseManagerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setup() throws ExpenseManagerException {
        SQLiteDatabase db = context.openOrCreateDatabase("180543P_ExpenseManager", context.MODE_PRIVATE, null);


        db.execSQL("create table if not exists AccountDetails(" + "accountNo varchar primary key," + "bankName varchar," + "accountHolderName varchar," + "balance REAL" + " );");

        db.execSQL("create table if not exists TransactionsDetails(" + "Transaction_id integer primary key," + "accountNo varchar," + "expenseType int," + "amount REAL," + "date DATE," + "foreign key(accountNo) references Account(accountNo)" + ");");


        PersistentAccountDAO accountDAO = new PersistentAccountDAO(db);
        setAccountsDAO(accountDAO);
        setTransactionsDAO(new PersistentTransactionDAO(db));

        Account dummyAcct1 = new Account("12345A", "Yoda Bank", "Anakin Skywalker", 10000.0);
        Account dummyAcct2 = new Account("78945Z", "Clone BC", "Obi-Wan Kenobi", 80000.0);
        getAccountsDAO().addAccount(dummyAcct1);
        getAccountsDAO().addAccount(dummyAcct2);

    }
}
