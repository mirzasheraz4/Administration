package com.example.administration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {
    private FirebaseAuth objectFirebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        objectFirebaseAuth= FirebaseAuth.getInstance();
    }
    public void Logout(View view)
    {
        objectFirebaseAuth.signOut();
        Toast.makeText(Home.this, "Sign Out Successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(Home.this, MainActivity.class));
        finish();

    }
    public void ADD(View view)
    {
        startActivity(new Intent(Home.this, AddProducts.class));
        finish();

    }
    public void ViewProduct(View view)
    {
        startActivity(new Intent(Home.this, ViewProducts.class));
        finish();

    }
    public void Delete(View view)
    {
        startActivity(new Intent(Home.this, ViewProducts.class));
        finish();
    }
}
