package grmasa.com.open_light.db;

import android.os.Parcel;
import android.os.Parcelable;

import grmasa.com.open_light.YeelightDevice;

public class Bulb implements Parcelable {
    private String ip;
    private String name;
    private String device_id;
    private String port;
    private String fw;
    private String support;
    private YeelightDevice device;

    public Bulb(String ip, String name, String device_id, String port, String fw, String support)    {
        this.ip = ip;
        this.name = name;
        this.device_id = device_id;
        this.port = port;
        this.fw = fw;
        this.support = support;
        this.device = null;
    }

    private Bulb(Parcel in) {
        ip = in.readString();
        name = in.readString();
        device_id = in.readString();
        port = in.readString();
        fw = in.readString();
        support = in.readString();
    }

    public static final Creator<Bulb> CREATOR = new Creator<Bulb>() {
        @Override
        public Bulb createFromParcel(Parcel in) {
            return new Bulb(in);
        }

        @Override
        public Bulb[] newArray(int size) {
            return new Bulb[size];
        }
    };

    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip=ip;
    }
    public String getDevice_id() {
        return device_id;
    }
    public String getPort() {
        return port;
    }
    public String getFW() {
        return fw;
    }
    public String getSupport() {
        return support;
    }
    public String getName() {
        return name;
    }
    public YeelightDevice getDevice() {
        return device;
    }
    public void setDevice(YeelightDevice device) {
        this.device = device;
    }
    public void setIP(String ip) {
        this.ip = ip;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ip);
        parcel.writeString(name);
        parcel.writeString(device_id);
        parcel.writeString(port);
        parcel.writeString(fw);
        parcel.writeString(support);
    }
}
