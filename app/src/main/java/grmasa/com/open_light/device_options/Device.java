package grmasa.com.open_light.device_options;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;

import java.util.Objects;

import grmasa.com.open_light.MainActivity;
import grmasa.com.open_light.ObjectWrapperForBinder;
import grmasa.com.open_light.R;
import grmasa.com.open_light.YeelightDevice;
import grmasa.com.open_light.db.Bulb;
import grmasa.com.open_light.db.Db;
import grmasa.com.open_light.yapi.exception.YeelightResultErrorException;
import grmasa.com.open_light.yapi.exception.YeelightSocketException;

public class Device extends AppCompatActivity {

    private Bulb bulb;
    private YeelightDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_options);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //get bulb object
        bulb = ((ObjectWrapperForBinder) Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).getBinder("bulb_v"))).getData();

        SeekBar brightness_bar = findViewById(R.id.brightness_bar);
        brightness_bar.setOnSeekBarChangeListener(seekBarChangeListener);

        if(bulb!=null) {

            device = bulb.getDevice();
            try {
                if(device==null) {
                    device = new YeelightDevice(bulb.getIp());
                }
                brightness_bar.setProgress(Integer.parseInt(device.getBrightness()));
            } catch (YeelightSocketException | YeelightResultErrorException e) {
                if(e.getMessage().contains("Broken pipe")){
                    try {
                        device = null;
                        device = new YeelightDevice(bulb.getIp());
                        bulb.setDevice(device);
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
        }else{
            //if bulb variable is null
            Intent myIntent = new Intent(getApplicationContext(), Device_error.class);
            startActivity(myIntent);
            finish();
        }
        Context context = Device.this;

        Button deleteBtn = findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(v -> {
            ProgressDialog mDialog = new ProgressDialog(context);
            mDialog.setMessage("Loading...");
            mDialog.setCancelable(false);
            mDialog.show();
            Db db = new Db(context);
            db.deleteBulb(bulb.getDevice_id());

            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        });

        TabLayout tabLayout = findViewById(R.id.device_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Recommended"));
        tabLayout.addTab(tabLayout.newTab().setText("Color"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = findViewById(R.id.device_pager);
        final Device_PagerAdapter adapter = new Device_PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), bulb);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(0);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}

        });


    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // after the user finishes moving the bat change the brightness
            try {
                device.setBrightness(seekBar.getProgress());
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

    private void reset_limit(){
        device = null;
        try {
            device = new YeelightDevice(bulb.getIp());
        } catch (YeelightSocketException e1) {
            e1.printStackTrace();
        }
    }
}
