package edu.uga.cs.finalproject;

import android.os.Bundle;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText itemNameEditText;
    private ListView groceryListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> groceryList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("grocery_items");

        itemNameEditText = findViewById(R.id.itemNameEditText);
        Button addButton = findViewById(R.id.addButton);
        groceryListView = findViewById(R.id.groceryListView);

        // Initialize the list and adapter
        groceryList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, groceryList);
        groceryListView.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToFirebaseAndListView();
            }
        });
    }

    private void addItemToFirebaseAndListView() {
        String itemName = itemNameEditText.getText().toString().trim();

        if (!itemName.isEmpty()) {
            // Add item to Firebase
            String key = databaseReference.push().getKey();
            databaseReference.child(key).setValue(itemName)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MainActivity.this, "Item added to Firebase", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Failed to add item to Firebase", Toast.LENGTH_SHORT).show();
                        }
                    });

            // Add item to ListView
            groceryList.add(itemName);
            adapter.notifyDataSetChanged();

            // Clear EditText
            itemNameEditText.setText("");
        } else {
            Toast.makeText(this, "Please enter an item name", Toast.LENGTH_SHORT).show();
        }
    }
}
