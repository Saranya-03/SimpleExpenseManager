package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    SQLiteDatabase db;
    private static String table2_name="TransactionsDetails";

    public PersistentTransactionDAO(SQLiteDatabase db){
        this.db =db;
    }


    @Override
    public void logTransaction(Date date_, String accountNo, ExpenseType expenseType, double amount){
        ContentValues cv = new ContentValues();
        cv.put("accountNo", accountNo);
        cv.put("expenseType",(expenseType == ExpenseType.EXPENSE) ? 0 : 1);
        cv.put("date",date_.getTime());
        cv.put("amount", amount);
        db.insert(table2_name, null, cv);
    }


    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> arr = new ArrayList<>();
        Cursor cur =  db.rawQuery( " select * from "+table2_name, null );
        try {
            if (cur.moveToFirst()) {
                do {
                    Date date=new Date(cur.getLong(cur.getColumnIndex("date")));
                    String account_No=cur.getString(cur.getColumnIndex("accountNo"));
                    ExpenseType expType=(cur.getInt(cur.getColumnIndex("expenseType")) == 0) ? ExpenseType.EXPENSE : ExpenseType.INCOME;
                    Double amount=cur.getDouble(cur.getColumnIndex("amount"));
                    Transaction transaction=new Transaction(date,account_No,expType,amount);
                    arr.add(transaction);
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
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> arr_Details = new ArrayList<>();
        Cursor cur = db.rawQuery( " select * from "+table2_name+" LIMIT"+limit, null);
        if (cur.moveToFirst()) {
            do {
                Date date=new Date(cur.getLong(cur.getColumnIndex("date")));
                String account_No=cur.getString(cur.getColumnIndex("accountNo"));
                ExpenseType expType=(cur.getInt(cur.getColumnIndex("expenseType")) == 0) ? ExpenseType.EXPENSE : ExpenseType.INCOME;
                Double amount=cur.getDouble(cur.getColumnIndex("amount"));
                Transaction transaction=new Transaction(date,account_No,expType,amount);
                arr_Details.add(transaction);
            } while (cur.moveToNext());
        }
        return  arr_Details;
    }
}
