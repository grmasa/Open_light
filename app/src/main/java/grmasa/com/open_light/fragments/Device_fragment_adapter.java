package grmasa.com.open_light.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

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
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            view = inflater.inflate(R.layout.device_fragment_adapter_layout, null);
        }

        TextView bulb_ip = view.findViewById(R.id.bulb_ip);
        TextView bulb_id = view.findViewById(R.id.bulb_id);
        bulb_id.setText( list.get(position).getDevice_id());
        bulb_ip.setText( list.get(position).getIp());
        TextView bulb_current_state = view.findViewById(R.id.bulb_current_state);

        ImageView on_off = view.findViewById(R.id.on_off);
        ImageView get_new_ip_btn = view.findViewById(R.id.get_new_ip);

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
        View finalView = view;
        get_new_ip_btn.setOnClickListener(v -> {

            ProgressDialog mDialog = new ProgressDialog(context);
            mDialog.setTitle("Loading");
            mDialog.setMessage("Loading...");
            mDialog.setCancelable(false);
            mDialog.show();

            DatagramSocket dSocket;
            String message = "M-SEARCH * HTTP/1.1\r\n" + "HOST:239.255.255.250:1982\r\n" + "MAN:\"ssdp:discover\"\r\n" + "ST:wifi_bulb\r\n";
            String UDP_HOST = "239.255.255.250";
            int UDP_PORT = 1982;
            int timeout = 7000;
            String ip, id;
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

                if(id.equals(list.get(position).getDevice_id())) {
                    Db db = new Db(context);
                    int rows = db.updateBulbIP(id, ip);
                    System.out.println("ip:" + ip + " id:" + id);
                    if (rows > 0) {
                        list.get(position).setIP(ip);
                        bulb_ip.setText(list.get(position).getIp());

                        device = list.get(position).getDevice();
                        if (device != null) {
                            bulb_current_state.setText(finalView.getResources().getString(R.string.online));
                            bulb_current_state.setTextColor(Color.GREEN);
                            if (device.getState().equals("off")) {
                                device.setPower(true);
                                on_off.setImageResource(R.drawable.icon_bulb_on);
                            } else {
                                device.setPower(false);
                                on_off.setImageResource(R.drawable.icon_bulb_off);
                            }
                        }
                    }
                }

                notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }

            mDialog.dismiss();
        });

        return view;
    }
}
