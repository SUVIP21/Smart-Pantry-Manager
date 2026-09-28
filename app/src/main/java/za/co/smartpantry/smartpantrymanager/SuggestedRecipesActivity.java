package za.co.smartpantry.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SuggestedRecipesActivity extends AppCompatActivity {
    private RecyclerView recyclerViewRecipes;
    private TextView textNoRecipes;
    private DatabaseHelper databaseHelper;
    private RecipeAdapter recipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_suggested_recipes);

        databaseHelper = new DatabaseHelper(this);

        recyclerViewRecipes = findViewById(R.id.recyclerViewRecipes);

        textNoRecipes = findViewById(R.id.textNoRecipes);

        recyclerViewRecipes.setLayoutManager(new LinearLayoutManager(this));

        setupNavigation();
        loadSuggestedRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {
        ArrayList<PantryItem> pantryItems = databaseHelper.getAllPantryItems();
        ArrayList<Recipe> allRecipes = databaseHelper.getAllRecipes();
        ArrayList<Recipe> matchingRecipes = RecipeMatcher.getMatchingRecipes(allRecipes, pantryItems, databaseHelper);

        if (matchingRecipes.isEmpty()) {
            textNoRecipes.setVisibility(TextView.VISIBLE);
        } else {
            textNoRecipes.setVisibility(TextView.GONE);
        }

        if (recipeAdapter == null) {
            recipeAdapter = new RecipeAdapter(matchingRecipes, recipe -> openRecipeDetail(recipe));
            recyclerViewRecipes.setAdapter(recipeAdapter);
        } else {
            recipeAdapter.updateData(matchingRecipes);
        }
    }

    private void openRecipeDetail(Recipe recipe) {
        Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
        intent.putExtra("recipeId", recipe.getId());
        startActivity(intent);
    }

    private void setupNavigation() {
        Button buttonPantry = findViewById(R.id.buttonPantry);
        Button buttonRecipes = findViewById(R.id.buttonRecipes);
        Button buttonSettings = findViewById(R.id.buttonSettings);

        buttonPantry.setOnClickListener(v -> {
            Intent intent = new Intent(SuggestedRecipesActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        buttonRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(SuggestedRecipesActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }
}
