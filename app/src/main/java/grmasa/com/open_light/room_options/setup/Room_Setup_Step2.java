package grmasa.com.open_light.room_options.setup;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import grmasa.com.open_light.MainActivity;
import grmasa.com.open_light.R;
import grmasa.com.open_light.db.Bulb;
import grmasa.com.open_light.db.Db;

public class Room_Setup_Step2 extends AppCompatActivity {

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_setup_step2);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        String roomName = "" + getIntent().getStringExtra("ROOM_NAME");

        List<String> stringBulbList = new ArrayList<>();
        Db db = new Db(getApplicationContext());

        Button done_button = findViewById(R.id.done);
        done_button.setAlpha(.5f);
        done_button.setEnabled(false);

        done_button.setOnClickListener(v -> {
            db.insertRoom(roomName);
            int len = lv.getCount();
            SparseBooleanArray checked = lv.getCheckedItemPositions();
            for (int i = 0; i < len; i++) {
                if (checked.get(i)) {
                    String[] lines = stringBulbList.get(i).split("\\r?\\n");
                    db.insertBulbInRoom(roomName, lines[1].replaceAll("\\s+", ""));
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });

        ArrayList<Bulb> bulb_ar = db.getAllBulbs();
        for (Bulb b : bulb_ar) {
            stringBulbList.add(b.getIp() + "\n" + b.getDevice_id());
        }
        lv = findViewById(R.id.added_device_list);
        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_2, stringBulbList));
        lv.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
            if (lv.getCheckedItemCount() > 0) {
                done_button.setEnabled(true);
                done_button.setAlpha(1);
            } else {
                done_button.setAlpha(.5f);
                done_button.setEnabled(false);
            }
        });
        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, stringBulbList));
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return false;
    }

}
