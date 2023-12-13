package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity2 extends AppCompatActivity {



    private int mAudioSource = MediaRecorder.AudioSource.MIC;
    private int mSampleRate = 44100;
    private int mChannelCount = AudioFormat.CHANNEL_IN_DEFAULT;
    private int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int mBufferSize = 8192;
    AudioTrack audioTrack;
    public AudioRecord mAudioRecord = null;

    static int userlogin;

    InetAddress serverAddrrev;
    String msg;
    Date lastDate;
    Button connectBtn;
    Thread TCPThread;


    EditText ipaddress;
    TextView connectText;
    TCPClient  tcpClient;
    EditText userpass;
    TextView checkin;
    TextView checkout;


    Button onBtn;
    Button offBtn;
    Button callBtn;
    Button changeBtn;
    WebView webView;
    Button backBtn;
    TextView guestinfo1;
    Button checkoutBtn;




    DatagramSocket socket;
    NetworkUDPrev udprev;
    Thread cThreadrev;
    int sec = 1;


    String  pdate;
    int value;
    String ntime1;
    Random createRandom;
    String tommorrow;
    String checkoutt;



    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = new Date();
    Calendar cal = Calendar.getInstance();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            socket.close(); // 소켓 닫음
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart(){
        super.onStart();


    }
    EditText passTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        connectBtn = (Button)findViewById(R.id.connectBtn); // xml있는 오브젝트를 컨트롤하기위해 ID를 가지고옴
        callBtn = (Button)findViewById(R.id.callBtn);
        ipaddress = (EditText)findViewById(R.id.ipaddress);
        connectText = (TextView)findViewById(R.id.connectText);

        webView = (WebView)findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient()); // 이걸 안해주면 새창이 뜸
        passTxt = findViewById(R.id.passTxt);
        changeBtn = findViewById(R.id.changeBtn);
        backBtn = findViewById(R.id.back);
        guestinfo1 = findViewById(R.id.guestinfo1);
        checkoutBtn = findViewById(R.id.checkoutBtn);

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mChannelCount, mAudioFormat, 8192, AudioTrack.MODE_STREAM);

        createRandom = new Random();

        checkin = findViewById(R.id.checkin);
        checkout = findViewById(R.id.checkout);
        cal.setTime(date);

        //callBtn.setVisibility(View.VISIBLE);

        try{
            socket = new DatagramSocket(3333);//소켓통신을 위한 소켓 생성 포트 3333
            Log.e("sock1","soc1k");
        }
        catch (Exception ex) {Log.e("fail","soc1k");}

        SharedPreferences pref1 = getSharedPreferences("SAVEdate", MODE_PRIVATE);
        pdate = pref1.getString("Savedate",simpleDateFormat.format(date));
        checkin.setText(pdate);

        SharedPreferences pref2 = getSharedPreferences("CHECKOUT", MODE_PRIVATE);
        checkoutt = pref2.getString("Checkout",simpleDateFormat.format(date));
        checkout.setText(checkoutt);

        ntime1 = simpleDateFormat.format(date); //현재날짜-임의로설정해둠
        value = ntime1.compareTo(checkoutt)+(-1); //현재날짜와 입실 날짜 비교한 값

        if(value>0 && checkoutt!=" "){
            Toast.makeText(MainActivity2.this, "HOST님 환영합니다.", Toast.LENGTH_SHORT).show();
        }





        udprev = new NetworkUDPrev();//수신객체 선언
        cThreadrev = new Thread(udprev);//수신스레드 선언
        cThreadrev.start();//수신스레드 시작
        callBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(MainActivity2.this, "라즈베리에 비밀번호 전송", Toast.LENGTH_SHORT).show();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    (new Thread(new NetworkUDP("p"+passTxt.getText().toString()))).start(); // o라는 메세지를 라즈베리로 보냄 p2222
                    return true;
                }
                return false;
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref4 = getSharedPreferences("SAVEdate", MODE_PRIVATE);
                SharedPreferences.Editor editor4 = pref4.edit();
                editor4.remove("Savedate");
                editor4.commit();

                checkin.setText(" ");
                checkout.setText(" ");

                SharedPreferences pref8 = getSharedPreferences("SAVEdate", MODE_PRIVATE);
                SharedPreferences.Editor editor8 = pref8.edit();
                editor8.putString("Savedate", " ");
                editor8.commit();

                SharedPreferences pref9 = getSharedPreferences("CHECKOUT", MODE_PRIVATE);
                SharedPreferences.Editor editor9 = pref9.edit();
                editor9.remove("Checkout");
                editor9.commit();

                SharedPreferences pref10 = getSharedPreferences("CHECKOUT", MODE_PRIVATE);
                SharedPreferences.Editor editor10 = pref10.edit();
                editor10.putString("Checkout", " ");
                editor10.commit();

                SharedPreferences pref6 = getSharedPreferences("SAVE", MODE_PRIVATE);
                SharedPreferences.Editor editor6 = pref6.edit();
                editor6.remove("Saveuserpassword");
                editor6.commit();

                userlogin = 1234;

                SharedPreferences pref7 = getSharedPreferences("SAVE", MODE_PRIVATE);
                SharedPreferences.Editor editor7 = pref7.edit();
                editor7.putInt("Saveuserpassword", userlogin);
                editor7.commit();

                (new Thread(new NetworkUDP("p"+userlogin))).start();

            }
        });


        connectBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { // 클릭하면
                Toast.makeText(MainActivity2.this, "연결시도", Toast.LENGTH_SHORT).show();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    (new Thread(new NetworkUDP("o"))).start(); // o라는 메세지를 라즈베리로 보냄

                    connectText.setText("연결 안됨"); // 메세지가 오기전엔 not
                    // connect 키고 ㅔㅁ세지를 받으면 connected 로 표시
                    try{
                        tcpClient = new TCPClient(ipaddress.getText().toString());
                        TCPThread = new Thread(tcpClient);
                        TCPThread.start();

                        Log.e("sock1","soc1k");
                    }
                    catch (Exception ex) {
                        Log.e("TCPCreate", "onTouch: ", ex);
                    }


                    return true;
                }
                return false;
            }
        });

        ntime1 = simpleDateFormat.format(date); //현재 날짜 저장

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //사용자 로그인 비밀번호 변경 및 숙박객 입실 시간 저장
                SharedPreferences pref2 = getSharedPreferences("SAVE", MODE_PRIVATE);
                SharedPreferences.Editor editor2 = pref2.edit();
                editor2.remove("Saveuserpassword");
                editor2.commit();

