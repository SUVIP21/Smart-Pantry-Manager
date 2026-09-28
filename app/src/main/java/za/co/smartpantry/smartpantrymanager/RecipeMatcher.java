package za.co.smartpantry.smartpantrymanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
public class RecipeMatcher {
    public static ArrayList<Recipe> getMatchingRecipes(
            ArrayList<Recipe> recipes,
            ArrayList<PantryItem> pantryItems,
            DatabaseHelper databaseHelper
    ) {
        ArrayList<Recipe> matchingRecipes = new ArrayList<>();

        for (Recipe recipe : recipes) {
            ArrayList<RecipeIngredient> requiredIngredients = databaseHelper.getRecipeIngredients(recipe.getId());
            boolean recipeMatches = true;
            for (RecipeIngredient required : requiredIngredients) {
                PantryItem pantryItem = findMatchingPantryItem(required, pantryItems);
                if (pantryItem == null) {
                    recipeMatches = false;
                    break;
                }
                if (!hasEnoughQuantity(pantryItem, required)) {
                    recipeMatches = false;
                    break;
                }
            }
            if (recipeMatches) {
                matchingRecipes.add(recipe);
            }
        }
        return matchingRecipes;
    }

    //Find matching Pantry Item
    private static PantryItem findMatchingPantryItem(RecipeIngredient required, ArrayList<PantryItem> pantryItems) {
        String requiredName = normalizeIngredientName(required.getIngredientName());
        for (PantryItem pantryItem : pantryItems) {
            String pantryName = normalizeIngredientName(pantryItem.getName());
            if (requiredName.equals(pantryName)) {
                return pantryItem;
            }
        }
        return null;
    }

    //CHECK QUANTITY
    private static boolean hasEnoughQuantity(PantryItem pantryItem, RecipeIngredient required) {
        String pantryUnit = normalizeUnit(pantryItem.getUnit());
        String recipeUnit = normalizeUnit(required.getUnit());
        String pantryType = getUnitType(pantryUnit);
        String recipeType = getUnitType(recipeUnit);

        if (!pantryType.equals(recipeType)) {
            return false;
        }

        double pantryQuantity = convertToBaseUnit(pantryItem.getQuantity(), pantryUnit);
        double requiredQuantity = convertToBaseUnit(required.getRequiredQuantity(), recipeUnit);
        return pantryQuantity >= requiredQuantity;
    }

    //Normalize Ingredient Name
    public static String normalizeIngredientName(String ingredient) {
        if (ingredient == null) {
            return "";
        }

        String name = ingredient.trim().toLowerCase(Locale.US);

        name = name.replaceAll("[^a-z0-9 ]", "");
        name = name.replaceAll("\\s+", " ");

        if (name.equals("tomatoes")) {
            return "tomato";
        }
        if (name.equals("potatoes")) {
            return "potato";
        }
        if (name.equals("carrots")) {
            return "carrot";
        }
        if (name.equals("peas")) {
            return "pea";
        }
        if (name.equals("eggs")) {
            return "egg";
        }
        if (name.equals("onions")) {
            return "onion";
        }
        if (name.equals("chickens")) {
            return "chicken";
        }
        if (name.equals("lettuces")) {
            return "lettuce";
        }
        return name;
    }

    //Normalize Unit
    private static String normalizeUnit(String unit) {
        if (unit == null) {
            return "";
        }
        String normalized = unit.trim().toLowerCase(Locale.US);

        switch (normalized) {
            case "grams":
            case "gram":
                return "g";

            case "kilograms":
            case "kilogram":
                return "kg";

            case "millilitres":
            case "millilitre":
            case "milliliters":
            case "milliliter":
                return "ml";

            case "litres":
            case "litre":
            case "liters":
            case "liter":
                return "l";

            case "pieces":
            case "piece":
            case "pcs":
                return "piece";

            default:
                return normalized;
        }
    }

    //Unit Type
    private static String getUnitType(String unit) {
        switch (unit) {
            case "g":
            case "kg":
                return "mass";

            case "ml":
            case "l":
                return "volume";

            case "piece":
                return "count";

            default:
                return "unknown";
        }
    }

    //Convert to base unit
    private static double convertToBaseUnit(double quantity, String unit) {
        switch (unit) {
            case "kg":
                return quantity * 1000;

            case "g":
                return quantity;

            case "l":
                return quantity * 1000;

            case "ml":
                return quantity;

            case "piece":
                return quantity;

            default:
                return quantity;
        }
    }
}
