package com.example.dmitry.easycook;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class IngredientAdapter extends ArrayAdapter<String> {
    private LayoutInflater inflater;
    private int layout;
    private ArrayList<String> ingredientList;
    private Button button;
    private Context con;

    IngredientAdapter(Context context, int resource, ArrayList<String> ingredients, Button b) {
        super(context, resource, ingredients);
        this.ingredientList = ingredients;
        this.layout = resource;
        con = context;
        this.inflater = LayoutInflater.from(context);
        this.button = b;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String product = ingredientList.get(position);

        viewHolder.nameView.setText(product);

        viewHolder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingredientList.remove(position);
                notifyDataSetChanged();
                try {
                    FileOutputStream fOut = con.openFileOutput("chosenIngr.txt", Context.MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);
                    for (int i = 0; i < ingredientList.size(); i++) {
                        osw.write(ingredientList.get(i) + "\n");
                    }
                    osw.flush();
                    osw.close();
                    System.out.println("Changed");
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                if (ingredientList.size() == 0) {
                    button.setEnabled(false);
                }
            }
        });

        return convertView;
    }

    private class ViewHolder {
        final Button removeButton;
        final TextView nameView;

        ViewHolder(View view) {
            removeButton = (Button) view.findViewById(R.id.removeButton);
            nameView = (TextView) view.findViewById(R.id.nameView);
        }

    }
}