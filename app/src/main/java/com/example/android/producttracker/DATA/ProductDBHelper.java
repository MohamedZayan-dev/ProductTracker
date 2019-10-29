package com.example.android.producttracker.DATA;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.producttracker.DATA.ProductContract.ProductEntry;

public class ProductDBHelper extends SQLiteOpenHelper {
    private static final String dbName="inventory.db";
    private static final int dbVersion=1;
    public ProductDBHelper(Context context) {
        super(context, dbName,null,dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    String SqlTable="create table "+ProductEntry.tableName+" ("+ProductEntry.col_id+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ProductEntry.col_name
            +" TEXT not null, "+ProductEntry.col_price+" INTEGER not null, "+ProductEntry.col_quantity+" INTEGER NOT NULL, "+ProductEntry.col_image+" TEXT )";
    sqLiteDatabase.execSQL(SqlTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
//create table product (id integer primary key autoincrement,name text not null,price integer not null, quantity integer not null)