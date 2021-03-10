package com.example.dmitry.easycook;
import com.example.dmitry.easycook.ResultElement;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import io.opencensus.internal.StringUtils;

import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Random;


public class ResultActivity extends AppCompatActivity {
    private ResultAdapter mAdapter;
    ArrayList<String> products;
    int calories;
    ArrayList<ResultElement> resultDishes = new ArrayList<ResultElement>();
    ArrayList<ResultElement> missingDishes = new ArrayList<ResultElement>();
    Button mBackButton;
    ListView mList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            products = b.getStringArrayList("ingredients");
            calories = b.getInt("calories");
        }
        System.out.println(calories);

        mList = findViewById(R.id.resultList);
        mBackButton = findViewById(R.id.backButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ResultElement resEl = (ResultElement) adapterView.getAdapter().getItem(i);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(resEl.mLink));
                System.out.println(resEl.mLink);
                ResultActivity.this.startActivity(browserIntent);
            }
        });

        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("dishes")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    ArrayList<String> dishIngr = (ArrayList<String>) document.get("ingredients");
                                    System.out.println(dishIngr);
                                    int absentIngr = 0;
                                    ArrayList<String> miss = new ArrayList<String>();

                                    for (int i = 0; i < dishIngr.size(); i++) {
                                        if (!products.contains(dishIngr.get(i))) {
                                            absentIngr += 1;
                                            miss.add(dishIngr.get(i));
                                        }

                                    }
                                    System.out.println(absentIngr);

                                    if (absentIngr == 0) {
                                        resultDishes.add(new ResultElement((String) document.get("name"), (Long) document.get("calories"), (String) document.get("link")));
                                    } else if ((absentIngr == 1)||(absentIngr == 2)) {
                                        missingDishes.add(new ResultElement((String) document.get("name"), 0, TextUtils.join(", ", miss)));
                                    }

                                }
                            }
                            mAdapter = new ResultAdapter(ResultActivity.this, resultDishes, calories);
                            mList.setAdapter(mAdapter);
                            if (resultDishes.size() == 0) {
                                if (missingDishes.size() == 0) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
                                    builder.setTitle("Оповещение").setMessage("К сожалению, из таких ингредиентов нечего приготовить").setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                            finish();
                                        }
                                    });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                } else {
                                    StringBuffer answer = new StringBuffer("К сожалению, из таких ингредиентов нечего приготовить.");
                                    for (int i = 0; i < Integer.min(missingDishes.size(), 5); i++) {
                                        Random random = new Random();
                                        int r = random.nextInt(Integer.min(missingDishes.size(), 5));
                                        ResultElement el = missingDishes.get(r);
                                        missingDishes.remove(r);

                                        answer.append("\nДобавьте " + el.mLink + ", чтобы приготовить " + el.mName + ".");
                                    }

                                    AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
                                    builder.setTitle("Оповещение").setMessage(answer).setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                            finish();
                                        }
                                    });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            }

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
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
