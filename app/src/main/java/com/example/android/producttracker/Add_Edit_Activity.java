package com.example.android.producttracker;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.Toast;

import com.example.android.producttracker.DATA.ProductContract.ProductEntry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Add_Edit_Activity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ImageView image;
    private EditText nameEditText;
    private EditText priceEditText;
    private EditText quantityEditText;
    private Button increment;
    private Button decrement;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int GALLERY_REQUEST = 2;
    private Uri Gallery_imageUri;
    private Uri mCurrentUri;
    private boolean hasChanged = false;
    private static final int LoaderId = 0;
    private String filePath;
    private Uri CameraimageUri;
   private int quantity;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            hasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_activity);
        Intent intent = getIntent();
        mCurrentUri = intent.getData();
        image = findViewById(R.id.editImage);
        nameEditText = findViewById(R.id.edit_name);
        priceEditText = findViewById(R.id.editPrice);
        quantityEditText = findViewById(R.id.edit_quantity);
        increment = findViewById(R.id.increment);
        decrement = findViewById(R.id.decrement);

        nameEditText.setOnTouchListener(onTouchListener);
        priceEditText.setOnTouchListener(onTouchListener);
        quantityEditText.setOnTouchListener(onTouchListener);
        image.setOnTouchListener(onTouchListener);


            increment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    increment();
                }
            });
            decrement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    decrement();
                }
            });

        if (mCurrentUri == null) {
            setTitle("Add a product");
        } else {

            setTitle("Edit product");
            getSupportLoaderManager().initLoader(LoaderId, null, this);
        }
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoDialog();
            }
        });

    }



    private void increment(){
        try {
            quantity = Integer.parseInt(quantityEditText.getText().toString().trim());
        } catch (NumberFormatException n) {
            n.printStackTrace();
        }
        quantity++;
        quantityEditText.setText(""+quantity);
    }
    private void decrement(){
        try {
            quantity = Integer.parseInt(quantityEditText.getText().toString().trim());
        } catch (NumberFormatException n) {
            n.printStackTrace();
        }
        if (quantity>0) {
            quantity--;
            quantityEditText.setText(""+quantity);
        }
        else
            Toast.makeText(this,"Quantity must be larger than 0",Toast.LENGTH_LONG).show();
    }

    private void saveProduct() {


        String name = nameEditText.getText().toString().trim();
        int price = Integer.parseInt(priceEditText.getText().toString().trim());

        ContentValues values = new ContentValues();
        if (Gallery_imageUri != null)
            values.put(ProductEntry.col_image, Gallery_imageUri.toString());

        values.put(ProductEntry.col_name, name);
        values.put(ProductEntry.col_price, price);
        values.put(ProductEntry.col_quantity, quantity);
        if (CameraimageUri != null)
            values.put(ProductEntry.col_image, CameraimageUri.toString());
        if (mCurrentUri == null) {
            Uri insertionUri = getContentResolver().insert(ProductEntry.content_uri, values);
            if (insertionUri == null)
                Toast.makeText(getApplicationContext(), "Insertion failed", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "Product saved", Toast.LENGTH_SHORT).show();
        } else
            getContentResolver().update(mCurrentUri, values, null, null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_edit_menu, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentUri == null) {
            MenuItem deleteItem = menu.findItem(R.id.delete_one);
            deleteItem.setVisible(false);

        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save:

                String name = nameEditText.getText().toString();
                String price = priceEditText.getText().toString();
                String quantity = quantityEditText.getText().toString();


                if (TextUtils.isEmpty(name))
                    Toast.makeText(getApplicationContext(), "Please fill in a name", Toast.LENGTH_SHORT).show();

                else if (TextUtils.isEmpty(price))
                    Toast.makeText(getApplicationContext(), "Please fill in a price", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(quantity))
                    Toast.makeText(getApplicationContext(), "Please fill in quantity", Toast.LENGTH_SHORT).show();
                else if (Gallery_imageUri==null&&CameraimageUri==null)
                    Toast.makeText(getApplicationContext(),"Please Insert a photo",Toast.LENGTH_SHORT).show();
                else {
                    saveProduct();
                    finish();
                }
                return true;

            case R.id.delete_one:
             deleteProductDialog();
             return true;

            case android.R.id.home:
                if(hasChanged==true) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                backConfirmationeDialog();

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(hasChanged==false) {
            super.onBackPressed();
            return;
        }
        backConfirmationeDialog();
    }
    //confirm going back without saving changes
    private void backConfirmationeDialog(){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage("Go back without saving changes?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog!=null)
                    dialog.dismiss();
            }
        });
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NavUtils.navigateUpFromSameTask(Add_Edit_Activity.this);
            }
        });
        AlertDialog alertDialog= builder.create();

        alertDialog.show();
    }
    private void PhotoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("");
        builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    captureImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null)
                    dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                OpenGallery();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void captureImage() throws IOException {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {

            //Toast.makeText(getApplicationContext(),"sad",Toast.LENGTH_SHORT).show();

            File photoFile = createImageFile();
            if (photoFile != null) {

                filePath = photoFile.getAbsolutePath();
                CameraimageUri = FileProvider.getUriForFile(this, "com.ProdcutTracker.android.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, CameraimageUri);

                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    Bitmap picture = BitmapFactory.decodeFile(filePath);
                    image.setImageBitmap(picture);
                }
                break;
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    //data.getData return the content URI for the selected Image
                    Gallery_imageUri = data.getData();

                    InputStream inputStream= null;
                    try {
                        inputStream = getContentResolver().openInputStream(Gallery_imageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                  image.setImageBitmap(bitmap);

                }
                break;
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        filePath = image.getAbsolutePath();
        return image;
    }


    //to pick from gallery
    private void OpenGallery() {
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);

            //where to look for the data?
            File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
           String picGallerPath = f.getPath();

            //get Uri representation
            Uri uriForFile = Uri.parse(picGallerPath);

            intent.setDataAndType(uriForFile, "image/*");
            startActivityForResult(intent, GALLERY_REQUEST);

    }

private void deleteOne(){
    int rowdeleted=0;
        if (mCurrentUri!=null) {
            rowdeleted = getContentResolver().delete(mCurrentUri, null, null);

            if (rowdeleted == 0)
                Toast.makeText(getApplication(), "Deletion Failed", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), "Product Deleted", Toast.LENGTH_SHORT).show();

        }
        finish();
    }

    private void deleteProductDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Delete Product?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteOne();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog!=null)
                    dialog.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {ProductEntry.col_name, ProductEntry.col_price, ProductEntry.col_quantity, ProductEntry.col_image};

        return new CursorLoader(this, mCurrentUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            nameEditText.setText(data.getString(data.getColumnIndex("name")));
            priceEditText.setText(data.getString(data.getColumnIndex("price")));
            quantityEditText.setText(data.getString(data.getColumnIndex("quantity")));
            //image.setImageResource(data.getInt(data.getColumnIndex("image")));
            image.setImageURI(Uri.parse(data.getString(data.getColumnIndex("image"))));
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
         image.setImageURI(null);
    }
}
