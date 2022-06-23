package com.tudorvalentine.augmentedimages.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tudorvalentine.augmentedimages.R;
import com.tudorvalentine.augmentedimages.helpers.SQLiteHandler;

import java.util.HashMap;

public class AssociationsActivity extends Activity {
    private SQLiteHandler db;
    private ListView listView;
    private TextView pdf;
    private ImageView imageAssoc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.associations_activity);
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String,String> user = db.getUserDetails();
        TextView username = findViewById(R.id.username_associations);
        username.setText(user.get("username"));

        listView = findViewById(R.id.list_view);
        pdf = findViewById(R.id.text_pdf);
        imageAssoc = findViewById(R.id.image_assoc);

        String [] arr = {"database.pdf", "pointer.pdf"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.item_list,R.id.text_pdf, arr);
        listView.setAdapter(adapter);
    }
}
