package grmasa.com.open_light.device_options.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import grmasa.com.open_light.R;
import grmasa.com.open_light.YeelightDevice;
import grmasa.com.open_light.db.Bulb;
import grmasa.com.open_light.device_options.fragments.color.ColorView;
import grmasa.com.open_light.yapi.exception.YeelightResultErrorException;
import grmasa.com.open_light.yapi.exception.YeelightSocketException;

public class Color_fragment extends Fragment {

    private YeelightDevice device;
    private Bulb bulb;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_options_color_fragment, container, false);

        if( this.getArguments() != null) {
            Bundle bundle = this.getArguments();
            bulb = bundle.getParcelable("bulb_v");
            if(bulb !=null) {
                device = bulb.getDevice();
                try {
                    if(device==null) {
                        device = new YeelightDevice(bulb.getIp());
                    }
                } catch (YeelightSocketException e) {
                    e.printStackTrace();
                }
            }
        }
        ColorView colorView = view.findViewById(R.id.colorView);
        colorView.listenToColorPallet(this::onEvent);
        return view;
    }

    public void onEvent(int color,int touchX,int touchY) {
        String hexColor = String.format("#%06X", (0xFFFFFF & color));
        int red = Integer.valueOf( hexColor.substring( 1, 3 ), 16 );
        int green = Integer.valueOf( hexColor.substring( 3, 5 ), 16 );
        int blue = Integer.valueOf( hexColor.substring( 5, 7 ), 16 );
        //Log.d("Touch event!", "red= "+red+" green: "+green+" blue:"+blue+" valueInt: "+color+" touchX: "+touchX+" touchY: "+touchY);
        try {
            device.setRGB(red,green,blue);
        } catch (YeelightResultErrorException | YeelightSocketException e) {
            e.printStackTrace();
            reset_limit();
        }
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