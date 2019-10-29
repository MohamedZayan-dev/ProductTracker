package com.example.android.producttracker.DATA;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ProductContract
{
    public static final String authority="com.example.android.producttracker";
    public static final Uri base_uri=Uri.parse("content://"+authority);
    public static final String path="products";
    public ProductContract(){}

    public static final class ProductEntry implements BaseColumns{

        public static final Uri content_uri=Uri.withAppendedPath(base_uri,path);
        public static final String MIMIE_DIR= ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+authority+"/"+path;
        public static final String MIME_SINGLE=ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+authority+"/"+path;

        public static final String tableName="Product";

        public static final String col_id=BaseColumns._ID;
        public static final String col_name="name";
        public static final String col_price="price";
        public static final String col_quantity="quantity";
        public static final String col_image="image";
    }
}
