package grmasa.com.open_light.device_setup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.Objects;

import grmasa.com.open_light.R;

public class Setup_step1 extends AppCompatActivity {
    private Button next_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_step1);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        next_button = findViewById(R.id.next);
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()){
            next_button.setAlpha(.5f);
            next_button.setEnabled(false);
        }
        next_button.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Setup_step2.class);
            startActivity(myIntent);
        });

        final Runnable dialog_wifi = this::wifi_dialog;
        Handler handler = new Handler();
        handler.postDelayed(dialog_wifi, 0);

    }

    protected void wifi_dialog() {
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Attention");
            alertDialogBuilder.setMessage(getResources().getString(R.string.wifi_warning)).setCancelable(false).setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    wifiManager.setWifiEnabled(true);
                    next_button.setAlpha(1);
                    next_button.setEnabled(true);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.wifi_enabled),Toast.LENGTH_LONG).show();
                }})
                    .setNegativeButton("Preferences",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return false;
    }
}
