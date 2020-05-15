package com.example.administration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administration.Products;
import com.example.administration.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class ViewProducts extends AppCompatActivity {

    private FirebaseFirestore objectFirebaseFirestore;
    private DocumentReference objectDocumentReference;
    private CollectionReference objectCollectionReference;

    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_products);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        objectFirebaseFirestore=FirebaseFirestore.getInstance();

        try {
            objectFirebaseFirestore=FirebaseFirestore.getInstance();
            objectCollectionReference = objectFirebaseFirestore.collection("Products");
        }
        catch (Exception e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
    }
    @Override
    protected void onStart() {
        super.onStart();

        //query set krnege k products retrieve krey database me se iskay lioye 1 product ki model class
        //bnaynge jaha se sari info get krnege

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductsRef, Products.class)
                        .build();



        //bridge between ui and data source

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {

                        //display krnege sara data holder object ko use krtay hoye
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText("PKR = " + model.getPrice() + "");
                        Picasso.get().load(model.getImage()).into(holder.imageView);


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ViewProducts.this, DeleteProduct.class);
                                intent.putExtra("pid", model.getPid());
                                startActivity(intent);
                            }
                        });


                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.produc_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };

        //adapter set krenge
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    public void back(View view)
    {
        startActivity(new Intent(ViewProducts.this, Home.class));
        finish();
    }
}
