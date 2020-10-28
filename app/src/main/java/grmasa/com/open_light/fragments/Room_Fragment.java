package grmasa.com.open_light.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import grmasa.com.open_light.R;
import grmasa.com.open_light.RoomObjectWrapperForBinder;
import grmasa.com.open_light.db.Room;
import grmasa.com.open_light.db.Db;
import grmasa.com.open_light.room_options.Device;
import grmasa.com.open_light.room_options.setup.Room_Setup_Step1;

public class Room_Fragment extends Fragment {
    View view;

    @SuppressLint("StaticFieldLeak")
    private ArrayList<Room> Room_ar;
    private ListView lView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.room_fragment, container, false);

        setHasOptionsMenu(true);

        final Button add_room_button = view.findViewById(R.id.add_room_button);
        add_room_button.setOnClickListener(v -> {
            Intent myIntent = new Intent(getActivity(), Room_Setup_Step1.class);
            startActivity(myIntent);
        });

        Db db = new Db(getContext());
        Room_ar = db.getAllRooms();
        db.close();
        if (Room_ar.size() > 0) {
            //System.out.println(bulb_ar.get(0).getDevice_id());
            ImageView room_img = view.findViewById(R.id.room_img);
            room_img.setVisibility(View.GONE);
            add_room_button.setVisibility(View.GONE);
            new Room_Fragment.update_ui().execute("");
        } else {
            ImageView room_img = view.findViewById(R.id.room_img);
            room_img.setVisibility(View.VISIBLE);
            add_room_button.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private void update_listview(View view) {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }

        Room_fragment_adapter adapter = new Room_fragment_adapter(Room_ar, getContext());
        lView = view.findViewById(R.id.room_list);
        lView.setAdapter(adapter);
        lView.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
            Object obj = lView.getItemAtPosition(arg2);
            Room r = (Room) obj;
            final Bundle bundle = new Bundle();
            bundle.putBinder("bulb_v", new RoomObjectWrapperForBinder(r));
            startActivity(new Intent(getContext(), Device.class).putExtras(bundle));
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.extra_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            Intent myIntent = new Intent(getActivity(), Room_Setup_Step1.class);
            startActivity(myIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class update_ui extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            update_listview(view);
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}