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

    private final HashMap<String,HashMap<String,String>> usersDatabase;

    private static final String TAG = "ReservedPlayersAdapter";

    public ReservedPlayersAdapter(Context context, ArrayList<HashMap<String, String>> list){
        this.mContext = context;
        this.playersList =list;
        TypeToken<HashMap<String,HashMap<String,String>>> token = new TypeToken<HashMap<String,HashMap<String,String>>>(){};
        this.usersDatabase = SharedPreferenceHandler.getSavedObjectFromPreference(mContext, "UsersDatabase", "users", token.getType());
    }

//    private class ViewHolder extends RecyclerView.ViewHolder {
//        private ImageView imageView;
//        private TextView textView;
//
//        private ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            imageView = itemView.findViewById(R.id.imageView2);
//            textView = itemView.findViewById(R.id.textView);
//        }
//    }

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
                //final DragData state = new DragData(item, shape.getWidth(), shape.getHeight());
                final View.DragShadowBuilder shadow = new View.DragShadowBuilder(view);
                HashMap<String, String> playerInfo = usersDatabase.getOrDefault((player.get("userID")), null);
                UserInfo playerData;

                if(playerInfo != null) {
                    playerData = new UserInfo(
                            Integer.valueOf(playerInfo.get("entryID")),
                            playerInfo.get("firstName"),
                            playerInfo.get("lastName"),
                            Integer.valueOf(playerInfo.get("batch")),
                            playerInfo.get("contactNum"),
                            playerInfo.get("username"));
                }
                else{
                    playerData = new UserInfo(-1, holder.rName.getText().toString(), "", -1, "", "");
                }

                DragData data = new DragData(playerData, player.get("position"));
                ViewCompat.startDragAndDrop(view, null, shadow, data, 0);
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
            rName = player.get("userID") + " (G)";
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