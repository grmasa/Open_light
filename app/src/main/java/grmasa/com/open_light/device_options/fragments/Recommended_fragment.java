package grmasa.com.open_light.device_options.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import grmasa.com.open_light.R;
import grmasa.com.open_light.YeelightDevice;
import grmasa.com.open_light.db.Bulb;
import grmasa.com.open_light.yapi.exception.YeelightResultErrorException;
import grmasa.com.open_light.yapi.exception.YeelightSocketException;

public class Recommended_fragment extends Fragment {

    private YeelightDevice device;
    private Bulb bulb;
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_options_recommended_fragment, container, false);

        context = getContext();

        if( this.getArguments() != null) {
            Bundle bundle = this.getArguments();
            bulb = bundle.getParcelable("bulb_v");
            if(bulb !=null) {
                device = bulb.getDevice();
                try {
                    if(device ==null) {
                        device = new YeelightDevice(bulb.getIp());
                    }
                } catch (YeelightSocketException e) {
                    e.printStackTrace();
                }
            }
        }

        Button default_button = view.findViewById(R.id.recommended_button_default);
        Button movie_button = view.findViewById(R.id.recommended_button_movie);
        Button romance_button = view.findViewById(R.id.recommended_button_romance);
        Button night_button = view.findViewById(R.id.recommended_button_night);

        default_button.setOnClickListener(arg0 -> {
            try {
                if (device.getState().equals("off")) {
                    device.setPower(true);
                }
                device.setBrightness(100);
                device.setColorTemperature(4000);
            } catch (YeelightResultErrorException | YeelightSocketException e) {
                e.printStackTrace();
                reset_limit();
            }
        });
        movie_button.setOnClickListener(arg0 -> {
            try {
                if (device.getState().equals("off")) {
                    device.setPower(true);
                }
                device.setRGB(20,20,50);
                device.setBrightness(50);
            } catch (YeelightResultErrorException | YeelightSocketException e) {
                e.printStackTrace();
                reset_limit();
            }
        });
        romance_button.setOnClickListener(arg0 -> {
            try {
                if (device.getState().equals("off")) {
                    device.setPower(true);
                }
                device.setRGB(255,0,255);
                device.setBrightness(60);
            } catch (YeelightResultErrorException | YeelightSocketException e) {
                e.printStackTrace();
                reset_limit();
            }
        });
        night_button.setOnClickListener(arg0 -> {
            try {
                if (device.getState().equals("off")) {
                    device.setPower(true);
                }
                device.setRGB(255,153,0);
                device.setBrightness(1);
            } catch (YeelightResultErrorException | YeelightSocketException e) {
                e.printStackTrace();
                reset_limit();
            }
        });

        return view;
    }

    private void reset_limit(){
        device = null;
        try {
            device = new YeelightDevice(bulb.getIp());
            Toast.makeText(context,getResources().getString(R.string.quota_reset),Toast.LENGTH_SHORT).show();
        } catch (YeelightSocketException e1) {
            e1.printStackTrace();
        }
    }

}
