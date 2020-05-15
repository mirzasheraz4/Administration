package com.example.administration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class DeleteProduct extends AppCompatActivity {

    private Button DeleteProduct;
    private TextView name, price, description;
    private ImageView imageView;


    private String productID = "";
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //initialize krenge
        setContentView(R.layout.activity_delete_product);


        productID = getIntent().getStringExtra("pid");
        //database refernce bnaynge
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);



        DeleteProduct= findViewById(R.id.DeleteProduct);
        name = findViewById(R.id.product_name_details);
        price = findViewById(R.id.product_price_details);
        description = findViewById(R.id.product_description_details);
        imageView = findViewById(R.id.product_image_details);



        displaySpecificProductInfo();


        DeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                deleteThisProduct();
            }
        });
    }

    private void deleteThisProduct()
    {
        productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Intent intent = new Intent(DeleteProduct.this, Home.class);
                startActivity(intent);
                finish();
                Toast.makeText(DeleteProduct.this, "The Product Is deleted successfully.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displaySpecificProductInfo()
    {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String pName = dataSnapshot.child("pname").getValue().toString();
                    String pPrice = dataSnapshot.child("price").getValue().toString();
                    String pDescription = dataSnapshot.child("description").getValue().toString();
                    String pImage = dataSnapshot.child("image").getValue().toString();


                    name.setText(pName);
                    price.setText(pPrice);
                    description.setText(pDescription);
                    Picasso.get().load(pImage).into(imageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void back(View view)
    {
        startActivity(new Intent(DeleteProduct.this, Home.class));
        finish();
    }
}

