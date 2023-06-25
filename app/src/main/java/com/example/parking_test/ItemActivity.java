package com.example.parking_test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parking_test.databinding.ActivityItemBinding;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ItemActivity extends AppCompatActivity {
    String shared = "file";
    public String ipValue = "";

    private ActivityItemBinding binding;
    private List<Item> itemList;
    private ItemDao itemDao;
    private int i=0;
    private boolean getItem = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_item);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_item);



        Button buttonNewItem = findViewById(R.id.buttonNewItem);
        Button buttonItemReSearch = findViewById(R.id.buttonItemMail);

        refreshIP();
        AppDatabase itemDb = Room.databaseBuilder(this, AppDatabase.class, "Item-db")
                .allowMainThreadQueries()
                .build();
        itemDao = itemDb.itemDao();
        //itemList.clear();
        itemList = itemDao.getAll();

        if (ipValue.equals("")) {

        } else {
            buttonNewItem.setVisibility(View.GONE);
            ClientThread thread = new ClientThread();
            thread.setDaemon(true);
            thread.start();
        }

        buttonNewItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), ItemUpdateActivity.class);
                intent.putExtra("itemId","");
                startActivity(intent);
            }
        });

        buttonItemReSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (ipValue.equals("")) {
                    itemList = itemDao.getAll();
                    binding.recyclerViewItem.setAdapter(new RecyclerviewAdapter());
                } else {
                    ClientThread thread = new ClientThread();
                    thread.setDaemon(true);
                    thread.start();
                    getItem = false;
                    while (getItem = true) {
                        SystemClock.sleep(1000);
                        binding.recyclerViewItem.setAdapter(new RecyclerviewAdapter());
                    }
                }
            }
        });

        binding.recyclerViewItem.setAdapter(new RecyclerviewAdapter());
        binding.recyclerViewItem.setLayoutManager(new LinearLayoutManager(this));
        Toast.makeText(ItemActivity.this, "아이템수 :"+itemList.size(), Toast.LENGTH_SHORT).show();
    }

    public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.ViewHolder>{

        @NonNull
        @Override
        public RecyclerviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerviewAdapter.ViewHolder holder, int position) {
            String reTextViewsItemCode;
            reTextViewsItemCode = itemList.get(position).getItemCode();

            String itemTime = itemList.get(position).getItemTime();

            holder.reTextViewsItemCode.setText((CharSequence) itemList.get(position).getItemCode());
            holder.reTextViewsItemName.setText((CharSequence) itemList.get(position).getItemName());
            holder.reTextViewItemTime.setText(itemTime);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openVideoActivity(reTextViewsItemCode);
                }
            });
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView reTextViewsItemCode;
            private TextView reTextViewsItemName;
            private TextView reTextViewItemTime;
            public ViewHolder(View view){
                super(view);
                reTextViewsItemCode = view.findViewById(R.id.reTextViewsItemCode);
                reTextViewsItemName = view.findViewById(R.id.reTextViewsItemName);
                reTextViewItemTime = view.findViewById(R.id.reTextViewTime);
            }
        }

        public void openVideoActivity(String itemId) {
            if (ipValue.equals("")) {
                Intent intent;
                //변경해야할 부분
                intent = new Intent(getApplicationContext(),ItemUpdateActivity.class);
                intent.putExtra("itemId",itemId);
                startActivity(intent);
            } else {

            }
        }

    }


    private void refreshIP() {
        SharedPreferences sharedPreferences = getSharedPreferences(shared,0);
        ipValue = sharedPreferences.getString("ip","");
    }

    class ClientThread extends Thread {
        @Override
        public void run() {
            String host = ipValue;
            int port = 5001;

            try {

                //itemList.add(new Item("002", "002아이템", 1000, 10000, "002메모", 0, 0));
                //getItem = true;

                Socket socket = new Socket(host, port);
                ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
                outstream.writeObject("item");
                outstream.flush();
                Log.d("ClientStream", "Sent to server");

                ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());
                Object input = instream.readObject();
                Log.d("ClientThread", "Received data: " + input);

                JSONArray jsonArray = new JSONArray(input);
                for(int i = 0; i < jsonArray.length(); i++ ){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String ItemCode = jsonObject.getString("ItemCode");
                    String ItemName = jsonObject.getString("ItemName");
                    String ItemDate = jsonObject.getString("ItemDate");
                    String ItemTime = jsonObject.getString("ItemTime");
                    Item item = new Item(ItemCode, ItemName, ItemDate, ItemTime);
                    itemList.add(item);
                }

                Log.d("ClientThread", "get data end ");
                getItem = true;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
