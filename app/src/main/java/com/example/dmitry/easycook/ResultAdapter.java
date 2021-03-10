package com.example.dmitry.easycook;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dmitry.easycook.ResultElement;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ResultAdapter extends ArrayAdapter<ResultElement> {
    int calori;
    Context context;

    public ResultAdapter(Context context, ArrayList<ResultElement> elements, int calories) {
        super(context, 0, elements);
        this.calori = calories;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ResultElement resEl = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_2, parent, false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.nam);
        TextView calories = (TextView) convertView.findViewById(R.id.calor);

        name.setText(resEl.mName);
        calories.setText(Long.toString(resEl.mCalories));

        if (calori != 0) {
            if (resEl.mCalories > calori) {
                calories.setTextColor(Color.RED);
            } else {
                calories.setTextColor(Color.GREEN);
            }
        }

        return convertView;
    }
}
