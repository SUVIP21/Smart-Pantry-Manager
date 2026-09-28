package za.co.smartpantry.smartpantrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "SmartPantrySettings";
    private static final String EXPIRY_ALERTS = "expiryAlerts";
    private Switch switchExpiryAlerts;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);

        Button buttonPantry = findViewById(R.id.buttonSettingsPantry);
        Button buttonRecipes = findViewById(R.id.buttonSettingsRecipes);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        boolean alertsEnabled = preferences.getBoolean(EXPIRY_ALERTS, true);

        switchExpiryAlerts.setChecked(alertsEnabled);

        switchExpiryAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean(EXPIRY_ALERTS, isChecked).apply();
        });

        buttonPantry.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        buttonRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, SuggestedRecipesActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
