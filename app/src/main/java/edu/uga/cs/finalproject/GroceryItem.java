package edu.uga.cs.finalproject;
public class GroceryItem {
    private String key;
    private String name;
    private boolean purchased; // Add a boolean field for purchased status

    public GroceryItem() {
        // Default constructor required for Firebase
    }

    public GroceryItem(String name, String key) {
        this.name = name;
        this.key = key;
        this.purchased = false; // Initialize purchased status as false
    }

    // Getter and Setter for purchased
    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    // Other getters and setters for name and key
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
