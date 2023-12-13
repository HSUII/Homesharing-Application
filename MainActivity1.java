package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class MainActivity extends AppCompatActivity {
    Boolean mset = false;
    Boolean uset = false;
    static int userlogin ;
    String  pdate; //숙박날짜
    int value; //현재날짜 숙박날짜 비교값
    String ntime1; //현재 날짜
    String checkout;



    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = new Date();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences pref3 = getSharedPreferences("SAVE", MODE_PRIVATE); //숙박객 로그인 비밀번호 불러오기
        userlogin = pref3.getInt("Saveuserpassword", Integer.parseInt("1234"));



        SharedPreferences pref2 = getSharedPreferences("CHECKOUT", MODE_PRIVATE);
        checkout = pref2.getString("Checkout",simpleDateFormat.format(date));
//        pdate = "2021-05-23 00:58:00";

        SharedPreferences pref1 = getSharedPreferences("SAVEdate", MODE_PRIVATE);
        pdate = pref1.getString("Savedate",simpleDateFormat.format(date));


        ntime1 = simpleDateFormat.format(date); //현재날짜-임의로설정해둠
        value = ntime1.compareTo(checkout)+(-1); //현재날짜와 입실 날짜 비교한 값




        final EditText pass= findViewById(R.id.pass);

        Button login = findViewById(R.id.login); //로그인 버튼
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mset == true && uset == false) { //관리자버튼만 on되있을 경우
                    if(value>0){
                    if (Integer.parseInt(pass.getText().toString().trim()) == 0000) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "관리자 비밀번호가 틀렸습니다.  다시한번 확인해 주세요", Toast.LENGTH_SHORT).show();
                    }
                    }
                    if(value<=0){
                        Toast.makeText(MainActivity.this, "게스트가 체크아웃한 이후 로그인 할 수 있습니다", Toast.LENGTH_SHORT).show();
                    }
                }
                

                if(uset == true && mset == false  ) { //숙박객버튼만 on되있고, 입실날짜가 3일이 지나지 않은 경우
                    if (Integer.parseInt(pass.getText().toString().trim()) == userlogin) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity3.class);
                        startActivity(intent);
                        Toast.makeText(MainActivity.this, "GUEST님 환영합니다", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "guest 비밀번호가 틀렸습니다. 다시한번 확인해 주세요", Toast.LENGTH_SHORT).show();
                    }
                }

                if(mset == true && uset == true) { //관리자, 숙박객 둘다 on 되있을 경우
                    Toast.makeText(MainActivity.this, "host와 guest 중 하나만 선택해 주세요", Toast.LENGTH_SHORT).show();
                }

                if(mset == false && uset == false) { //관리자, 숙박객 둘다 off 되있을 경우
                    Toast.makeText(MainActivity.this, "host와 guest 중 하나를 선택해 주세요", Toast.LENGTH_SHORT).show();
                }

            }
        });

        final ImageButton bt1 = findViewById(R.id.imageButton);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //관리자 로그인버튼 클릭시
                mset = !mset;
                if(mset == true){
                bt1.setImageResource(R.drawable.on);}
                else if(mset ==false)
                {bt1.setImageResource(R.drawable.off);}
            }
        });



        final ImageButton bt2 = findViewById(R.id.imageButton2);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //숙박객 로그인 버튼 클릭시
                uset = !uset;
                if(uset == true ){
                    bt2.setImageResource(R.drawable.on);
                    if(value>0) {
                        Toast.makeText(MainActivity.this, "checkout시간이 지나 로그인할 수 없습니다", Toast.LENGTH_SHORT).show();
                        SharedPreferences pref4 = getSharedPreferences("SAVE", MODE_PRIVATE);
                        SharedPreferences.Editor editor4 = pref4.edit();
                        editor4.remove("Saveuserpassword");
                        editor4.commit();

                        userlogin = 1234;

                        SharedPreferences pref1 = getSharedPreferences("SAVE", MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = pref1.edit();
                        editor1.putInt("Saveuserpassword", userlogin);
                        editor1.commit();
                    }

                }
                else if(uset == false)
                {bt2.setImageResource(R.drawable.off);}
            }
        });

    }
}
