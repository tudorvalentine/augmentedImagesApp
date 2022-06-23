package com.tudorvalentine.augmentedimages.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tudorvalentine.augmentedimages.R;
import com.tudorvalentine.augmentedimages.app.AppConfig;
import com.tudorvalentine.augmentedimages.helpers.SQLiteHandler;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public class LinkToConspectActivity extends Activity implements View.OnClickListener {
    private TextView textView, linkView;
    private String linkToConspect;
    private Button btn_transfer;

    private SQLiteHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consp);

        db = new SQLiteHandler(getApplicationContext());
        List<Map<String,String>> userData = db.getUserData();
        textView = (TextView) findViewById(R.id.txtView);
        linkView = (TextView) findViewById(R.id.linkView);
        btn_transfer = (Button) findViewById(R.id.btn_trece);
        Intent intent = getIntent();

        int i = intent.getIntExtra("index",-1);
        String nameImage = intent.getStringExtra("name");
        String nameFilePdf = "";
        for (Map<String,String> assoc: userData) {
            String toCompare = assoc.get("image");
            if (Objects.equals(toCompare, nameImage)) nameFilePdf = assoc.get("conspect");
        }
//        String [] nameAndExtension = name.split("[.]");

        textView.setText("Recognized image : " + i + " with name >> " + nameImage);
        linkToConspect = AppConfig.URL_PDF + nameFilePdf;
        linkView.setText(linkToConspect);

        btn_transfer.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        Intent intentToConspect = new Intent(Intent.ACTION_VIEW, Uri.parse(linkToConspect));
        startActivity(intentToConspect);
    }
}