//                if((userpass.getText().toString().trim()) == "password")
                userlogin = createRandom.nextInt((9999 - 1000) + 1) + 1000; //4자리의 비밀번호 난수 생성
                MainActivity.userlogin = userlogin;
//                Toast.makeText(MainActivity2.this, "라즈베리에 비밀번호 " + userlogin + " 전송", Toast.LENGTH_SHORT).show();



                SharedPreferences pref1 = getSharedPreferences("SAVE", MODE_PRIVATE);
                SharedPreferences.Editor editor1 = pref1.edit();
                editor1.putInt("Saveuserpassword", userlogin);
                editor1.commit();


                SharedPreferences pref4 = getSharedPreferences("SAVEdate", MODE_PRIVATE);
                SharedPreferences.Editor editor4 = pref4.edit();
                editor4.remove("Savedate");
                editor4.commit();

                SharedPreferences pref3 = getSharedPreferences("SAVEdate", MODE_PRIVATE);
                SharedPreferences.Editor editor3 = pref3.edit();
                editor3.putString("Savedate", ntime1);
                editor3.commit();

                Toast.makeText(MainActivity2.this, "새로운 guest 입실 날짜는 " +ntime1 + "입니다.", Toast.LENGTH_SHORT).show();
                checkin.setText(ntime1);
                cal.add(Calendar.MINUTE, +2);
                tommorrow = simpleDateFormat.format(cal.getTime());
                checkout.setText(tommorrow);

                SharedPreferences pref5 = getSharedPreferences("CHECKOUT", MODE_PRIVATE);
                SharedPreferences.Editor editor5 = pref5.edit();
                editor5.remove("Checkout");
                editor5.commit();

                SharedPreferences pref6 = getSharedPreferences("CHECKOUT", MODE_PRIVATE);
                SharedPreferences.Editor editor6 = pref6.edit();
                editor6.putString("Checkout", tommorrow);
                editor6.commit();





                Toast.makeText(MainActivity2.this, "guest 로그인 비밀번호와 도어락 비밀번호를 변경하였습니다.", Toast.LENGTH_SHORT).show();

                (new Thread(new NetworkUDP("p"+userlogin))).start();

            }
        });
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        try {
//            tcpClient.closed = 1;
//            tcpClient.socketTCP.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void openBtn(View view) {
        (new Thread(new NetworkUDP("n"))).start(); // o라는 메세지를 라즈베리로 보냄
    }

