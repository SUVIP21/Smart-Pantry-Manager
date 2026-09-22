package za.co.smartpantry.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditIngredientActivity extends AppCompatActivity{
    private EditText editName;
    private EditText editQuantity;
    private EditText editUnit;
    private EditText editExpiryDate;

    private DatabaseHelper databaseHelper;

    private int ingredientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_edit_ingredient);

        databaseHelper = new DatabaseHelper(this);

        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        editUnit = findViewById(R.id.editUnit);
        editExpiryDate = findViewById(R.id.editExpiryDate);

        Button buttonSave = findViewById(R.id.buttonSave);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("id")) {
            ingredientId = intent.getIntExtra("id", -1);
            String name = intent.getStringExtra("name");
            double quantity = intent.getDoubleExtra("quantity", 0);
            String unit = intent.getStringExtra("unit");
            String expiryDate = intent.getStringExtra("expiryDate");

            editName.setText(name);
            editQuantity.setText(String.valueOf(quantity));
            editUnit.setText(unit);

            if (expiryDate != null) {
                editExpiryDate.setText(expiryDate);
            }
        }

        buttonSave.setOnClickListener(v -> saveIngredient());
    }

    private void saveIngredient() {
        String name = editName.getText().toString().trim();
        String quantityText = editQuantity.getText().toString().trim();
        String unit = editUnit.getText().toString().trim();
        String expiryDate = editExpiryDate.getText().toString().trim();

        //Validate ingredient name
        if (TextUtils.isEmpty(name)) {
            editName.setError("Ingredient name is required");
            editName.requestFocus();
            return;
        }

        //Validate quantity
        if (TextUtils.isEmpty(quantityText)) {
            editQuantity.setError("Quantity is required");
            editQuantity.requestFocus();
            return;
        }

        double quantity;

        try{
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            editQuantity.setError("Enter a valid number");
            editQuantity.requestFocus();
            return;
        }

        if (quantity <= 0) {
            editQuantity.setError("Quantity must be greater than 0");
            editQuantity.requestFocus();
            return;
        }

        //Validate Unit
        if (TextUtils.isEmpty(unit)) {
            editUnit.setError("Unit is required");
            editUnit.requestFocus();
            return;
        }

        PantryItem item = new PantryItem(
                ingredientId,
                name,
                quantity,
                unit,
                expiryDate
        );

        if (ingredientId == -1) {
            long result = databaseHelper.addPantryItem(item);

            if (result != -1) {
                Toast.makeText(
                        this,
                        "Ingredient added successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
            } else {
                Toast.makeText(
                        this,
                        "Failed to add ingredient",
                        Toast.LENGTH_SHORT
                ).show();
            }
        } else {
            int result = databaseHelper.updatePantryItem(item);
            if (result > 0) {
                Toast.makeText(
                        this,
                        "Ingredient updated successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
            } else{
                Toast.makeText(
                        this,
                        "Failed to update ingredient",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
}
