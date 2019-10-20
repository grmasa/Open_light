package grmasa.com.open_light.fragments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import grmasa.com.open_light.MainActivity;
import grmasa.com.open_light.R;
import grmasa.com.open_light.YeelightDevice;
import grmasa.com.open_light.db.Bulb;
import grmasa.com.open_light.db.Db;
import grmasa.com.open_light.yapi.exception.YeelightResultErrorException;
import grmasa.com.open_light.yapi.exception.YeelightSocketException;

public class Device_fragment_adapter extends BaseAdapter implements ListAdapter {

    private ArrayList<Bulb> list;
    private Context context;
    private YeelightDevice device;
    private TextView bulb_current_state, bulb_ip;
    private AlertDialog bulb_ip_edit_alert;
    private int position;
    private ImageView get_new_ip_btn, on_off;
    private View finalView;

    Device_fragment_adapter(ArrayList<Bulb> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        this.position = position;
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            view = inflater.inflate(R.layout.device_fragment_adapter_layout, null);
        }

        bulb_ip = view.findViewById(R.id.bulb_ip);
        final EditText bulb_ip_edit = new EditText(context);

        bulb_ip_edit_alert = new AlertDialog.Builder(context).create();
        bulb_ip_edit_alert.setTitle("Edit bulb IP");
        bulb_ip_edit_alert.setView(bulb_ip_edit);

        bulb_ip_edit_alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
            System.out.println("yolooo 6:"+bulb_ip_edit.getText());
            System.out.println("yolooo 6:"+bulb_ip_edit.getText().toString());
            list.get(position).setIp(bulb_ip_edit.getText().toString());
            bulb_ip.setText(bulb_ip_edit.getText().toString());

