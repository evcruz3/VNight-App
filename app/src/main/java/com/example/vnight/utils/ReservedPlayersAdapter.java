package com.example.vnight.utils;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vnight.R;
import com.google.gson.reflect.TypeToken;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Integer.parseInt;

public class ReservedPlayersAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<HashMap<String, String>> playersList;

    private final HashMap<String,HashMap<String,String>> usersDatabase;

    private static final String TAG = "ReservedPlayersAdapter";

    public ReservedPlayersAdapter(Context context, ArrayList<HashMap<String, String>> list){
        this.mContext = context;
        this.playersList =list;
        TypeToken<HashMap<String,HashMap<String,String>>>  token = new TypeToken<HashMap<String,HashMap<String,String>>>(){};
        this.usersDatabase = SharedPreferenceHandler.getSavedObjectFromPreference(mContext, "UsersDatabase", "users", token.getType());
    }


    @Override
    public int getCount(){
        return playersList.size();
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
        final HashMap<String, String> player = playersList.get(position);


//        int dbSize = usersDatabase.size();
//        int playerID = player.get("userID");
//        for(int i = 0; i<dbSize; i++){
//            final HashMap<String, String> tmp = usersDatabase.get(i);
//            if (Integer.valueOf(tmp.get("entryID")) == playerID){
//                playerInfo = tmp;
//            }
//        }

        if(convertView == null){
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.linearlayout_reserved_players, null);

            final TextView rName = (TextView)convertView.findViewById(R.id.tv_name);
            final TextView rPosition = (TextView)convertView.findViewById(R.id.tv_position);
            final ImageView rAvatar = (ImageView)convertView.findViewById(R.id.imageView);

            final ViewHolder viewHolder = new ViewHolder(rName, rPosition, rAvatar);
            convertView.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder)convertView.getTag();
        String rName;
        HashMap<String, String> playerInfo;

        Log.d(TAG, usersDatabase.toString());

        try {
            playerInfo = usersDatabase.getOrDefault((player.get("userID")), null);
//            playerInfo = usersDatabase.get;
            //Log.d(TAG, "playerInfo: firstName: " + playerInfo.get("firstName"));
        }
        catch (NumberFormatException e){
            Log.d(TAG, e.toString());
            playerInfo = null;
        }

//        Log.d(TAG, playerInfo.get("UserID"));

        if(playerInfo == null){
            rName = player.get("userID") + " (G)";
        }
        else{
            rName = playerInfo.get("firstName") + " " + playerInfo.get("lastName");
        }

        viewHolder.rName.setText(rName);
        viewHolder.rPosition.setText(player.get("position"));
        //TODO: setter for avatar
//        viewHolder.playerName.setText(player.get("playerName"));
        //playerLocation.setText(player.get("playerLocation"));


        return convertView;
    }

    private class ViewHolder {
        private final TextView rName, rPosition;
        private final ImageView rAvatar;

        public ViewHolder(TextView rName, TextView rPosition, ImageView rAvatar){
            this.rName = rName;
            this.rPosition = rPosition;
            this.rAvatar = rAvatar;
        }
    }
}
