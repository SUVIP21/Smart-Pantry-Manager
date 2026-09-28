package za.co.smartpantry.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    //Database Info
    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 2;

    //Pantry Table
    public static final String TABLE_PANTRY = "pantry";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_EXPIRY_DATE = "expiry_date";

    //Recipe Table
    public static final String TABLE_RECIPES = "recipes";
    public static final String COLUMN_RECIPE_ID = "id";
    public static final String COLUMN_RECIPE_NAME = "name";
    public static final String COLUMN_RECIPE_METHOD = "method";

    //Recipe Ingredient Table
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COLUMN_RECIPE_INGREDIENT_ID = "id";
    public static final String COLUMN_RECIPE_ID_FK = "recipe_id";
    public static final String COLUMN_INGREDIENT_NAME = "ingredient_name";
    public static final String COLUMN_REQUIRED_QUANTITY = "required_quantity";
    public static final String COLUMN_RECIPE_UNIT = "unit";

    //SQL to create pantry table
    private static final String CREATE_PANTRY_TABLE = "CREATE TABLE " + TABLE_PANTRY + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT NOT NULL, " +
            COLUMN_QUANTITY + " REAL NOT NULL, " +
            COLUMN_UNIT + " TEXT NOT NULL, " +
            COLUMN_EXPIRY_DATE + " TEXT" +
            ")";

    //Create Recipes Table
    private static final String CREATE_RECIPES_TABLE = "CREATE TABLE " + TABLE_RECIPES + " (" +
            COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
            COLUMN_RECIPE_METHOD + " TEXT NOT NULL" +
            ")";

    //Create Recipe Ingredients Table
    private static final String CREATE_RECIPE_INGREDIENTS_TABLE = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
            COLUMN_RECIPE_INGREDIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_RECIPE_ID_FK + " INTEGER NOT NULL, " +
            COLUMN_INGREDIENT_NAME + " TEXT NOT NULL, " +
            COLUMN_REQUIRED_QUANTITY + " REAL NOT NULL, " +
            COLUMN_RECIPE_UNIT + " TEXT NOT NULL, " +
            "FOREIGN KEY (" + COLUMN_RECIPE_ID_FK +
            ") REFERENCES " + TABLE_RECIPES +
            "(" + COLUMN_RECIPE_ID +
            ")" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PANTRY_TABLE);
        db.execSQL(CREATE_RECIPES_TABLE);
        db.execSQL(CREATE_RECIPE_INGREDIENTS_TABLE);
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing tables during development upgrades
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);

        // Re-create full schema
        onCreate(db);
    }

    //CREATE
    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        values.put(COLUMN_UNIT, item.getUnit());
        values.put(COLUMN_EXPIRY_DATE, item.getExpiryDate());

        long result = db.insert(TABLE_PANTRY, null, values);
        db.close();

        return result;
    }

    //READ-get all pantry items
    public ArrayList<PantryItem> getAllPantryItems() {
        ArrayList<PantryItem> pantryItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,
                COLUMN_ID + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNIT));
                String expiryDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPIRY_DATE));

                PantryItem item = new PantryItem(id, name, quantity, unit, expiryDate);
                pantryItems.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return pantryItems;
    }

    //UPDATE
    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        values.put(COLUMN_UNIT, item.getUnit());
        values.put(COLUMN_EXPIRY_DATE, item.getExpiryDate());

        int result = db.update(
                TABLE_PANTRY,
                values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(item.getId())}
        );
        db.close();
        return result;
    }

    //DELETE
    public int deletePantryItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(
                TABLE_PANTRY,
                COLUMN_ID + " =?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return result;
    }

    //RECIPES-GET ALL
    public ArrayList<Recipe> getAllRecipes() {
        ArrayList<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,
                COLUMN_RECIPE_NAME + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_NAME));
                String method = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_METHOD));
                recipes.add(new Recipe(id, name, method));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return recipes;
    }

    //Get recipe by ID
    public Recipe getRecipeById(int recipeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_RECIPES,
                null,
                COLUMN_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                null
        );
        Recipe recipe = null;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_NAME));
            String method = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_METHOD));
            recipe = new Recipe(id, name, method);
        }

        cursor.close();
        db.close();
        return recipe;
    }

    //GET INGREDIENTS FOR A RECIPE
    public ArrayList<RecipeIngredient> getRecipeIngredients(int recipeId) {
        ArrayList<RecipeIngredient> ingredients = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_RECIPE_INGREDIENTS,
                null,
                COLUMN_RECIPE_ID_FK + " = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                COLUMN_RECIPE_INGREDIENT_ID + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_INGREDIENT_ID));
                int recipeIdValue = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_ID_FK));
                String ingredientName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENT_NAME));
                double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_REQUIRED_QUANTITY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_UNIT));

                ingredients.add(
                        new RecipeIngredient(
                                id,
                                recipeIdValue,
                                ingredientName,
                                quantity,
                                unit
                        )
                );
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return ingredients;
    }

    //SEED RECIPES
    private void seedRecipes(SQLiteDatabase db) {
        //1) Tomato Pasta
        int recipeId = insertRecipe(
                db,
                "Tomato Pasta",
                "1. Boil the pasta until tender.\n" +
                        "2. Cook the tomatoes and onions in a pan.\n" +
                        "3. Add the cooked pasta.\n" +
                        "4. Mix well and serve."
        );

        insertRecipeIngredient(db, recipeId, "pasta", 200, "g");
        insertRecipeIngredient(db, recipeId, "tomato", 200, "g");
        insertRecipeIngredient(db, recipeId, "onion", 1, "piece");

        //2) Chicken Pasta
        recipeId = insertRecipe(
                db,
                "Chicken Pasta",
                "1. Boil the pasta.\n" +
                        "2. Cook the chicken thoroughly.\n" +
                        "3. Add onion and mix.\n" +
                        "4. Combine with pasta and serve."
        );

        insertRecipeIngredient(db, recipeId, "pasta", 200, "g");
        insertRecipeIngredient(db, recipeId, "chicken", 250, "g");
        insertRecipeIngredient(db, recipeId, "onion", 1, "piece");

        //3) Chicken Rice
        recipeId = insertRecipe(
                db,
                "Chicken Rice",
                "1. Cook the rice.\n" +
                        "2. Cook the chicken thoroughly.\n" +
                        "3. Add onion and mix.\n" +
                        "4. Serve with rice."
        );

        insertRecipeIngredient(db, recipeId, "rice", 200, "g");
        insertRecipeIngredient(db, recipeId, "chicken", 250, "g");
        insertRecipeIngredient(db, recipeId, "onion", 1, "piece");

        //4) Egg Fried Rice
        recipeId = insertRecipe(
                db,
                "Egg Fried Rice",
                "1. Cook the rice.\n" +
                        "2. Scramble the eggs.\n" +
                        "3. Add rice and vegetables.\n" +
                        "4. Stir-fry and serve."
        );

        insertRecipeIngredient(db, recipeId, "rice", 200, "g");
        insertRecipeIngredient(db, recipeId, "egg", 2, "piece");
        insertRecipeIngredient(db, recipeId, "carrot", 100, "g");

        //5) Vegetable Rice
        recipeId = insertRecipe(
                db,
                "Vegetable Rice",
                "1. Cook the rice.\n" +
                        "2. Chop the vegetables.\n" +
                        "3. Stir-fry the vegetables.\n" +
                        "4. Mix with rice and serve."
        );

        insertRecipeIngredient(db, recipeId, "rice", 200, "g");
        insertRecipeIngredient(db, recipeId, "carrot", 100, "g");
        insertRecipeIngredient(db, recipeId, "peas", 100, "g");

        //6) Chicken Sandwich
        recipeId = insertRecipe(
                db,
                "Chicken Sandwich",
                "1. Cook the chicken.\n" +
                        "2. Slice the tomato and lettuce.\n" +
                        "3. Place ingredients between bread.\n" +
                        "4. Serve."
        );

        insertRecipeIngredient(db, recipeId, "bread", 2, "piece");
        insertRecipeIngredient(db, recipeId, "chicken", 150, "g");
        insertRecipeIngredient(db, recipeId, "tomato", 100, "g");
        insertRecipeIngredient(db, recipeId, "lettuce", 50, "g");

        //7) Tuna Sandwich
        recipeId = insertRecipe(
                db,
                "Tuna Sandwich",
                "1. Drain the tuna.\n" +
                        "2. Prepare the tomato and lettuce.\n" +
                        "3. Place ingredients between bread.\n" +
                        "4. Serve."
        );

        insertRecipeIngredient(db, recipeId, "bread", 2, "piece");
        insertRecipeIngredient(db, recipeId, "tuna", 150, "g");
        insertRecipeIngredient(db, recipeId, "tomato", 100, "g");
        insertRecipeIngredient(db, recipeId, "lettuce", 50, "g");

        //8) Vegetable Omelette
        recipeId = insertRecipe(
                db,
                "Vegetable Omelette",
                "1. Beat the eggs.\n" +
                        "2. Chop the onion and tomato.\n" +
                        "3. Cook vegetables in a pan.\n" +
                        "4. Add eggs and cook until set."
        );

        insertRecipeIngredient(db, recipeId, "eggs", 3, "piece");
        insertRecipeIngredient(db, recipeId, "onion", 1, "piece");
        insertRecipeIngredient(db, recipeId, "tomato", 100, "g");

        //9) Chicken Curry
        recipeId = insertRecipe(
                db,
                "Chicken Curry",
                "1. Cook the onion.\n" +
                        "2. Add chicken and cook thoroughly.\n" +
                        "3. Add tomato and curry seasoning.\n" +
                        "4. Simmer and serve."
        );

        insertRecipeIngredient(db, recipeId, "chicken", 300, "g");
        insertRecipeIngredient(db, recipeId, "onion", 1, "piece");
        insertRecipeIngredient(db, recipeId, "tomato", 200, "g");

        //10) Tomato Soup
        recipeId = insertRecipe(
                db,
                "Tomato Soup",
                "1. Chop the tomatoes and onion.\n" +
                        "2. Cook them until soft.\n" +
                        "3. Add water and simmer.\n" +
                        "4. Blend and serve."
        );

        insertRecipeIngredient(db, recipeId, "tomato", 400, "g");
        insertRecipeIngredient(db, recipeId, "onion", 1, "piece");

        //11) Potato Omelette
        recipeId = insertRecipe(
                db,
                "Potato Omelette",
                "1. Slice and cook the tomato.\n" +
                        "2. Beat the eggs.\n" +
                        "3. Add eggs to the potatoes.\n" +
                        "4. Cook until set."
        );

        insertRecipeIngredient(db, recipeId, "potato", 300, "g");
        insertRecipeIngredient(db, recipeId, "egg", 3, "piece");

        //12) Tuna Pasta
        recipeId = insertRecipe(
                db,
                "Tuna Pasta",
                "1. Boil the pasta.\n" +
                        "2. Drain the tuna.\n" +
                        "3. Mix tuna with pasta and tomato.\n" +
                        "4. Serve."
        );

        insertRecipeIngredient(db, recipeId, "pasta", 200, "g");
        insertRecipeIngredient(db, recipeId, "tuna", 150, "g");
        insertRecipeIngredient(db, recipeId, "tomato", 150, "g");

        //13) Vegetable Pasta
        recipeId = insertRecipe(
                db,
                "Vegetable Pasta",
                "1. Boil the pasta.\n" +
                        "2. Cook the vegetables.\n" +
                        "3. Add tomato and onion.\n" +
                        "4. Mix with pasta and serve."
        );

        insertRecipeIngredient(db, recipeId, "pasta", 250, "g");
        insertRecipeIngredient(db, recipeId, "carrot", 150, "g");
        insertRecipeIngredient(db, recipeId, "tomato", 150, "g");
        insertRecipeIngredient(db, recipeId, "onion", 1, "piece");

        //14) Chicken Salad
        recipeId = insertRecipe(
                db,
                "Chicken Salad",
                "1. Cook the chicken thoroughly.\n" +
                        "2. Chop lettuce and tomato.\n" +
                        "3. Add chicken.\n" +
                        "4. Mix and serve."
        );

        insertRecipeIngredient(db, recipeId, "chicken", 200, "g");
        insertRecipeIngredient(db, recipeId, "lettuce", 100, "g");
        insertRecipeIngredient(db, recipeId, "tomato", 100, "g");

        //15) Potato Salad
        recipeId = insertRecipe(
                db,
                "Potato Salad",
                "1. Boil the potatoes.\n" +
                        "2. Chop the onion.\n" +
                        "3. Mix potatoes and onion.\n"+
                        "4. Serve."
        );

        insertRecipeIngredient(db, recipeId, "potato", 400, "g");
        insertRecipeIngredient(db, recipeId, "onion", 1, "piece");

        //16) Egg Sandwich
        recipeId = insertRecipe(
                db,
                "Egg Sandwich",
                "1. Boil the eggs.\n" +
                        "2. Slice the eggs.\n" +
                        "3. Place eggs and lettuce between bread.\n" +
                        "4. Serve."
        );

        insertRecipeIngredient(db, recipeId, "bread", 2, "piece");
        insertRecipeIngredient(db, recipeId, "egg", 2, "piece");
        insertRecipeIngredient(db, recipeId, "lettuce", 50, "g");

        //17) Tomato Egg Rice
        recipeId = insertRecipe(
                db,
                "Tomato Egg Rice",
                "1. Cook the rice.\n" +
                        "2. Cook the tomato.\n" +
                        "3. Add beaten eggs.\n" +
                        "4. Mix with rice and serve."
        );

        insertRecipeIngredient(db, recipeId, "rice", 200, "g");
        insertRecipeIngredient(db, recipeId, "tomato", 200, "g");
        insertRecipeIngredient(db, recipeId, "egg", 2, "piece");

        //18) Chicken Potato
        recipeId = insertRecipe(
                db,
                "Chicken Potato",
                "1. Chop the potatoes.\n" +
                        "2. Cook the chicken thoroughly.\n" +
                        "3. Add potatoes and onion.\n" +
                        "4. Cook until tender."
        );

        insertRecipeIngredient(db, recipeId, "chicken", 250, "g");
        insertRecipeIngredient(db, recipeId, "potato", 300, "g");
        insertRecipeIngredient(db, recipeId, "onion", 1, "piece");

        //19) Tuna Rice
        recipeId = insertRecipe(
                db,
                "Tuna Rice",
                "1. Cook the rice.\n" +
                        "2. .Drain the tuna\n" +
                        "3. Mix tuna with rice and peas.\n" +
                        "4. Serve."
        );

        insertRecipeIngredient(db, recipeId, "rice", 250, "g");
        insertRecipeIngredient(db, recipeId, "tuna", 150, "g");
        insertRecipeIngredient(db, recipeId, "peas", 100, "g");

        //20) Vegetable Stir Fry
        recipeId = insertRecipe(
                db,
                "Vegetable Stir Fry",
                "1. Chop the vegetables.\n" +
                        "2. Heat a pan.\n" +
                        "3. Stir-fry the vegetables.\n" +
                        "4. Cook until tender and serve."
        );

        insertRecipeIngredient(db, recipeId, "carrot", 100, "g");
        insertRecipeIngredient(db, recipeId, "peas", 100, "g");
        insertRecipeIngredient(db, recipeId, "onion", 1, "piece");
    }

    //INSERT RECIPE
    private int insertRecipe(
            SQLiteDatabase db,
            String name,
            String method
    ) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_RECIPE_NAME, name);
        values.put(COLUMN_RECIPE_METHOD, method);
        long id = db.insert(TABLE_RECIPES, null, values);

        return (int) id;
    }

    //INSERT RECIPE INGREDIENT
    private void insertRecipeIngredient(
            SQLiteDatabase db,
            int recipeId,
            String ingredientName,
            double quantity,
            String unit
    ) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_RECIPE_ID_FK, recipeId);
        values.put(COLUMN_INGREDIENT_NAME, ingredientName);
        values.put(COLUMN_REQUIRED_QUANTITY, quantity);
        values.put(COLUMN_RECIPE_UNIT, unit);

        db.insert(TABLE_RECIPE_INGREDIENTS, null, values);
    }
}

