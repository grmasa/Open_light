package grmasa.com.open_light.room_options;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

import grmasa.com.open_light.MainActivity;
import grmasa.com.open_light.R;
import grmasa.com.open_light.RoomObjectWrapperForBinder;
import grmasa.com.open_light.YeelightDevice;
import grmasa.com.open_light.db.Bulb;
import grmasa.com.open_light.db.Db;
import grmasa.com.open_light.db.Room;
import grmasa.com.open_light.yapi.exception.YeelightResultErrorException;
import grmasa.com.open_light.yapi.exception.YeelightSocketException;

public class Device extends AppCompatActivity {

    private Room room;
    private ArrayList<Bulb> bulbs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_options);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //get bulb object
        room = ((RoomObjectWrapperForBinder) Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).getBinder("bulb_v"))).getData();

        SeekBar brightness_bar = findViewById(R.id.brightness_bar);
        brightness_bar.setOnSeekBarChangeListener(seekBarChangeListener);
        bulbs = room.getBulbList();
        if (bulbs.get(0).getDevice() != null) {

            YeelightDevice device = bulbs.get(0).getDevice();
            try {
                if (device == null) {
                    device = new YeelightDevice(bulbs.get(0).getIp());
                }
                brightness_bar.setProgress(Integer.parseInt(device.getBrightness()));
            } catch (YeelightSocketException | YeelightResultErrorException e) {
                if (Objects.requireNonNull(e.getMessage()).contains("Broken pipe")) {
                    try {
                        device = new YeelightDevice(bulbs.get(0).getIp());
                        bulbs.get(0).setDevice(device);
                    } catch (YeelightSocketException e1) {
                        e1.printStackTrace();
                    }
                }
                //if device is offline
                Intent myIntent = new Intent(getApplicationContext(), Device_error.class);
                startActivity(myIntent);
                finish();
                e.printStackTrace();
            }
        } else {
            //if bulb variable is null
            Intent myIntent = new Intent(getApplicationContext(), Device_error.class);
            startActivity(myIntent);
            finish();
        }
        Context context = Device.this;

        Button deleteBtn = findViewById(R.id.delete_btn);
        deleteBtn.setText(getResources().getString(R.string.delete_room));
        deleteBtn.setOnClickListener(v -> {
            ProgressDialog mDialog = new ProgressDialog(context);
            mDialog.setMessage("Loading...");
            mDialog.setCancelable(false);
            mDialog.show();
            Db db = new Db(context);
            db.deleteRoom(room.getName());

            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        });

        TabLayout tabLayout = findViewById(R.id.device_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Recommended"));
        tabLayout.addTab(tabLayout.newTab().setText("Color"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = findViewById(R.id.device_pager);
        final Device_PagerAdapter adapter = new Device_PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), room);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(0);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

        });


    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // after the user finishes moving the bat change the brightness
            try {
                for (int i = 0; i < bulbs.size(); i++) {
                    YeelightDevice device = bulbs.get(0).getDevice();
                    device.setBrightness(seekBar.getProgress());
                }
            } catch (YeelightResultErrorException | YeelightSocketException e) {
                e.printStackTrace();
                reset_limit();
            }
        }
    };

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return false;
    }

    private void reset_limit() {
        for (int i = 0; i < bulbs.size(); i++) {
            try {
                YeelightDevice device = new YeelightDevice(bulbs.get(0).getIp());
                bulbs.get(0).setDevice(device);
            } catch (YeelightSocketException e1) {
                e1.printStackTrace();
            }
        }
    }
}
