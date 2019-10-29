package com.example.android.producttracker;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.android.producttracker.DATA.ProductContract.ProductEntry;
import com.example.android.producttracker.DATA.ProductContract;
public class ProductAdapter extends CursorAdapter {
    TextView quanityText;
    String quantity;
    Uri image_uri;
    Context context;
     int quant=0;
    public ProductAdapter(Context context, Cursor c) {
        super(context, c);
        this.context=context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_list,viewGroup,false);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {

        TextView nameText = view.findViewById(R.id.product_name);
        String name = cursor.getString(cursor.getColumnIndex("name"));
        nameText.setText(name);

        TextView priceText = view.findViewById(R.id.product_price);
        String price = cursor.getString(cursor.getColumnIndex("price"));
        priceText.setText(price);

       quanityText = view.findViewById(R.id.quantity_value);
         quantity = cursor.getString(cursor.getColumnIndex("quantity"));
        quanityText.setText(quantity);

                      quant = Integer.parseInt(quantity);

        ImageView imageView = view.findViewById(R.id.image);

        String image = cursor.getString(cursor.getColumnIndex("image"));

        if (image != null)
           image_uri= Uri.parse(image);
        imageView.setImageURI(image_uri);

        Button increase= view.findViewById(R.id.quantity_increment);


        increase.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                quant++;
            ContentValues values= new ContentValues();
            values.put(ProductEntry.col_quantity,quant);
            String Selection= ProductEntry._ID+"=?";
            String [] selectionArgs= new String[]{String.valueOf(v.getId())};
            UpdateQuant(ProductEntry.content_uri,values,Selection,selectionArgs);

            }
        });
        if (quant!=0)
            quanityText.setText(String.valueOf(quant));
    }
    int UpdateQuant(Uri uri, ContentValues values, String selection, String[] selectionArgs){



        int rowsUpdated= context.getContentResolver().update(uri,values,selection,selectionArgs);
        context.getContentResolver().notifyChange(uri, null);


        return rowsUpdated;
    }

            }