            Db db = new Db(context);
            int rows = db.updateBulbIP(list.get(position).getDevice_id(), bulb_ip_edit.getText().toString());
            System.out.println("New ip:" + bulb_ip_edit.getText().toString() + " id:" + list.get(position).getDevice_id());
            if (rows > 0) {
                list.get(position).setIP(bulb_ip_edit.getText().toString());
                bulb_ip.setText(list.get(position).getIp());

                Intent restart_activity = new Intent(context, MainActivity.class);
                PendingIntent pending_intent = PendingIntent.getActivity(context, 123456,    restart_activity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis(), pending_intent);
                System.exit(0);
            }
            bulb_ip_edit_alert.dismiss();

        });

        bulb_ip.setOnClickListener(v -> {
            bulb_ip_edit.setText(bulb_ip.getText());
            bulb_ip_edit_alert.show();
        });

        TextView bulb_id = view.findViewById(R.id.bulb_id);
        bulb_id.setText( list.get(position).getDevice_id());
        bulb_ip.setText( list.get(position).getIp());
        bulb_current_state = view.findViewById(R.id.bulb_current_state);

        on_off = view.findViewById(R.id.on_off);
        get_new_ip_btn = view.findViewById(R.id.get_new_ip);

        device = list.get(position).getDevice();

        try {
            if(device==null) {
                on_off.setImageResource(R.drawable.icon_bulb_off);
                device = new YeelightDevice(list.get(position).getIp());
                list.get(position).setDevice(device);
            }
            bulb_current_state.setText(view.getResources().getString(R.string.online));
            bulb_current_state.setTextColor(Color.GREEN);
            if(device.getState().equals("off")){
                on_off.setImageResource(R.drawable.icon_bulb_off);
            }else{
                on_off.setImageResource(R.drawable.icon_bulb_on);
            }

        } catch (YeelightSocketException | YeelightResultErrorException e) {
            if(e.getMessage().contains("Broken pipe")){
                try {
                    device = null;
                    device = new YeelightDevice(list.get(position).getIp());
                    list.get(position).setDevice(device);
                } catch (YeelightSocketException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
            on_off.setImageResource(R.drawable.icon_bulb_off);
            bulb_current_state.setText(view.getResources().getString(R.string.offline));
            bulb_current_state.setTextColor(Color.RED);
        }
        on_off.setOnClickListener(v -> {
            device = list.get(position).getDevice();
            if(device!=null) {
                try {
                    if (device.getState().equals("off")) {
                        device.setPower(true);
                        on_off.setImageResource(R.drawable.icon_bulb_on);
                    } else {
                        device.setPower(false);
                        on_off.setImageResource(R.drawable.icon_bulb_off);
                    }
                } catch (YeelightResultErrorException | YeelightSocketException e) {
                    e.printStackTrace();
                }
                notifyDataSetChanged();
            }
        });
        finalView = view;

        get_new_ip_btn.setOnClickListener(v -> {

            RotateAnimation rotateAnimation = new RotateAnimation(0, 360f,Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setInterpolator(new LinearInterpolator());
            rotateAnimation.setDuration(500);
            rotateAnimation.setRepeatCount(Animation.INFINITE);
            get_new_ip_btn.startAnimation(rotateAnimation);

            new AsyncTaskExample().execute();

        });

        return finalView;
    }


    private class AsyncTaskExample extends AsyncTask<Void, Void, Void> {
        private String ip, id;

        @Override
        protected Void doInBackground(Void... voids){
            DatagramSocket dSocket;
            String message = "M-SEARCH * HTTP/1.1\r\n" + "HOST:239.255.255.250:1982\r\n" + "MAN:\"ssdp:discover\"\r\n" + "ST:wifi_bulb\r\n";
            String UDP_HOST = "239.255.255.250";
            int UDP_PORT = 1982;
            int timeout = 7000;
            try {
                dSocket = new DatagramSocket();
                DatagramPacket dpSend = new DatagramPacket(message.getBytes(),message.getBytes().length, InetAddress.getByName(UDP_HOST), UDP_PORT);
                dSocket.send(dpSend);
                byte[] buf = new byte[1024];
                DatagramPacket dpRecv = new DatagramPacket(buf, buf.length);
                dSocket.setSoTimeout(timeout);
                dSocket.receive(dpRecv);
                dSocket.setSoTimeout(0);
                byte[] bytes = dpRecv.getData();
                StringBuilder buffer = new StringBuilder();
                for (int i = 0; i < dpRecv.getLength(); i++) {
                    if (bytes[i] == 13) {
                        continue;
                    }
                    buffer.append((char) bytes[i]);
                }
                if (!buffer.toString().contains("yeelight")) {
                    throw new Exception("Device not found");
                }
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
                String ipPort = location.substring(location.lastIndexOf("/")+1);
                ip = ipPort.substring(0,ipPort.indexOf(":"));
                id = bulbInfo.get("id");

            } catch (Exception e) {
                System.out.println("nigger");
                System.out.println(e.toString());
                e.printStackTrace();
                get_new_ip_btn.clearAnimation();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param){
            try {
                if(id.equals(list.get(position).getDevice_id())) {
                    Db db = new Db(context);
                    int rows = db.updateBulbIP(id, ip);
                    System.out.println("ip:" + ip + " id:" + id);
                    if (rows > 0) {
                        list.get(position).setIP(ip);
                        bulb_ip.setText(list.get(position).getIp());

                        device = new YeelightDevice(ip);
                        bulb_current_state.setText(finalView.getResources().getString(R.string.online));
                        bulb_current_state.setTextColor(Color.GREEN);
                        if (device.getState().equals("off")) {
                            on_off.setImageResource(R.drawable.icon_bulb_on);
                        } else {
                            on_off.setImageResource(R.drawable.icon_bulb_off);
                        }
                    }
                }

                notifyDataSetChanged();
                get_new_ip_btn.clearAnimation();
            } catch (Exception e) {
                get_new_ip_btn.clearAnimation();
                e.printStackTrace();
            }
        }
    }

}
