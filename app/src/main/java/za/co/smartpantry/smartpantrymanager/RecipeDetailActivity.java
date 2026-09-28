package za.co.smartpantry.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private TextView textRecipeDetailName;
    private TextView textRecipeIngredients;
    private TextView textRecipeMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_detail);

        databaseHelper = new DatabaseHelper(this);

        textRecipeDetailName = findViewById(R.id.textRecipeDetailName);
        textRecipeIngredients = findViewById(R.id.textRecipeIngredients);
        textRecipeMethod = findViewById(R.id.textRecipeMethod);

        Button buttonBack = findViewById(R.id.buttonBackToRecipes);

        int recipeId = getIntent().getIntExtra("recipeId", -1);

        if (recipeId == -1) {
            Toast.makeText(
                    this,
                    "Recipe could not be found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        loadRecipe(recipeId);
        buttonBack.setOnClickListener(v -> finish());
    }

    private void loadRecipe(int recipeId) {
        Recipe recipe = databaseHelper.getRecipeById(recipeId);

        if (recipe == null) {
            Toast.makeText(
                    this,
                    "Recipe could not be found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        textRecipeDetailName.setText(recipe.getName());
        textRecipeMethod.setText(recipe.getMethod());

        ArrayList<RecipeIngredient> ingredients = databaseHelper.getRecipeIngredients(recipeId);

        StringBuilder ingredientText = new StringBuilder();

        for (RecipeIngredient ingredient : ingredients) {
            ingredientText
                    .append(". ")
                    .append(ingredient.getIngredientName())
                    .append(" - ")
                    .append(ingredient.getRequiredQuantity())
                    .append(" ")
                    .append(ingredient.getUnit())
                    .append("\n");
        }

        textRecipeIngredients.setText(ingredientText.toString());
    }
}
