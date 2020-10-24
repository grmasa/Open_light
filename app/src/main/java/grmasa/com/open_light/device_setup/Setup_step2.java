package grmasa.com.open_light.device_setup;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Objects;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import grmasa.com.open_light.db.Bulb;
import grmasa.com.open_light.db.Db;

import grmasa.com.open_light.R;

public class Setup_step2 extends AppCompatActivity {

    private TextView refresh_text;
    private ImageView refresh_retry_img;
    private ProgressBar refresh_progress;
    private ListView lv;
    private Button add_device_button;

    private static final String message = "M-SEARCH * HTTP/1.1\r\n" + "HOST:239.255.255.250:1982\r\n" + "MAN:\"ssdp:discover\"\r\n" + "ST:wifi_bulb\r\n";
    private static final String UDP_HOST = "239.255.255.250";
    private static final int UDP_PORT = 1982;
    private static int timeout = 7000;
    private Db db;
    private ArrayList<Bulb> bulb_ar;

    private String id;
    private String fw;
    private String support;
    private String port;
    private String ip;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new Db(this);
        bulb_ar = db.getAllBulbs();

        setContentView(R.layout.activity_setup_step2);

        refresh_text = findViewById(R.id.refresh_text);
        refresh_retry_img = findViewById(R.id.refresh_retry_img);
        refresh_progress = findViewById(R.id.refresh_progress);

        lv = findViewById(R.id.device_list);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        add_device_button = findViewById(R.id.next);

        //scan for devices on activity startup
        scan_device_update_ui();

        add_device_button.setOnClickListener(v -> {
            db.insertBulb(id, ip, fw, port, support, name);
            Intent myIntent = new Intent(getApplicationContext(), Setup_step3.class);
            startActivity(myIntent);
            finish();
        });
        LinearLayout refresh_layout_group = findViewById(R.id.refresh_layout);
        refresh_layout_group.setOnClickListener(view -> {
            lv.setAdapter(null);
            scan_device_update_ui();
        });
    }

    protected void onPause() {
        super.onPause();
        refresh_retry_img.setVisibility(View.VISIBLE);
        refresh_progress.setVisibility(View.GONE);
        refresh_text.setText(R.string.add_device_rescan);
    }

    public void scan_device() {
        DatagramSocket datagramSocket;
        ArrayList<String> devices = new ArrayList<>();
        try {
            datagramSocket = new DatagramSocket();
            DatagramPacket dpSend = new DatagramPacket(message.getBytes(), message.getBytes().length, InetAddress.getByName(UDP_HOST), UDP_PORT);
            datagramSocket.send(dpSend);
            while (true) {
                byte[] buff = new byte[1024];
                DatagramPacket dpRecv = new DatagramPacket(buff, buff.length);
                datagramSocket.setSoTimeout(timeout);
                datagramSocket.receive(dpRecv);
                //datagramSocket.setSoTimeout(0);
                byte[] bytes = dpRecv.getData();
                StringBuilder buffer = new StringBuilder();
                for (int i = 0; i < dpRecv.getLength(); i++) {
                    if (bytes[i] == 13) {
                        continue;
                    }
                    buffer.append((char) bytes[i]);
                }
                if (!buffer.toString().contains("yeelight")) {
                    throw new UnknownHostException("Device not found");
                }
                //System.out.println(buffer.toString());
                String[] infos = buffer.toString().split("\n");
                HashMap<String, String> bulbInfo = new HashMap<>();
                for (String str : infos) {
                    int index = str.indexOf(":");
                    if (index == -1) {
                        continue;
                    }
                    String title = str.substring(0, index);
                    String value = str.substring(index + 1);
                    bulbInfo.put(title, value);
                }

                String location = bulbInfo.get("Location");
                assert location != null;
                String ipPort = location.substring(location.lastIndexOf("/") + 1);
                ip = ipPort.substring(0, ipPort.indexOf(":"));
                port = ipPort.substring(ipPort.indexOf(":") + 1);
                support = bulbInfo.get("support");
                fw = bulbInfo.get("fw_ver");
                id = bulbInfo.get("id");
                name = bulbInfo.get("name");
                boolean exists = false;
                if (!devices.contains(ip + ":" + port)) {
                    //Check if bulb is already added by the user
                    if (bulb_ar.size() > 0) {
                        for (Bulb bulb : bulb_ar) {
                            if(bulb.getDevice_id().equals(id)){
                                exists = true;
                                break;
                            }
                        }
                    }
                    if (!exists) {
                        devices.add(ip + ":" + port);
                    }
                }
            }
        } catch (SocketTimeoutException | UnknownHostException e) {
            e.printStackTrace();
            reset_ui_scan_again();
            timeout = timeout + 5000;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //display device list to user
            update_listview(devices.parallelStream().toArray(String[]::new));
        }
    }

    private void reset_ui_scan_again() {
        runOnUiThread(() -> {
            refresh_retry_img.setVisibility(View.VISIBLE);
            refresh_progress.setVisibility(View.GONE);
            refresh_text.setText(R.string.add_device_rescan);
        });
    }

    private void update_listview(String[] devices) {
        runOnUiThread(() -> {
            reset_ui_scan_again();
            lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, devices));
            lv.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
                ListView lv1 = (ListView) arg0;
                TextView tv = (TextView) lv1.getChildAt(arg2);
                String s1 = tv.getText().toString();
                Toast.makeText(this, s1, Toast.LENGTH_LONG).show();
                add_device_button.setAlpha(1);
                add_device_button.setEnabled(true);
            });
            lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, devices));
            lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        });
    }

    private void scan_device_update_ui() {
        runOnUiThread(() -> {
            runOnUiThread(() -> {
                add_device_button.setAlpha(.5f);
                add_device_button.setEnabled(false);
                refresh_retry_img.setVisibility(View.GONE);
                refresh_progress.setVisibility(View.VISIBLE);
                refresh_text.setText(R.string.add_device_scanning);
            });

            Thread thread = new Thread() {
                @Override
                public void run() {
                    scan_device();
                }
            };
            thread.start();
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
