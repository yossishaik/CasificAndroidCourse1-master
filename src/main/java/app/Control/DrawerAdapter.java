package app.Control;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import app.R;

public class DrawerAdapter extends ArrayAdapter<String> {
    Context mContext;

    public DrawerAdapter(Context c) {
        // Super constructor to create the adapter and fill titles
        super(c, R.layout.drawer_single_row);
        this.mContext = c;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_single_row, null);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);

        // setting the image resource and headerTitle
        txtTitle.setText(mContext.getResources().getStringArray(
                R.array.drawer_list)[position]);

        switch (position) {

            case 0:
                // Facebook
                imgIcon.setImageResource(R.drawable.ic_facebook);
                break;
            case 1:
                // email
                imgIcon.setImageResource(R.drawable.ic_email);
                break;
            case 2:
                // linkdin
                imgIcon.setImageResource(R.drawable.ic_linkedin);
                break;
            case 3:
                // CV
                imgIcon.setImageResource(R.drawable.ic_cv);
                break;
            case 4:
                //ebay
                imgIcon.setImageResource(R.drawable.ic_ebay);
                break;
            case 5:
                // youtube
                imgIcon.setImageResource(R.drawable.ic_youtube);
                break;


        }
        return convertView;
    }

    @Override
    public int getCount() {
        return mContext.getResources().getStringArray(R.array.drawer_list).length;
    }


}
