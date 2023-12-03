package edu.uga.cs.finalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroceryItemAdapter extends ArrayAdapter<GroceryItem> {

    private DatabaseReference databaseReference;
    private ArrayList<GroceryItem> itemList;

    public GroceryItemAdapter(Context context, ArrayList<GroceryItem> itemList) {
        super(context, 0, itemList);
        databaseReference = FirebaseDatabase.getInstance().getReference("grocery_items");
        this.itemList = itemList;
    }
    public void fetchPurchasedItems() {
        Query query = databaseReference.orderByChild("purchased").equalTo(true);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<GroceryItem> purchasedItems = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroceryItem item = snapshot.getValue(GroceryItem.class);
                    if (item != null) {
                        purchasedItems.add(item);
                        Log.d("PurchasedItem", "Item: " + item.getName() + ", Purchased: " + item.isPurchased());
                    }
                }
                // Handle the purchased items (e.g., update a list in your UI)
                // For example: updatePurchasedItemsList(purchasedItems);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch purchased items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final GroceryItem item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView itemNameTextView = convertView.findViewById(R.id.itemNameTextView);
        Button deleteButton = convertView.findViewById(R.id.deleteButton);
        Button editButton = convertView.findViewById(R.id.editButton);
        CheckBox purchasedCheckBox = convertView.findViewById(R.id.purchasedCheckBox);

        if (purchasedCheckBox != null) {
            // Populate the data into the template view using the data object
            itemNameTextView.setText(item.getName());

            // Set click listener for the edit button
            editButton.setOnClickListener(v -> showEditItemDialog(item));

            // Set click listener for the delete button
            deleteButton.setOnClickListener(v -> removeItem(item));

            // Set a click listener for the checkbox
            purchasedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    markItemAsPurchasedInFirebase(item);
                }
            });
        } else {
            // Handle the case where purchasedCheckBox is null
            // You could log an error or take appropriate action
        }

        return convertView;
    }

    private void markItemAsPurchasedInFirebase(GroceryItem item) {
        // Update the "purchased" status of the item in Firebase
        // You can add a field like "purchased" to your GroceryItem class and set it to true
        item.setPurchased(true); // Assuming you have a 'setPurchased' method in your GroceryItem class
        // Then, update the item in Firebase using databaseReference.child(item.getKey()).setValue(item)
        databaseReference.child(item.getKey()).setValue(item)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Item marked as purchased", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to mark item as purchased", Toast.LENGTH_SHORT).show();
                    // If the update fails, uncheck the checkbox to keep the UI in sync
                    item.setPurchased(false);
                    notifyDataSetChanged();
                });
        // Make sure to adjust your GroceryItem class accordingly.
    }

    private void removeItem(GroceryItem item) {
        databaseReference.child(item.getKey()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Find the correct position based on the item's key
                    int positionToRemove = -1;
                    for (int i = 0; i < itemList.size(); i++) {
                        if (itemList.get(i).getKey().equals(item.getKey())) {
                            positionToRemove = i;
                            break;
                        }
                    }

                    // Remove the item if found
                    if (positionToRemove != -1) {
                        itemList.remove(positionToRemove);
                        notifyDataSetChanged();
                        Toast.makeText(getContext(), "Item removed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to remove item", Toast.LENGTH_SHORT).show());
    }

    private void showEditItemDialog(GroceryItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Item");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(item.getName()); // Pre-fill with current item name
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newName = input.getText().toString();
            if (!newName.isEmpty()) {
                updateItem(item, newName); // Call updateItem with the new name
            } else {
                Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateItem(GroceryItem item, String newName) {
        // Update the name of the item
        item.setName(newName);

        // Update the whole object in Firebase
        databaseReference.child(item.getKey()).setValue(item)
                .addOnSuccessListener(aVoid -> {
                    notifyDataSetChanged();
                    Toast.makeText(getContext(), "Item updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update item", Toast.LENGTH_SHORT).show());
    }

    @Override
    public GroceryItem getItem(int position) {
        return super.getItem(position);
    }
}