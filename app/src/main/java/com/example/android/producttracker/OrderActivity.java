package com.example.android.producttracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class OrderActivity extends AppCompatActivity
{
   private Button orderButton;
   private EditText ProductOrderName;
   private EditText ProductOrderQuantity;
   private EditText ShopOrderName;
   private EditText ShopOderAddress;
   private EditText SupplierOrderName;
   private EditText SupplierOrderEmail;


    private String productName,productOrderQuant, ShopName, ShopAddress, supplierName, supplierEmail;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity);
        ProductOrderName=findViewById(R.id.et_order_product_name);
        ProductOrderQuantity=findViewById(R.id.et_order_product_quantity);
        ShopOrderName=findViewById(R.id.et_order_shop_name);
        ShopOderAddress=findViewById(R.id.et_order_shop_address);
        SupplierOrderName=findViewById(R.id.et_order_supplier_name);
        SupplierOrderEmail=findViewById(R.id.et_order_supplier_email);
        orderButton= findViewById(R.id.orderButton);

        orderButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        sendEmail();
    }
});
    }


    private void sendEmail(){


        productName = ProductOrderName.getText().toString().trim();
        productOrderQuant = ProductOrderQuantity.getText().toString().trim();
        ShopName  = ShopOrderName.getText().toString().trim();
        ShopAddress  = ShopOderAddress.getText().toString().trim();
        supplierName  = SupplierOrderName.getText().toString().trim();
        supplierEmail  = SupplierOrderEmail.getText().toString().trim();

        String address = supplierEmail;
        String subject =  ShopName +" Requests " + productOrderQuant +" " +productName;
        String body = "Hello Mr "+supplierName+ " Hope You are having a wonderful day\n"
                +" This is a request of " + productName +" for my shop in the following address: " + ShopAddress +"\n" +ShopName +" Requests " +productOrderQuant +
                " " +productName + "\n\n\n\n\n\n    Regards:\n  "+ShopName;
        Intent intent= new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL,new String[]{ address});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT,body);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


}

