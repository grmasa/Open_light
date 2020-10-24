package grmasa.com.open_light.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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

import grmasa.com.open_light.ObjectWrapperForBinder;
import grmasa.com.open_light.R;
import grmasa.com.open_light.db.Bulb;
import grmasa.com.open_light.db.Db;
import grmasa.com.open_light.device_options.Device;
import grmasa.com.open_light.device_setup.Setup_step1;

public class Device_Fragment extends Fragment  {

    private ListView lView;
    private ArrayList<Bulb> bulb_ar;
    private View view;
    private SwipeRefreshLayout pullToRefresh;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.device_fragment, container, false);

        setHasOptionsMenu(true);

        final Button add_device_button = view.findViewById(R.id.add_device_button);
        add_device_button.setOnClickListener(v -> {
            Intent myIntent = new Intent(getActivity(), Setup_step1.class);
            startActivity(myIntent);
        });

        Db db = new Db(getContext());
        bulb_ar = db.getAllBulbs();
        if(bulb_ar.size()>0) {
            //System.out.println(bulb_ar.get(0).getDevice_id());
            ImageView lamp_img = view.findViewById(R.id.lamp_img);
            lamp_img.setVisibility(View.GONE);
            add_device_button.setVisibility(View.GONE);
            new update_ui().execute("");
        }else{
            ImageView lamp_img = view.findViewById(R.id.lamp_img);
            lamp_img.setVisibility(View.VISIBLE);
            add_device_button.setVisibility(View.VISIBLE);
        }

        //Refresh listview on swipe down
        pullToRefresh = view.findViewById(R.id.refresh);
        pullToRefresh.setOnRefreshListener(() -> new update_ui().execute(""));

        return view;
    }

    private void update_listview(View view){
        FragmentActivity activity = getActivity();
        if (activity == null ) {
            return;
        }

        Device_fragment_adapter adapter = new Device_fragment_adapter(bulb_ar, getContext());
        lView = view.findViewById(R.id.device_list);
        lView.setAdapter(adapter);
        lView.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
            Object obj = lView.getItemAtPosition(arg2);
            Bulb b = (Bulb)obj;

            final Bundle bundle = new Bundle();
            bundle.putBinder("bulb_v", new ObjectWrapperForBinder(b));
            startActivity(new Intent(getContext(), Device.class).putExtras(bundle));
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            Intent myIntent = new Intent(getActivity(), Setup_step1.class);
            startActivity(myIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.extra_menu, menu);
    }

    private class update_ui extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            update_listview(view);
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            pullToRefresh.setRefreshing(false);
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}