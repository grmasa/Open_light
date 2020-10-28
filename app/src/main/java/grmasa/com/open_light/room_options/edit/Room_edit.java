package grmasa.com.open_light.room_options.edit;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import grmasa.com.open_light.MainActivity;
import grmasa.com.open_light.R;
import grmasa.com.open_light.db.Bulb;
import grmasa.com.open_light.db.Db;

public class Room_edit extends AppCompatActivity {

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_setup_step2);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        String roomName = "" + getIntent().getStringExtra("ROOM_NAME");

        List<String> stringBulbList = new ArrayList<>();

        Button done_button = findViewById(R.id.done);
        done_button.setAlpha(.5f);
        done_button.setEnabled(false);

        done_button.setOnClickListener(v -> {
            Db db = new Db(getApplicationContext());
            db.deleteRoom(roomName);
            db.insertRoom(roomName);
            int len = lv.getCount();
            SparseBooleanArray checked = lv.getCheckedItemPositions();
            for (int i = 0; i < len; i++) {
                if (checked.get(i)) {
                    String[] lines = stringBulbList.get(i).split("\\r?\\n");
                    db.insertBulbInRoom(roomName, lines[1].replaceAll("\\s+", ""));
                }
            }
            db.close();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        Db db = new Db(getApplicationContext());
        ArrayList<Bulb> bulb_ar = db.getAllBulbs();
        ArrayList<Bulb> existingBulbs = db.getAllBulbsFromRoom(roomName);
        db.close();
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
        setListViewHeight(lv, stringBulbList);

        //set checked bulbs
        int len = lv.getCount();
        for (int i = 0; i < len; i++) {
            String[] lines = stringBulbList.get(i).split("\\r?\\n");
            String bulbID = lines[1].replaceAll("\\s+", "");
            for (Bulb b : existingBulbs) {
                if (b.getDevice_id().replaceAll("\\s+", "").equals(bulbID)) {
                    lv.setItemChecked(i, true);
                }
            }
        }
    }

    public static void setListViewHeight(ListView lv, List<String> stringBulbList) {
        ListAdapter listAdapter = lv.getAdapter();
        int totalHeight = lv.getPaddingTop() + lv.getPaddingBottom() + 50;
        for (int i = 0; i < (Math.min(stringBulbList.size(), 5)); i++) {
            View listItem = listAdapter.getView(i, null, lv);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = lv.getLayoutParams();
        params.height = totalHeight + (lv.getDividerHeight() * (listAdapter.getCount() - 1));
        lv.setLayoutParams(params);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return false;
    }

}
