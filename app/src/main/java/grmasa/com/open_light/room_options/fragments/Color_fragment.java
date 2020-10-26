package grmasa.com.open_light.room_options.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import grmasa.com.open_light.R;
import grmasa.com.open_light.YeelightDevice;
import grmasa.com.open_light.db.Bulb;
import grmasa.com.open_light.db.Room;
import grmasa.com.open_light.room_options.fragments.color.ColorView;
import grmasa.com.open_light.yapi.exception.YeelightResultErrorException;
import grmasa.com.open_light.yapi.exception.YeelightSocketException;

public class Color_fragment extends Fragment {

    private ArrayList<Bulb> bulbs;
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.room_color_fragment, container, false);

        context = getContext();

        if (this.getArguments() != null) {
            Bundle bundle = this.getArguments();
            Room room = bundle.getParcelable("bulb_v");
            assert room != null;
            bulbs = room.getBulbList();
            for (int i = 0; i < bulbs.size(); i++) {
                try {
                    YeelightDevice device = bulbs.get(i).getDevice();
                    try {
                        if (device == null) {
                            device = new YeelightDevice(bulbs.get(i).getIp());
                            bulbs.get(i).setDevice(device);
                        }
                    } catch (YeelightSocketException e) {
                        e.printStackTrace();
                    }
                }catch(NullPointerException ignored){

                }
            }
        }
        ColorView colorView = view.findViewById(R.id.colorView);
        colorView.listenToColorPallet(this::onEvent);
        return view;
    }

    public void onEvent(int color, int touchX, int touchY) {
        String hexColor = String.format("#%06X", (0xFFFFFF & color));
        int red = Integer.valueOf(hexColor.substring(1, 3), 16);
        int green = Integer.valueOf(hexColor.substring(3, 5), 16);
        int blue = Integer.valueOf(hexColor.substring(5, 7), 16);
        //Log.d("Touch event!", "red= "+red+" green: "+green+" blue:"+blue+" valueInt: "+color+" touchX: "+touchX+" touchY: "+touchY);
        for (int i = 0; i < bulbs.size(); i++) {
            try {
                bulbs.get(i).getDevice().setRGB(red, green, blue);
            } catch (YeelightResultErrorException | YeelightSocketException e) {
                e.printStackTrace();
                reset_limit();
            }
        }
    }

    private void reset_limit() {
        for (int i = 0; i < bulbs.size(); i++) {
            try {
                YeelightDevice device = new YeelightDevice(bulbs.get(i).getIp());
                bulbs.get(i).setDevice(device);
                Toast.makeText(context, getResources().getString(R.string.quota_reset), Toast.LENGTH_SHORT).show();
            } catch (YeelightSocketException e1) {
                e1.printStackTrace();
            }
        }
    }

}