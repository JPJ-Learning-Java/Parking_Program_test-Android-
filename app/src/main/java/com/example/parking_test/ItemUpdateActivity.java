package com.example.parking_test;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ItemUpdateActivity extends AppCompatActivity {
    private ItemDao itemDao;
    private List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_update);

        AppDatabase itemDb = Room.databaseBuilder(this, AppDatabase.class, "Item-db")
                .allowMainThreadQueries()
                .build();
        itemDao = itemDb.itemDao();

        Button buttonItemSave = findViewById(R.id.buttonItemSave);
        Button buttonItemOut = findViewById(R.id.buttonItemOut);
        Button buttonItemComplete = findViewById(R.id.buttonItemComplete);

        EditText editTextsItemCode = findViewById(R.id.editTextsItemCode);
        EditText editTextsItemName = findViewById(R.id.editTextsItemName);
        EditText editTextItemTime = findViewById(R.id.editTextItemTime);
        EditText editTextOutTime = findViewById(R.id.editTextOutTime);

        EditText editTextAmount = findViewById(R.id.editTextAmount);

        Intent intent = getIntent();
        String sItemCode = intent.getExtras().getString("itemId");
        if(sItemCode.equals("")){
            if (itemDao.countCode() > 0)
                editTextsItemCode.setText(Integer.toString(Integer.parseInt(itemDao.newCode()) + 1));
        }else {
            //업데이드 후 보여주는 데이터
            //timeThread = new Thread(new timeThread());
            editTextsItemCode.setText(sItemCode);
            itemList = itemDao.selectCode(editTextsItemCode.getText().toString());
            editTextsItemName.setText(itemList.get(0).getItemName());
            //update에 표기됨
            editTextOutTime.setText(itemList.get(0).getItemOutTime());
            //변경한 부분
            if (!itemList.isEmpty()) {
                Item firstItem = itemList.get(0);
                String itemTime = firstItem.getItemTime(); // getItemTime() 메서드로 시간 값을 가져옵니다.
                editTextItemTime.setText(itemTime);
            }
            if (!itemList.isEmpty()) {
                Item firstItem = itemList.get(0);
                String itemAmount = firstItem.getItemAmount(); // getItemAmount() 메서드로 시간 값을 가져옵니다.
                editTextAmount.setText(itemAmount);
            }
        }

        buttonItemSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // 메세지 출력
                if (editTextsItemCode.getText().length() == 0) {
                    Toast.makeText(ItemUpdateActivity.this, "코드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (editTextsItemName.getText().length() == 0) {
                    Toast.makeText(ItemUpdateActivity.this, "차량번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                //변경해야하는부분;

                if (itemDao.countCode(editTextsItemCode.getText().toString()) > 0) {
                    AlertDialog.Builder saveAD = new AlertDialog.Builder(ItemUpdateActivity.this);
                    saveAD.setIcon(R.mipmap.ic_launcher);
                    saveAD.setTitle("확인");
                    saveAD.setMessage("해당 내용을 업데이트 하시겠습니까?");
                    saveAD.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            itemDao.updateCode(editTextsItemCode.getText().toString()
                                    , editTextsItemName.getText().toString()
                                    //변경해야하는부분 date부분과 Time부분
                                    , getDate()
                                    , getTime()
                                    , editTextOutTime.getText().toString()
                                    , editTextAmount.getText().toString()
                            );
                            editTextItemTime.setText(getTime());
                            Toast.makeText(ItemUpdateActivity.this, "업데이트 되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    saveAD.setNegativeButton("아니요.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    saveAD.show();
                } else {
                    Item item = new Item(editTextsItemCode.getText().toString()
                            , editTextsItemName.getText().toString()
                            //변경해야하는부분
                            , getDate()
                            , getTime()
                            , editTextOutTime.getText().toString()
                            , editTextAmount.getText().toString()
                    );
                    editTextItemTime.setText(getTime());
                    itemDao.insert(item);

                    Toast.makeText(ItemUpdateActivity.this, "신규 저장되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //출차 버튼 코드
        buttonItemComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String itemCode = editTextsItemCode.getText().toString();
                String itemGetTime = itemList.get(0).getItemTime();
                String itemOutTime = outTime();

                String itemAmount;

                try {
                    itemAmount = amount(itemGetTime, itemOutTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                    itemAmount = "Error";
                }

                // 기존 데이터 조회
                Item updateTime = itemDao.getItemByCode(itemCode);

                // 데이터 수정
                if (updateTime != null) {
                    updateTime.setItemTime(itemOutTime);

                    // 수정된 데이터 업데이트
                    itemDao.updateTimeCode(itemCode, itemOutTime);
                    itemDao.updateAmountCode(itemCode, itemAmount);
                }

                editTextAmount.setText(itemAmount);

                editTextOutTime.setText(itemOutTime);

                Toast.makeText(ItemUpdateActivity.this, "출차 되었습니다.", Toast.LENGTH_SHORT).show();

            }
        });

        buttonItemOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                    itemDao.deleteCode(editTextsItemCode.getText().toString());

                    Toast.makeText(ItemUpdateActivity.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    //현재 날짜 함수
    private String getDate(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat nowDate = new SimpleDateFormat("yyyy-MM-dd");

        String getDate = nowDate.format(date);

        return getDate;
    }

    //출입시간 함수
    private String getTime(){
        long now = System.currentTimeMillis();;
        Date time = new Date(now);
        SimpleDateFormat nowTime = new SimpleDateFormat("hh:mm");

        String getTime = nowTime.format(time);

        return getTime;
    }

    //출차시간 함수
    private String outTime(){
        long now = System.currentTimeMillis();;
        Date time = new Date(now);
        SimpleDateFormat nowTime = new SimpleDateFormat("hh:mm");

        String outTime = nowTime.format(time);

        return outTime;
    }

    public String amount(String getTime, String outTime) throws ParseException {

        // 두 개의 문자열을 Date 형식으로 변환
        SimpleDateFormat f = new SimpleDateFormat("hh:mm", Locale.KOREA);
        Date d1 = f.parse(getTime);
        Date d2 = f.parse(outTime);

        // 두 날짜 사이의 차이 계산
        long diff = d2.getTime() - d1.getTime();
        long lastDiff = diff/10;

        //Log.d("Time", String.valueOf(lastDiff));

        // 결과물인 시간을 받아서 금액으로 변환
        int hourlyRate = 1000; //시간당 요금
        int parkingTime = (int) (lastDiff / (60 * 100));
        int roundedTime = (parkingTime / 30) * 30;
        //Log.d("parkTime", String.valueOf(parkingTime));
        //Log.d("roundTime", String.valueOf(roundedTime));
        int totalPay = 1000;
        for( int i = 0; i < roundedTime; i += 30)
        {
            totalPay += hourlyRate;
        }
        String formattedPay = String.format("%d", totalPay);

        /*
        // 결과를 시간 형태인 hh:mm으로 변환하여 반환
        int hour = (int) (lastDiff / (60 * 60 * 100));
        int minute = (int) ((lastDiff / 60 - (hour * 60 * 100))/100);
        String formattedTime = String.format("%02d:%02d", hour, minute);
         */

        // 결과를 다시 String 형태로 변환하여 반환
        return formattedPay;
    }
}