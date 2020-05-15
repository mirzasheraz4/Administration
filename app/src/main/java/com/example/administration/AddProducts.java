package com.example.administration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddProducts extends AppCompatActivity {


    private FirebaseFirestore objectFirebaseFirestore;
    private DocumentReference objectDocumentReference;
    private CollectionReference objectCollectionReference;
    private static final String COLLECTION_NAME = "Products";

    private String Description, Price, Pname, saveCurrentDate, saveCurrentTime;
    private Button AddNewProductButton;
    private Button back;
    private ImageView InputProductImage;
    private EditText InputProductName, InputProductDescription, InputProductPrice;
    private static final int Gallery = 1;
    private Uri ImageUri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);


        objectFirebaseFirestore = FirebaseFirestore.getInstance();
        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");


        AddNewProductButton = findViewById(R.id.AddBTN);
        InputProductImage = findViewById(R.id.productIV);
        InputProductName = findViewById(R.id.ProductNameET);
        InputProductDescription = findViewById(R.id.ProductDesET);
        InputProductPrice = findViewById(R.id.ProductPriceET);
        loadingBar = new ProgressDialog(this);


        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });


        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });
        try {
            objectFirebaseFirestore = FirebaseFirestore.getInstance();
            objectCollectionReference = objectFirebaseFirestore.collection(COLLECTION_NAME);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
    }


    private void OpenGallery() {
        Intent galleryIntent = new Intent();

        //action get context basically diaplay krta h numbers pics allow krta hai user ko k wo browse krlay or baad me doabra
        //usey activity me ajye
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        //ye allow krta hai user ko k kisi bhi type ki iamge sleect krle
        galleryIntent.setType("image/*");

        //startactivity se hum basically result get krtay hai dosri activity me se we send info from 1 activity to other.
        startActivityForResult(galleryIntent, Gallery);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            InputProductImage.setImageURI(ImageUri);
        }
    }


    private void ValidateProductData() {
        Description = InputProductDescription.getText().toString();
        Price = InputProductPrice.getText().toString();
        Pname = InputProductName.getText().toString();

        if (ImageUri == null) {
            Toast.makeText(this, "Product image is mandatory.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Description)) {
            Toast.makeText(this, "Please write product description.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Price)) {
            Toast.makeText(this, "Please write product Price.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Pname)) {
            Toast.makeText(this, "Please write product name.", Toast.LENGTH_SHORT).show();
        } else {
            StoreProductInformation();
        }
    }


    private void StoreProductInformation() {

        loadingBar.setTitle("Add New Product");
        loadingBar.setMessage("Dear Admin, please wait while we are adding the new product.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = ProductImagesRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AddProducts.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddProducts.this, "Product Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(AddProducts.this, "Got the Product image Url Successfully...", Toast.LENGTH_SHORT).show();

                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }


    private void SaveProductInfoToDatabase() {
        {
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", productRandomKey);
            productMap.put("date", saveCurrentDate);
            productMap.put("time", saveCurrentTime);
            productMap.put("description", Description);
            productMap.put("image", downloadImageUrl);
            productMap.put("price", Price);
            productMap.put("pname", Pname);

            ProductsRef.child(productRandomKey).updateChildren(productMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(AddProducts.this, Home.class);
                                startActivity(intent);

                                loadingBar.dismiss();
                                Toast.makeText(AddProducts.this, "Food is added successfully..", Toast.LENGTH_SHORT).show();
                            } else {
                                loadingBar.dismiss();
                                String message = task.getException().toString();
                                Toast.makeText(AddProducts.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    public void back(View view)
    {
        startActivity(new Intent(AddProducts.this, Home.class));
        finish();
    }
}