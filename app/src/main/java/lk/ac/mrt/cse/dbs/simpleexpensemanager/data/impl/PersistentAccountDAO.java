package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    SQLiteDatabase db;
    private static String table1_name="AccountDetails";

    public PersistentAccountDAO(SQLiteDatabase db){
        this.db = db;
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> arr = new ArrayList<>();
        Cursor cur = db.rawQuery("select * from " + table1_name, null);
        cur.moveToFirst();
        try {
            if (cur.moveToFirst()) {
                do {
                    arr.add(cur.getString(cur.getColumnIndex("accountNo")));
                } while (cur.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }
        return arr;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> arr=new ArrayList<>();
        Cursor cur=db.rawQuery("select * from "+table1_name,null);

        try {
            if (cur.moveToFirst()) {
                do {
                    String account_no=cur.getString(cur.getColumnIndex("accountNo"));
                    String bank_name=cur.getString(cur.getColumnIndex("bankName"));
                    String account_holder=cur.getString(cur.getColumnIndex("accountHolderName"));
                    Double balance=cur.getDouble(cur.getColumnIndex("balance"));
                    Account acc=new Account(account_no,bank_name,account_holder,balance);
                    arr.add(acc);
                } while (cur.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }
        return arr;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Cursor cur=db.rawQuery("select * from "+table1_name+" where accountNo=?",new String[]{accountNo});
        String bank_name=cur.getString(cur.getColumnIndex("bankName"));
        String account_holder=cur.getString(cur.getColumnIndex("accountHolderName"));
        Double balance=cur.getDouble(cur.getColumnIndex("balance"));
        Account acc=new Account(accountNo,bank_name,account_holder,balance);
        return acc;
    }

    @Override
    public void addAccount(Account account) {
        ContentValues cv=new ContentValues();
        cv.put("accountNo",account.getAccountNo());
        cv.put("bankName",account.getBankName());
        cv.put("accountHolderName",account.getAccountHolderName());
        cv.put("balance",account.getBalance());
        db.insert(table1_name,null,cv);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        db.execSQL("delete from "+table1_name+" where accountNo=?",new String[]{accountNo});
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expense_Type, double amount) throws InvalidAccountException {
        SQLiteStatement statement = db.compileStatement("UPDATE AccountDetails SET balance = balance + ?");
        if(expense_Type == ExpenseType.EXPENSE){
            statement.bindDouble(1,-amount);
        }else{
            statement.bindDouble(1,amount);
        }
        statement.executeUpdateDelete();
    }
}
