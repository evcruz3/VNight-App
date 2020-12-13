package com.example.vnight.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vnight.R;
import com.example.vnight.customClasses.DragData;
import com.example.vnight.customClasses.UserInfo;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by becody.com on 09,10,2019
 */
public class ReservedPlayersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private final ArrayList<HashMap<String, String>> playersList;

//    private static final HashMap<String,HashMap<String,String>> usersDatabase = UsersDatabase.getUsersDatabase();
    private static HashMap<String,HashMap<String,String>> usersDatabase;

    private static final String TAG = "ReservedPlayersAdapter";

    public ReservedPlayersAdapter(Context context, ArrayList<HashMap<String, String>> list){
        this.mContext = context;
        this.playersList =list;
        TypeToken<HashMap<String,HashMap<String,String>>> token = new TypeToken<HashMap<String,HashMap<String,String>>>(){};
        this.usersDatabase = SharedPreferenceHandler.getSavedObjectFromPreference(mContext, "UsersDatabase", "users", token.getType());
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView rName, rPosition;
        private final ImageView rAvatar;

        private ViewHolder(@NonNull View itemView){
            super(itemView);
            rName = itemView.findViewById(R.id.tv_name);
            rPosition = itemView.findViewById(R.id.tv_position);
            rAvatar = itemView.findViewById(R.id.imageView);
        }

        public String getPlayerName(){
            return rName.getText().toString();
        }

        public String getPlayerPosition(){
            return rPosition.getText().toString();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.linearlayout_reserved_players, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        //final View shape = holder.imageView;
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final HashMap<String, String> player = playersList.get(holder.getAdapterPosition());
                final View.DragShadowBuilder shadow = new View.DragShadowBuilder(view);
                ViewCompat.startDragAndDrop(view, null, shadow, player, 0);
                return true;
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        HashMap<String, String> player = playersList.get(position);
        //final ReservedPlayersAdapter.ViewHolder viewHolder = (ReservedPlayersAdapter.ViewHolder)convertView.getTag();
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
            rName = player.get("unknown user");
        }
        else{
            rName = playerInfo.get("firstName") + " " + playerInfo.get("lastName");
        }

        viewHolder.rName.setText(rName);
        viewHolder.rPosition.setText(player.get("position"));
        viewHolder.rAvatar.setColorFilter(Color.GRAY);

    }

    @Override
    public int getItemCount() {
        return playersList.size();
    }
}