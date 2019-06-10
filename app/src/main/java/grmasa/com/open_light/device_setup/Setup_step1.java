package grmasa.com.open_light.device_setup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import java.util.Objects;

import grmasa.com.open_light.R;

public class Setup_step1 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_step1);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        final Button next_button = findViewById(R.id.next);
        next_button.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Setup_step2.class);
            startActivity(myIntent);
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return false;
    }
}