//    public void closeBtn(View view) {
//        (new Thread(new NetworkUDP("c"))).start(); // o라는 메세지를 라즈베리로 보냄
//    }

    public class NetworkUDP implements Runnable {//송신클래스
        String msg = "connect";//메세지 초기값  생성되자마자 메세지를 보내줌으로써 상대에게 클라이언트 ip를 알린다
        String serverIP = ipaddress.getText().toString(); //address(text) value save; //serverIP를 추가합니다.
        InetAddress serverAddr;
        NetworkUDP(){}
        NetworkUDP(String _msg)
        {
            this.msg = _msg;
        }
        public void run() {
            try {
                Log.e(msg,msg);
                serverAddr = InetAddress.getByName(serverIP);//서버이름을 얻어온다
                byte[] buf = new byte[20];
                buf = msg.getBytes();
                DatagramPacket Packet = new DatagramPacket(buf, buf.length, serverAddr, 3333);//msg에 담겨있는 메세지와 서버ip와 3333포트로 설정된 메세지를 만든다
                socket.send(Packet);//메세지 전송
            } catch (Exception ex) {

            }
        }
    }

    public class NetworkUDPrev implements Runnable {//수신 클래스
        int flag  = 0;
        public void run() {
            try {
                int port = 3333;
                while (true) {
                    byte[] buf = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddrrev, port);
                    //((MainActivity)getActivity()).socket.receive(packet);//서버와 포트로 만들어진 패킷을 수신한다
                    socket.receive(packet);//서버와 포트로 만들어진 패킷을 수신한다

                    msg = new String(packet.getData(), 0, packet.getLength());


                    switch(msg.toCharArray()[0]) {
                        case 'o': //o라는 메세지를 받으면  연결되었다라고 판단 최종메세지를 받은 시간 기록
                            connectText.post(new Runnable() {
                                public void run() {
                                    connectText.setText("연결됨");
                                    long now = System.currentTimeMillis();
                                    lastDate = new Date(now);
                                }
                            });
                            break;
                        case 'h':
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    guestinfo1.setText("정보 확인");
                                    SharedPreferences pref2 = getSharedPreferences("SAVE", MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = pref2.edit();
                                    editor2.remove("Saveuserpassword");
                                    editor2.commit();

//                if((userpass.getText().toString().trim()) == "password")
                                    userlogin = createRandom.nextInt((9999 - 1000) + 1) + 1000; //4자리의 비밀번호 난수 생성
                                    MainActivity.userlogin = userlogin;
//                                    Toast.makeText(MainActivity2.this, "라즈베리에 비밀번호 " + userlogin + " 전송", Toast.LENGTH_SHORT).show();



                                    SharedPreferences pref1 = getSharedPreferences("SAVE", MODE_PRIVATE);
                                    SharedPreferences.Editor editor1 = pref1.edit();
                                    editor1.putInt("Saveuserpassword", userlogin);
                                    editor1.commit();


                                    SharedPreferences pref4 = getSharedPreferences("SAVEdate", MODE_PRIVATE);
                                    SharedPreferences.Editor editor4 = pref4.edit();
                                    editor4.remove("Savedate");
                                    editor4.commit();

                                    SharedPreferences pref3 = getSharedPreferences("SAVEdate", MODE_PRIVATE);
                                    SharedPreferences.Editor editor3 = pref3.edit();
                                    editor3.putString("Savedate", ntime1);
                                    editor3.commit();

                                    Toast.makeText(MainActivity2.this, "새로운 guest 입실 날짜는 " +ntime1 + "입니다.", Toast.LENGTH_SHORT).show();
                                    checkin.setText(ntime1);
                                    cal.add(Calendar.MINUTE, +2);
                                    tommorrow = simpleDateFormat.format(cal.getTime());
                                    checkout.setText(tommorrow);

                                    SharedPreferences pref5 = getSharedPreferences("CHECKOUT", MODE_PRIVATE);
                                    SharedPreferences.Editor editor5 = pref5.edit();
                                    editor5.remove("Checkout");
                                    editor5.commit();

                                    SharedPreferences pref6 = getSharedPreferences("CHECKOUT", MODE_PRIVATE);
                                    SharedPreferences.Editor editor6 = pref6.edit();
                                    editor6.putString("Checkout", tommorrow);
                                    editor6.commit();


                                    Toast.makeText(MainActivity2.this, "guest 로그인 비밀번호와 도어락 비밀번호를 변경하였습니다.", Toast.LENGTH_SHORT).show();

                                    (new Thread(new NetworkUDP("p"+userlogin))).start();
//
                                }
                            });

                            break;

                    }
                }

            } catch (Exception e) {

            }
            finally{

            }

        }
    }

    public class TCPClient implements Runnable {//송신클래스
        Socket socketTCP;
        String serverIP = ipaddress.getText().toString(); //address(text) value save; //serverIP를 추가합니다.
        InputStream input_data;
        OutputStream output_data;
        int closed = 0;
        public TCPClient(String serverIP) {
            this.serverIP = serverIP;
        }

        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e( "run: ", "Start");

                    Log.e( "run: ", "Starting");
                    byte[] readData = new byte[mBufferSize];
                    while(closed == 0) {
                        send(readData);
                        Log.e( "run: ", "read");
                    }
                }
            }).start();

            try {
                socketTCP = new Socket(serverIP , 3333);
                input_data =socketTCP.getInputStream();
                output_data =socketTCP.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                while (true) {
                    byte buf[] = new byte[8192];
                    input_data.read(buf);

                }
            } catch (Exception ex) {
            }
        }
        public void send(byte[] data)
        {
            try {
                output_data.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class NotifiRunnable implements Runnable
    {
        String mText;

        public NotifiRunnable(String text) {
            mText = text;
        }
        @Override public void run(){


        }
    }

 @Override
    protected void onDestroy() {
        super.onDestroy();
        closeSocket();
    }

    private void closeSocket() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

  
}
