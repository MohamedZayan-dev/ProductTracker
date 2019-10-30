package com.example.android.producttracker;

import android.content.Context;
import android.database.Cursor;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductAdapter extends CursorAdapter {

    Uri image_uri;

    public ProductAdapter(Context context, Cursor c) {
        super(context, c);

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

       TextView quanityText = view.findViewById(R.id.quantity_value);
       String quantity = cursor.getString(cursor.getColumnIndex("quantity"));
        quanityText.setText(quantity);

        ImageView imageView = view.findViewById(R.id.image);

        String image = cursor.getString(cursor.getColumnIndex("image"));

        if (image != null)
            image_uri = Uri.parse(image);
        imageView.setImageURI(image_uri);
    }

            }






