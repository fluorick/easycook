package com.example.dmitry.easycook;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    ArrayList<String> allIngredients = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Connector con = new Connector();
        boolean isSuccessful = con.execute();
        if (!isSuccessful) {
            isSuccessful = con.execute();
            if (!isSuccessful) {
                isSuccessful = con.execute();
                if (!isSuccessful) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Сбои в работе приложения").setMessage("Нет интернет-соединения или сервер в данный момент недоступен.").setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();

                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(homeIntent);
                finish();
            }

        }, 3000);
    }


    class Connector {

        boolean execute() {

            try {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("dishes")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        ArrayList<String> ll = (ArrayList<String>) document.get("ingredients");
                                        for (int i = 0; i < ll.size(); i++) {
                                            if (!allIngredients.contains(ll.get(i))) {
                                                allIngredients.add(ll.get(i));
                                            }
                                        }
                                    }
                                    try {
                                        FileOutputStream fOut = openFileOutput("ingr.txt", MODE_PRIVATE);
                                        OutputStreamWriter osw = new OutputStreamWriter(fOut);
                                        for (int i = 0; i < allIngredients.size(); i++) {
                                            osw.write(allIngredients.get(i) + "\n");
                                        }
                                        osw.flush();
                                        osw.close();

                                    } catch (IOException ioe) {
                                        ioe.printStackTrace();
                                    }
                                } else {
                                    System.out.println("Error getting documents." + task.getException());
                                }
                            }
                        });
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}

