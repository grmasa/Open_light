package grmasa.com.open_light.room_options.setup;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import grmasa.com.open_light.R;

public class Room_Setup_Step1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_setup_step1);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        EditText room_name = findViewById(R.id.room_name);
        room_name.requestFocus();

        Button next_button = findViewById(R.id.next);
        next_button.setAlpha(.5f);
        next_button.setEnabled(false);

        room_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    next_button.setEnabled(true);
                    next_button.setAlpha(1);
                }
            }
        });

        next_button.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Room_Setup_Step2.class);
            myIntent.putExtra("ROOM_NAME", room_name.getText().toString());
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
