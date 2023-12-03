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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText itemNameEditText;
    private ListView groceryListView;
    private GroceryItemAdapter adapter;  // Changed this line
    private ArrayList<GroceryItem> groceryList;
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

        groceryList = new ArrayList<>();
        adapter = new GroceryItemAdapter(this, groceryList);  // Make sure this matches your custom adapter
        groceryListView.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToFirebaseAndListView();
            }
        });

        fetchItemsFromFirebase();
    }
    private void fetchItemsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groceryList.clear();  // Clear the existing list
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroceryItem item = snapshot.getValue(GroceryItem.class);
                    if (item != null) {
                        groceryList.add(item);  // Add item to the list
                    }
                }
                adapter.notifyDataSetChanged();  // Notify the adapter of the data change
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to fetch items", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void addItemToFirebaseAndListView() {
        String itemName = itemNameEditText.getText().toString().trim();

        if (!itemName.isEmpty()) {
            // Create a new GroceryItem object
            String key = databaseReference.push().getKey();
            GroceryItem newItem = new GroceryItem(itemName, key);

            // Add the whole object to Firebase
            databaseReference.child(key).setValue(newItem)
                    .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Item added to Firebase", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Failed to add item to Firebase", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Please enter an item name", Toast.LENGTH_SHORT).show();
        }
    }
}

