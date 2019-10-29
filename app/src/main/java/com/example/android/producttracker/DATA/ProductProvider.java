package com.example.android.producttracker.DATA;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.example.android.producttracker.DATA.ProductContract.ProductEntry;
public class ProductProvider extends ContentProvider {
private ProductDBHelper productDBHelper;
private static final UriMatcher sUriMatcher= new UriMatcher(UriMatcher.NO_MATCH);
private static final int all_products=100;
private static final int one_product=200;

    @Override
    public boolean onCreate() {
        productDBHelper=new ProductDBHelper(getContext());
        return true;
    }

    static {
        sUriMatcher.addURI(ProductContract.authority,ProductContract.path,all_products);
        sUriMatcher.addURI(ProductContract.authority,ProductContract.path+"/#",one_product);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String s1) {
        SQLiteDatabase sqLiteDatabase=productDBHelper.getReadableDatabase();
        Cursor cursor;
        int match=sUriMatcher.match(uri);
        switch (match){
            case all_products:
                cursor=sqLiteDatabase.query(ProductEntry.tableName,projection,selection,selectionArgs,null,null,null);
                break;
            case  one_product:
                selection=ProductEntry._ID+"=?";
                selectionArgs=new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor=sqLiteDatabase.query(ProductEntry.tableName,projection,selection,selectionArgs,null,null,null);
                break;

                default: throw new IllegalArgumentException("Invalid uri"+uri);
        }

            cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }


    @Override
    public String getType( Uri uri) {
        int match=sUriMatcher.match(uri);
        switch (match){
            case all_products:
                return ProductEntry.MIMIE_DIR;
            case one_product:
                return ProductEntry.MIME_SINGLE;
                default: throw new IllegalArgumentException("Can't resolve uri"+uri);
        }

    }

    @Override
    public Uri insert( Uri uri,  ContentValues contentValues) {
        SQLiteDatabase sqLiteDatabase = productDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        long id=-1;
        switch (match) {
            case all_products:
               id =sqLiteDatabase.insert(ProductEntry.tableName,null,contentValues);
                if (id==-1)
                    throw new IllegalArgumentException("Insertion failed"+uri);
                break;
                default:
                    throw new IllegalArgumentException("Insertion failed"+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,id);
    }
    @Override
    public int delete( Uri uri, String selection,  String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase=productDBHelper.getWritableDatabase();
        int match=sUriMatcher.match(uri);
        int rowsDeleted=0;
        switch (match){
            case all_products:
               rowsDeleted= sqLiteDatabase.delete(ProductEntry.tableName,selection,selectionArgs);
               if (rowsDeleted!=0)
                   getContext().getContentResolver().notifyChange(uri,null);
               return rowsDeleted;
            case one_product:
                selection=ProductEntry._ID+"=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted=sqLiteDatabase.delete(ProductEntry.tableName,selection,selectionArgs);
                getContext().getContentResolver().notifyChange(uri,null);
                return rowsDeleted;

                default: throw new IllegalArgumentException("Deletion failed"+uri);
        }

    }

    @Override
    public int update( Uri uri,  ContentValues contentValues, String selection, String[] selectionArgs) {
        int match=sUriMatcher.match(uri);

        switch (match){
            case all_products:
                return updateProduct(uri,contentValues,selection,selectionArgs);

            case one_product:
                selection=ProductEntry._ID+"=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
              return   updateProduct(uri,contentValues,selection,selectionArgs);

                default: throw new IllegalArgumentException("Update failed "+ uri);
        }

    }
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = productDBHelper.getWritableDatabase();
        if (values.size() == 0)
            return 0;
        getContext().getContentResolver().notifyChange(uri, null);
        int rowsUpdated= database.update(ProductEntry.tableName, values, selection, selectionArgs);
        if (rowsUpdated!=0)
            getContext().getContentResolver().notifyChange(uri,null);
        return rowsUpdated;
    }
}
