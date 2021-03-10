package com.example.dmitry.easycook;

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class HomeActivity extends AppCompatActivity {
    private ArrayList<String> products = new ArrayList<>();

    AutoCompleteTextView mAutoCompleteTextView;
    Button mOkButton, mFindButton;
    CheckBox mCheckBox;
    SeekBar mSeekBar;
    EditText mCalories;
    ListView ingredientList;
    ArrayList <String> mIngredients;


    private void getIngredients() {
        mIngredients = new ArrayList<String>();
        try {
            FileInputStream fIn = openFileInput("ingr.txt");
            if (fIn != null) {
                InputStreamReader inputreader = new InputStreamReader(fIn);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";
                while ((line = buffreader.readLine()) != null) {
                    mIngredients.add(line);
                }
                System.out.println(mIngredients);
                File dir = getFilesDir();
                File file = new File(dir, "ingr.txt");
                boolean deleted = file.delete();
                System.out.println(deleted);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getIngredients();

        mOkButton = (Button) findViewById(R.id.buttonOK);
        mFindButton = (Button) findViewById(R.id.buttonFind);
        mAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteIngredient);
        mCheckBox = (CheckBox) findViewById(R.id.checkBox);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mCalories = (EditText) findViewById(R.id.Calories);

        try { //файл уже введенных
            File d = getFilesDir();
            File f = new File(d, "chosenIngr.txt");
            if (f.exists()) {
                FileInputStream fIn = openFileInput("chosenIngr.txt");
                if (fIn != null) {
                    InputStreamReader inputreader = new InputStreamReader(fIn);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line = "";
                    while ((line = buffreader.readLine()) != null) {
                        products.add(line);
                    }
                    File dir = getFilesDir();
                    File file = new File(dir, "chosenIngr.txt");
                    boolean deleted = file.delete();
                    System.out.println(deleted);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (products.size() == 0) {
            mSeekBar.setEnabled(false);
            mCalories.setEnabled(false);
            mFindButton.setEnabled(false);
        } else {
            mSeekBar.setEnabled(true);
            mCalories.setEnabled(true);
            mFindButton.setEnabled(true);
        }


        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mCheckBox.isChecked()) {
                    mSeekBar.setEnabled(true);
                    mCalories.setText(String.valueOf(mSeekBar.getProgress()));
                } else {
                    mSeekBar.setEnabled(false);
                    mCalories.setText("");
                }
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mCalories.setText(String.valueOf(mSeekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mAutoCompleteTextView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, mIngredients));

        ingredientList = (ListView) findViewById(R.id.ingredientList);
        final IngredientAdapter adapter = new IngredientAdapter(this, R.layout.list_item, products, mFindButton);
        ingredientList.setAdapter(adapter);

        mFindButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent resultIntent = new Intent(HomeActivity.this, ResultActivity.class);
                Bundle b = new Bundle();
                if (mSeekBar.isEnabled()) {
                    b.putInt("calories", mSeekBar.getProgress());
                } else {
                    b.putInt("calories", 0);
                }
                b.putStringArrayList("ingredients", products);

                resultIntent.putExtras(b);
                startActivity(resultIntent);
            }
        });



        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newIngredient = mAutoCompleteTextView.getText().toString();
                if (mIngredients.contains(newIngredient)) {

                    if (products.contains(newIngredient)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                        builder.setTitle("Оповещение").setMessage("Этот ингредиент уже выбран").setCancelable(true).setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        products.add(newIngredient);
                        adapter.notifyDataSetChanged();
                        try {
                            FileOutputStream fOut = openFileOutput("chosenIngr.txt", MODE_PRIVATE);
                            OutputStreamWriter osw = new OutputStreamWriter(fOut);
                            for (int i = 0; i < products.size(); i++) {
                                osw.write(products.get(i) + "\n");
                            }
                            osw.flush();
                            osw.close();
                            System.out.println("Changed");
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                        if (products.size() != 0){
                            mFindButton.setEnabled(true);
                        }
                        mAutoCompleteTextView.setText("");
                    }

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    builder.setTitle("Оповещение").setMessage("Пожалуйста, введите ингредиент из выпадающего списка").setCancelable(true).setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }
        });

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        File dir = getFilesDir();
        File file = new File(dir, "chosenIngr.txt");
        file.delete();
        System.out.println("deleted");

    }
}
