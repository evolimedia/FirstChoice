package firstchoice.technopear.com.firstchoice.common;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import firstchoice.technopear.com.firstchoice.R;

public class Search_ListAdapter extends BaseAdapter {

    private ArrayList<String> locationBeans;
    private Context context;

    public Search_ListAdapter(Context context, ArrayList<String> locationBeans) {
        this.context = context;
        this.locationBeans = locationBeans;
    }

    @Override
    public int getCount() {
        return locationBeans.size();
    }

    @Override
    public String getItem(int position) {
        return locationBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        String city = getItem(position);
//        LocationBean locationBean = getItem(position);

//        String city = locationBean.getCity();

        if (view == null) {
            Context context = parent.getContext();
            view = LayoutInflater.from(context).inflate(R.layout.search_listrow, null);
        } else {
            String viewTag = (String) view.getTag();
            if (viewTag != null && viewTag.equals(city)) {
                return view;
            }
        }


        ViewHolder viewholder = new ViewHolder(view);
        viewholder.textView.setText("  " + city);

        viewholder.textView.setCompoundDrawables(scaleDrawable(R.drawable.bullates)
                .getDrawable(), null, null, null);

        view.setTag(city);

        return view;
    }

    static class ViewHolder {
        TextView textView;
//        CheckedTextView checkedTextView;

        public ViewHolder(View view) {
            textView = (TextView) view.findViewById(R.id.searchCityTextView);
//            checkedTextView = (CheckedTextView) view.findViewById(R.id.checkedTextView);
        }
    }

    private ScaleDrawable scaleDrawable(int resId) {
        Drawable drawableLocation = context.getResources().getDrawable(resId);
        drawableLocation.setBounds(0, 0, (int) (drawableLocation.getIntrinsicWidth() * 0.6),
                (int) (drawableLocation.getIntrinsicHeight() * 0.6));
        ScaleDrawable sd = new ScaleDrawable(drawableLocation, 0, 0, 0);
        return sd;
    }

    private ScaleDrawable scaleDrawable1(int resId) {
        Drawable drawableLocation = context.getResources().getDrawable(resId);
        drawableLocation.setBounds(0, 0, (int) (drawableLocation.getIntrinsicWidth() * 0.3),
                (int) (drawableLocation.getIntrinsicHeight() * 0.3));
        ScaleDrawable sd = new ScaleDrawable(drawableLocation, 0, 0, 0);
        return sd;
    }
}