package grmasa.com.open_light;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private AlertDialog alertDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Devices").setIcon(R.drawable.baseline_highlight_black_18dp));
        tabLayout.addTab(tabLayout.newTab().setText("Room").setIcon(R.drawable.ic_home_black_24dp));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(0);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        final Runnable dialog_view = this::help_dialog;
        Handler handler = new Handler();
        handler.postDelayed(dialog_view, 0);

        final Runnable dialog_wifi = this::wifi_dialog;
        Handler handler2 = new Handler();
        handler2.postDelayed(dialog_wifi, 0);
    }

    @Override
    public void onResume(){
        super.onResume();
        final Runnable dialog_wifi = this::wifi_dialog;
        Handler handler2 = new Handler();
        handler2.postDelayed(dialog_wifi, 0);
    }

    protected void wifi_dialog() {
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if(!(wifiManager != null && wifiManager.isWifiEnabled()) && alertDialog == null){
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Attention");
                alertDialogBuilder.setMessage(getResources().getString(R.string.wifi_warning)).setCancelable(false).setPositiveButton("Yes", (dialog, id) -> {
                    assert wifiManager != null;
                    wifiManager.setWifiEnabled(true);
                    alertDialog = null;
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.wifi_enabled), Toast.LENGTH_LONG).show();
                })
                        .setNegativeButton("Preferences", (dialog, id) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)));
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }else{
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Attention");
                alertDialogBuilder.setMessage(getResources().getString(R.string.wifi_warning)).setCancelable(false).setPositiveButton("Preferences", (dialog, id) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)));
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }
    }

    protected void help_dialog() {
        super.onStart();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater alertInflater = LayoutInflater.from(this);
        View eulaLayout = alertInflater.inflate(R.layout.checkbox, null);
        SharedPreferences settings = getSharedPreferences("Preferences", 0);
        String skip = settings.getString("skip", "Not checked");

        ImageButton btn_yeelight = eulaLayout.findViewById(R.id.btn_yeelight);
        Picasso.with(this).load(getString(R.string.yeelight_app_url)).fit().into(btn_yeelight);

        CheckBox dontShow = eulaLayout.findViewById(R.id.skip);
        alertDialog.setView(eulaLayout);
        alertDialog.setTitle("Attention");

        alertDialog.setPositiveButton("Ok", (dialog, which) -> {
            String checkBoxResult = "Not checked";

            if (dontShow.isChecked()) {
                checkBoxResult = "checked";
            }

            SharedPreferences settings2 = getSharedPreferences("Preferences", 0);
            SharedPreferences.Editor editor = settings2.edit();

            editor.putString("skip", checkBoxResult);
            editor.apply();
        });

        if (!skip.equals("checked")) {
            alertDialog.show();
        }

        super.onResume();
    }

    public void open_yeelight_app(View v){
        Uri parentAppUri = Uri.parse(getString(R.string.store_base_url) + getString(R.string.parent_app_package));
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent.setAction(Intent.ACTION_VIEW).setData(parentAppUri));
    }

}
