package com.example.parking_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    String shared = "file";
    public String ipValue = "";

    ServerSocket serversocket;
    Socket socket;

    private List<Item> itemList;
    private ItemDao itemDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refreshIP();
        if (ipValue.equals("")) {
            AppDatabase itemDb = Room.databaseBuilder(this, AppDatabase.class, "Item-db")
                    .allowMainThreadQueries()
                    .build();
            itemDao = itemDb.itemDao();
            //itemList.clear();
            //itemList = itemDao.getAll();

            ServerThread thread = new ServerThread();
            thread.setDaemon(true);
            thread.start();
            Toast.makeText(MainActivity.this, "서버기능이 실행되었습니다.", Toast.LENGTH_SHORT).show();
        }

        Button buttonItem = findViewById(R.id.buttonItem);
        Button buttonLog = findViewById(R.id.buttonLog);
        Button buttonItemLedger = findViewById(R.id.buttonItemLedger);

        //버튼 이벤트
        buttonItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ItemActivity.class);
                startActivity(intent);
            }
        });


        buttonLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LogActivity.class);
                startActivity(intent);
            }
        });

        buttonItemLedger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshIP();
                if (ipValue.equals("")) {
                    Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "IP 입력시 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void refreshIP() {
        SharedPreferences sharedPreferences = getSharedPreferences(shared,0);
        ipValue = sharedPreferences.getString("ip","");
    }

    class ServerThread extends Thread {
        @Override
        public void run() {
            int port = 5001;

            try {
                ServerSocket server = new ServerSocket(port);
                Log.d("ServerThread", "Server Started.");

                while(true){
                    Socket socket = server.accept();

                    ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());
                    Object input = instream.readObject();
                    Log.d("ServerThread", "input: " + input);

                    if (input.equals("car")) {
                        ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
                        itemList = itemDao.getAll();
                        JSONArray jArray = new JSONArray();//배열이 필요할때
                        for (int i = 0; i < itemList.size(); i++) {
                            JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
                            sObject.put("ItemCode", itemList.get(i).getItemCode());
                            sObject.put("ItemName", itemList.get(i).getItemName());
                            sObject.put("ItemDate", itemList.get(i).getItemDate());
                            sObject.put("ItemTime", itemList.get(i).getItemTime());
                            jArray.put(sObject);
                        }
                        outstream.writeObject(jArray);
                        outstream.flush();
                        Log.d("ServerThread", "output sent.");

                    } else {
                        ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
                        outstream.writeObject(input + " from server.");
                        outstream.flush();
                        Log.d("ServerThread", "output sent.");
                    }
                    socket.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
