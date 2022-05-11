package grmasa.com.open_light.help;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import grmasa.com.open_light.R;

public class Info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_info);

        HashMap<String, String> listAns = new HashMap<>();

        listAns.put(getResources().getString(R.string.question1), getResources().getString(R.string.answer1));
        listAns.put(getResources().getString(R.string.question2), getResources().getString(R.string.answer2));
        listAns.put(getResources().getString(R.string.question3), getResources().getString(R.string.answer3));
        listAns.put(getResources().getString(R.string.question4), getResources().getString(R.string.answer4));
        listAns.put(getResources().getString(R.string.question5), getResources().getString(R.string.answer5));

        ExpandableListView faq = findViewById(R.id.faq);
        List<String> listTitle = new ArrayList<>(listAns.keySet());
        ExpandableListAdapter faqAdapter = new FAQ_Adapter(this, listTitle, listAns);
        faq.setAdapter(faqAdapter);
    }



    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return false;
    }
}
