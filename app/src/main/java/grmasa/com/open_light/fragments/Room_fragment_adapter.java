package grmasa.com.open_light.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import grmasa.com.open_light.R;
import grmasa.com.open_light.YeelightDevice;
import grmasa.com.open_light.db.Bulb;
import grmasa.com.open_light.db.Room;
import grmasa.com.open_light.db.Db;
import grmasa.com.open_light.yapi.exception.YeelightResultErrorException;
import grmasa.com.open_light.yapi.exception.YeelightSocketException;

public class Room_fragment_adapter extends BaseAdapter implements ListAdapter {

    private ArrayList<Room> list;
    private Context context;
    private YeelightDevice device;
    private TextView room_name;
    private AlertDialog room_name_edit_alert;
    private ImageView on_off;

    Room_fragment_adapter(ArrayList<Room> list, Context context) {
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
            view = inflater.inflate(R.layout.room_fragment_adapter_layout, null);
        }

        room_name = view.findViewById(R.id.room_name);
        final EditText room_name_edit = new EditText(context);

        room_name_edit_alert = new AlertDialog.Builder(context).create();
        room_name_edit_alert.setTitle("Edit Room Name");
        room_name_edit_alert.setView(room_name_edit);
        room_name_edit_alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
            Db db = new Db(context);
            int rows = db.changeRoomName(room_name_edit.getText().toString(), room_name.getText().toString());
            if (rows > 0) {
                list.get(position).setName(room_name_edit.getText().toString());
                room_name.setText(list.get(position).getName());
            }
            room_name_edit_alert.dismiss();
            db.close();
        });
        room_name_edit_alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> room_name_edit_alert.dismiss());

        room_name.setOnClickListener(v -> {
            room_name_edit.setText(room_name.getText());
            room_name_edit_alert.show();
        });

        TextView room_name = view.findViewById(R.id.room_name);
        TextView bulbs_open = view.findViewById(R.id.bulbs_open_num);
        TextView bulbs_closed = view.findViewById(R.id.bulbs_closed_num);
        TextView bulbs_unknown = view.findViewById(R.id.bulbs_unknown_num);
        int bulbs_unknown_num =0, bulbs_closed_num = 0, bulbs_open_num = 0;
        room_name.setText( list.get(position).getName());

        on_off = view.findViewById(R.id.on_off);
        ArrayList<Bulb> array_list = list.get(position).getBulbList();
        for ( Bulb b : array_list){
            device = b.getDevice();
            try {
                if(device==null) {
                    device = new YeelightDevice(b.getIp());
                    b.setDevice(device);
                }
                if(device.getState().equals("off")){
                    bulbs_closed_num++;
                }else{
                    bulbs_open_num++;
                }

            } catch (YeelightSocketException | YeelightResultErrorException e) {
                if(Objects.requireNonNull(e.getMessage()).contains("Broken pipe")){
                    try {
                        device = null;
                        device = new YeelightDevice(b.getIp());
                        b.setDevice(device);
                    } catch (YeelightSocketException e1) {
                        e1.printStackTrace();
                    }
                }
                e.printStackTrace();
                bulbs_unknown_num++;
            }
        }
        bulbs_open.setText(String.valueOf(bulbs_open_num));
        bulbs_closed.setText(String.valueOf(bulbs_closed_num));
        bulbs_unknown.setText(String.valueOf(bulbs_unknown_num));

        if (bulbs_closed_num < bulbs_open_num) {
            on_off.setImageResource(R.drawable.icon_bulb_on);
        } else {
            on_off.setImageResource(R.drawable.icon_bulb_off);
        }

        on_off.setOnClickListener(v -> {
            int bulbs_unknown_num_temp =0, bulbs_closed_num_temp = 0, bulbs_open_num_temp = 0;
            for ( Bulb b : array_list) {
                device = b.getDevice();
                try {
                    if(device==null) {
                        device = new YeelightDevice(b.getIp());
                        b.setDevice(device);
                    }
                    if(device.getState().equals("off")){
                        device.setPower(true);
                        bulbs_closed_num_temp++;
                    }else{
                        device.setPower(false);
                        bulbs_open_num_temp++;
                    }

                } catch (YeelightSocketException | YeelightResultErrorException e) {
                    if(Objects.requireNonNull(e.getMessage()).contains("Broken pipe")){
                        try {
                            device = null;
                            device = new YeelightDevice(b.getIp());
                            b.setDevice(device);
                        } catch (YeelightSocketException e1) {
                            e1.printStackTrace();
                        }
                    }
                    e.printStackTrace();
                    bulbs_unknown_num_temp++;
                }
            }
            bulbs_open.setText(String.valueOf(bulbs_open_num_temp));
            bulbs_closed.setText(String.valueOf(bulbs_closed_num_temp));
            bulbs_unknown.setText(String.valueOf(bulbs_unknown_num_temp));
            if (bulbs_closed_num_temp < bulbs_open_num_temp) {
                on_off.setImageResource(R.drawable.icon_bulb_on);
            } else {
                on_off.setImageResource(R.drawable.icon_bulb_off);
            }
            notifyDataSetChanged();
        });
        return view;
    }

}
