package grmasa.com.open_light.device_options.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import grmasa.com.open_light.R;
import grmasa.com.open_light.YeelightDevice;
import grmasa.com.open_light.db.Bulb;
import grmasa.com.open_light.yapi.enumeration.YeelightFlowAction;
import grmasa.com.open_light.yapi.exception.YeelightResultErrorException;
import grmasa.com.open_light.yapi.exception.YeelightSocketException;
import grmasa.com.open_light.yapi.flow.YeelightFlow;
import grmasa.com.open_light.yapi.flow.transition.YeelightColorTransition;

public class Recommended_fragment extends Fragment {

    private YeelightDevice device;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_options_recommended_fragment, container, false);

        if( this.getArguments() != null) {
            Bundle bundle = this.getArguments();
            Bulb bulb = bundle.getParcelable("bulb_v");
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
                device.setBrightness(100);
                device.setColorTemperature(4000);
            } catch (YeelightResultErrorException | YeelightSocketException e) {
                e.printStackTrace();
            }
        });
        movie_button.setOnClickListener(arg0 -> {
            try {
                device.setRGB(20,20,50);
                device.setBrightness(50);
            } catch (YeelightResultErrorException | YeelightSocketException e) {
                e.printStackTrace();
            }
        });
        romance_button.setOnClickListener(arg0 -> {
            try {
                device.setRGB(255,0,255);
                device.setBrightness(60);
            } catch (YeelightResultErrorException | YeelightSocketException e) {
                e.printStackTrace();
            }
        });
        night_button.setOnClickListener(arg0 -> {
            try {
                device.setRGB(255,153,0);
                device.setBrightness(1);
            } catch (YeelightResultErrorException | YeelightSocketException e) {
                e.printStackTrace();
            }
        });

        return view;
    }

}