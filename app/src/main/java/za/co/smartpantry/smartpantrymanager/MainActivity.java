package za.co.smartpantry.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewPantry;
    private Button buttonAddIngredient;
    private DatabaseHelper databaseHelper;
    private PantryAdapter pantryAdapter;
    private ArrayList<PantryItem> pantryItems;
    private TextView textEmptyPantry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        recyclerViewPantry = findViewById(R.id.recyclerViewPantry);

        buttonAddIngredient = findViewById(R.id.buttonAddIngredient);

        recyclerViewPantry.setLayoutManager(new LinearLayoutManager(this));

        buttonAddIngredient.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddEditIngredientActivity.class
            );
            startActivity(intent);
        });

        loadPantryItems();

        textEmptyPantry = findViewById(R.id.textEmptyPantry);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
    }

    private void loadPantryItems() {
        pantryItems = databaseHelper.getAllPantryItems();
        if (pantryAdapter == null) {
            pantryAdapter = new PantryAdapter(
                    pantryItems,
                    new PantryAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(PantryItem item) {
                            openEditScreen(item);
                        }

                        @Override
                        public void onItemLongClick(PantryItem item) {
                            showDeleteDialog(item);
                        }
                    }
            );

            recyclerViewPantry.setAdapter(pantryAdapter);
        } else {
            pantryAdapter.updateData(pantryItems);
        }

        if (pantryItems.isEmpty()) {
            textEmptyPantry.setVisibility(TextView.VISIBLE);
        } else {
            textEmptyPantry.setVisibility(TextView.GONE);
        }
    }

    private void openEditScreen(PantryItem item) {
        Intent intent = new Intent(
                MainActivity.this,
                AddEditIngredientActivity.class
        );

        intent.putExtra("id", item.getId());
        intent.putExtra("name", item.getName());
        intent.putExtra("quantity", item.getQuantity());
        intent.putExtra("unit", item.getUnit());
        intent.putExtra("expiryDate", item.getExpiryDate());

        startActivity(intent);
    }

    private void showDeleteDialog(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage("Are you sure you want to delete " + item.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteItem(item))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteItem(PantryItem item) {
        int result = databaseHelper.deletePantryItem(item.getId());
        if (result > 0) {
            Toast.makeText(
                    this,
                    "Ingredient Deleted",
                    Toast.LENGTH_SHORT
            ).show();

            loadPantryItems();
        } else {
            Toast.makeText(
                    this,
                    "Could not delete ingredient",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}