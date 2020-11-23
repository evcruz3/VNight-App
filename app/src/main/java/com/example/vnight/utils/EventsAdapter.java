package com.example.vnight.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.vnight.R;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Integer.parseInt;

public class EventsAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<HashMap<String, String>> eventsList;

    public EventsAdapter(Context context, ArrayList<HashMap<String, String>> list){
        this.mContext = context;
        this.eventsList =list;
    }


    @Override
    public int getCount(){
        return eventsList.size();
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    @Override
    public Object getItem(int position){
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final HashMap<String, String> event = eventsList.get(position);

        if(convertView == null){
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.linearlayout_event, null);

            final TextView eventMonth = (TextView)convertView.findViewById(R.id.tv_eventMonth);
            final TextView eventDay = (TextView)convertView.findViewById(R.id.tv_eventDay);
            final TextView eventName = (TextView)convertView.findViewById(R.id.et_eventName);



            final ViewHolder viewHolder = new ViewHolder(eventMonth, eventDay, eventName);
            convertView.setTag(viewHolder);
        }

        String[] date = event.get("eventDate").split("/");
        String monthString = DateFormatSymbols.getInstance().getShortMonths()[parseInt(date[0])-1];
        String dayString = date[1];

        final ViewHolder viewHolder = (ViewHolder)convertView.getTag();

        viewHolder.eventMonth.setText(monthString);
        viewHolder.eventDay.setText(dayString);
        viewHolder.eventName.setText(event.get("eventName"));
        //eventLocation.setText(event.get("eventLocation"));


        return convertView;
    }

    private class ViewHolder {
        private final TextView eventMonth, eventDay, eventName;

        public ViewHolder(TextView eventMonth, TextView eventDay, TextView eventName){
            this.eventMonth = eventMonth;
            this.eventDay = eventDay;
            this.eventName = eventName;
        }
    }
}
