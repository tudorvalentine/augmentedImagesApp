package com.tudorvalentine.augmentedimages;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tudorvalentine.augmentedimages.activity.AssociationsActivity;
import com.tudorvalentine.augmentedimages.activity.AugmentedImageActivity;
import com.tudorvalentine.augmentedimages.activity.AuthenticationActivity;
import com.tudorvalentine.augmentedimages.app.AppConfig;
import com.tudorvalentine.augmentedimages.app.AppController;
import com.tudorvalentine.augmentedimages.helpers.SQLiteHandler;
import com.tudorvalentine.augmentedimages.helpers.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ActionActivity extends Activity {
    private static final String TAG = ActionActivity.class.getSimpleName();
    private TextView txtUsername;
    private ImageButton btnLogout;

    private CardView cardCamera;
    private CardView cardAssociations;
    private CardView cardSync;

    private SQLiteHandler db;
    private SessionManager session;

    private ProgressDialog pDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_activity);

        txtUsername = findViewById(R.id.username_associations);
        btnLogout = findViewById(R.id.btnLogout);

        cardAssociations = findViewById(R.id.card_asoc);
        cardCamera = findViewById(R.id.card_camera);
        cardSync = findViewById(R.id.card_sync);

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);

        if (!session.isLoggedIn()){
            logoutUser();
        }
        HashMap<String,String> user = db.getUserDetails();
        Log.d(TAG, "Id_user: " + user.get("id"));
        String username = user.get("username");

        txtUsername.setText(username);
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        cardAssociations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActionActivity.this, AssociationsActivity.class);
                startActivity(intent);
            }
        });
        cardCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActionActivity.this, AugmentedImageActivity.class);
                startActivity(intent);
            }
        });
        cardSync.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String id_user = user.get("id");
                int countRowsAssocApp = db.countRowsAssoc();
                String req_tag = "sync_request";
                pDialog.setMessage("Sincronizare . . .");
                showDialog();
                String url = AppConfig.URL_SYNC + id_user;
                Log.d(TAG,"URL >> " + url);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, AppConfig.URL_SYNC + id_user, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,"Response JSON >> " + response.toString());
                        hideDialog();
                        try {
                            boolean error = response.getBoolean("error");
                            if (!error){
                                int countRowsAssocServer = response.getInt("countedRows");
                                if (countRowsAssocApp < countRowsAssocServer){
                                    JSONArray jsonArray = response.getJSONArray("rows");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        String image_name = jsonObject.getString("image_name");
                                        AppController.getInstance().downloadFile(image_name);
                                    }
                                    Toast.makeText(ActionActivity.this, "Sincronizarea a avut loc cu succes", Toast.LENGTH_LONG).show();
                                }else if (countRowsAssocApp == countRowsAssocServer){
                                    Toast.makeText(ActionActivity.this, "Nu este necesar de o sincronizare", Toast.LENGTH_SHORT).show();
                                }else
                                    Toast.makeText(ActionActivity.this, "Pe server mai putine linii", Toast.LENGTH_SHORT).show();
                            }else
                                Toast.makeText(ActionActivity.this, response.getString("error_msg"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Log.d(TAG, "JSON error >" + e.getMessage());
                            Toast.makeText(ActionActivity.this, "JSON error. A mers ceva rau", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Syncronize error : " + error.getMessage());
                        error.printStackTrace();
                        hideDialog();
                    }
                });
                AppController.getInstance().addToRequestQueue(jsonObjectRequest,req_tag);
            }
        });
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUserAndData();

        Intent intent = new Intent(ActionActivity.this, AuthenticationActivity.class);
        startActivity(intent);
        finish();
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
