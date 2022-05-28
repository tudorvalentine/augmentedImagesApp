package com.tudor.augmentedimage;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;


public class LinkToConspectus extends Activity implements View.OnClickListener {
    private TextView textView, linkView;
    private String hostLink = "https://augmented-images.herokuapp.com";
    private String linkToConspect;
    private Button btn_transfer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consp);

        textView = (TextView) findViewById(R.id.txtView);
        linkView = (TextView)findViewById(R.id.linkView);
        btn_transfer = (Button) findViewById(R.id.btn_trece);
        Intent intent = getIntent();

        int i = intent.getIntExtra("index",-1);
        String name = intent.getStringExtra("name");
        String [] nameAndExtension = name.split("[.]");

        textView.setText("Recognized image : " + i + " with name >> " + name);
        linkToConspect = hostLink + "/pdf/?pdf=" + nameAndExtension[0] + ".pdf";
        linkView.setText(linkToConspect);

        btn_transfer.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        Intent intentToConspect = new Intent(Intent.ACTION_VIEW, Uri.parse(linkToConspect));
        startActivity(intentToConspect);
    }
}
