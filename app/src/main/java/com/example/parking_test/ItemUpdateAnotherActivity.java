package com.example.parking_test;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class ItemUpdateAnotherActivity extends AppCompatActivity {
    private ItemDao itemDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_update_another);

        AppDatabase itemDb = Room.databaseBuilder(this, AppDatabase.class, "Item-db")
                .allowMainThreadQueries()
                .build();
        itemDao = itemDb.itemDao();

        Button buttonItemSave = findViewById(R.id.buttonItemSave);
        Button buttonItemOut = findViewById(R.id.buttonItemOut);
        EditText editTextsItemCode = findViewById(R.id.editTextsItemCode);
        EditText editTextsItemName = findViewById(R.id.editTextsItemName);
        EditText editTextNowDate = findViewById(R.id.editTextNowDate);
        EditText editTextItemTime = findViewById(R.id.editTextItemTime);

        Intent intent = getIntent();
        String sItemCode = intent.getExtras().getString("itemId");

        buttonItemSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // 메세지 출력
                if (editTextsItemCode.getText().length() == 0) {
                    Toast.makeText(ItemUpdateAnotherActivity.this, "코드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (editTextsItemName.getText().length() == 0) {
                    Toast.makeText(ItemUpdateAnotherActivity.this, "차량번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                //변경해야하는부분;

                if (itemDao.countCode(editTextsItemCode.getText().toString()) > 0) {
                    AlertDialog.Builder saveAD = new AlertDialog.Builder(ItemUpdateAnotherActivity.this);
                    saveAD.setIcon(R.mipmap.ic_launcher);
                    saveAD.setTitle("확인");
                    saveAD.setMessage("해당 내용을 업데이트 하시겠습니까?");
                    saveAD.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            itemDao.updateCode(editTextsItemCode.getText().toString()
                                    , editTextsItemName.getText().toString()
                                    //변경해야하는부분 date부분과 Time부분
                                    , editTextNowDate.getText().toString()
                                    , editTextItemTime.getText().toString());
                            Toast.makeText(ItemUpdateAnotherActivity.this, "업데이트 되었습니다.", Toast.LENGTH_SHORT).show();
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
                            , editTextNowDate.getText().toString()
                            , editTextItemTime.getText().toString()
                    );

                    itemDao.insert(item);
                    Toast.makeText(ItemUpdateAnotherActivity.this, "신규 저장되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonItemOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                itemDao.deleteCode(editTextsItemCode.getText().toString());
                Toast.makeText(ItemUpdateAnotherActivity.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
