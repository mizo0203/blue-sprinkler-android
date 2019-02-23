
package com.mizo0203.BlueSprinkler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<CustomData> {
    private LayoutInflater layoutInflater_;

    public CustomAdapter(Context context, int resource) {
        super(context, resource);
        layoutInflater_ = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 特定の行(position)のデータを得る
        CustomData item = (CustomData) getItem(position);

        // convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
        if (null == convertView) {
            convertView = layoutInflater_.inflate(R.layout.custom_layout, null);
        }

        // CustomDataのデータをViewの各Widgetにセットする
        // TODO:
        // imageView.setImageBitmap(item.getImageData());

        TextView textView;

        textView = (TextView) convertView.findViewById(R.id.text1);
        textView.setText(item.getScreenName());

        textView = (TextView) convertView.findViewById(R.id.text2);
        textView.setText(item.getName());

        Button button;
        button = (Button) convertView.findViewById(R.id.button1);
        item.buidButton(button);

        return convertView;
    }
}
