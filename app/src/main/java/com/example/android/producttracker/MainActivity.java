package com.example.android.producttracker;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;


import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.Toast;

import com.example.android.producttracker.DATA.ProductContract.ProductEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
private ProductAdapter mProductAdapter;
private ListView listView;
private int uriLoader=0;
private final static int mPermossionWrite = 100;
    private final static int mPermossionCamera = 200;
    private final static int mPermossionRead = 300;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},mPermossionWrite);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


         listView= findViewById(R.id.list_view);
        TextView emptyView=findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
        mProductAdapter=new ProductAdapter(this,null);
        listView.setAdapter(mProductAdapter);

        FloatingActionButton addProduct=findViewById(R.id.addProduct);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,Add_Edit_Activity.class);
                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,Add_Edit_Activity.class);
                Uri current_uri= ContentUris.withAppendedId(ProductEntry.content_uri,id);
                intent.setData(current_uri);
                startActivity(intent);
            }
        });
       getSupportLoaderManager().initLoader(uriLoader,null,this);
       setTitle("Products");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==mPermossionWrite) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {
                finish();
                Toast.makeText(this,"grant mf",Toast.LENGTH_LONG).show();
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.order:
                Intent orderIntent= new Intent(MainActivity.this,OrderActivity.class);
                startActivity(orderIntent);
                return true;
            case R.id.delete_all:
           deleteAllDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void deleteAllDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Delete All?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog!=null)
                    dialog.dismiss();
            }
        });
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int deletAll= getContentResolver().delete(ProductEntry.content_uri,null,null);
                if (deletAll>0)
                    Toast.makeText(getApplicationContext(),"All Products were Deleted",Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(),"Deletion failed",Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
    String projection[]={ProductEntry.col_id,ProductEntry.col_image,ProductEntry.col_name,ProductEntry.col_price,ProductEntry.col_quantity};
    Cursor cursor=getContentResolver().query(ProductEntry.content_uri,projection,null,null,null);
    mProductAdapter=new ProductAdapter(this,cursor);
    listView.setAdapter(mProductAdapter);
        return new CursorLoader(this,ProductEntry.content_uri,projection,null,null,null);
    }





    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
    mProductAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
mProductAdapter.swapCursor(null);
    }
}
