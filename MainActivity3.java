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
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity3 extends AppCompatActivity {



    private int mAudioSource = MediaRecorder.AudioSource.MIC;
    private int mSampleRate = 44100;
    private int mChannelCount = AudioFormat.CHANNEL_IN_DEFAULT;
    private int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int mBufferSize = 8192;
    AudioTrack audioTrack;
    public AudioRecord mAudioRecord = null;



    InetAddress serverAddrrev;
    String msg;
    Date lastDate;
    Button connectBtn;
    Thread TCPThread;
    Button backBtn;

    EditText ipaddress;
    TextView connectText;
    TCPClient  tcpClient;


    Button onBtn;
    Button offBtn;
    Button callBtn;
    WebView webView;

    TextView checkin;
    TextView checkout;
    Calendar cal = Calendar.getInstance();




    DatagramSocket socket;
    NetworkUDPrev udprev;
    Thread cThreadrev;
    int sec = 1;

    String ntime1;
    String tommorrow;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = new Date();

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
        setContentView(R.layout.activity_main3);

        Intent h = getIntent();

        connectBtn = (Button)findViewById(R.id.connectBtn); // xml있는 오브젝트를 컨트롤하기위해 ID를 가지고옴
        callBtn = (Button)findViewById(R.id.callBtn);
        ipaddress = (EditText)findViewById(R.id.ipaddress);
        connectText = (TextView)findViewById(R.id.connectText);

        webView = (WebView)findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient()); // 이걸 안해주면 새창이 뜸
        passTxt = findViewById(R.id.passTxt);
        backBtn = findViewById(R.id.back);

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mChannelCount, mAudioFormat, 8192, AudioTrack.MODE_STREAM);

        checkin = findViewById(R.id.checkin);
        checkout = findViewById(R.id.checkout);
        cal.setTime(date);

        //callBtn.setVisibility(View.VISIBLE);

        try{
            socket = new DatagramSocket(3333);//소켓통신을 위한 소켓 생성 포트 3333
            Log.e("sock1","soc1k");
        }
        catch (Exception ex) {Log.e("fail","soc1k");}

        SharedPreferences pref3 = getSharedPreferences("SAVEdate", MODE_PRIVATE);
        SharedPreferences.Editor editor3 = pref3.edit();
//        editor3.putString("Savedate", ntime1);
//        editor3.commit();

        ntime1 = pref3.getString("Savedate", "_");

        checkin.setText(ntime1);
//        checkout.setText("2021-06-16 15:00:00");
        SharedPreferences pref2 = getSharedPreferences("CHECKOUT", MODE_PRIVATE);
        checkout.setText(pref2.getString("Checkout",simpleDateFormat.format(date)));

//        cal.add(Calendar.DATE, +1);
//        tommorrow = simpleDateFormat.format(cal.getTime());
//        checkout.setText(tommorrow);


        udprev = new NetworkUDPrev();//수신객체 선언
        cThreadrev = new Thread(udprev);//수신스레드 선언
        cThreadrev.start();//수신스레드 시작
        callBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(MainActivity3.this, "라즈베리에 비밀번호 전송", Toast.LENGTH_SHORT).show();
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


        connectBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { // 클릭하면
                Toast.makeText(MainActivity3.this, "연결시도", Toast.LENGTH_SHORT).show();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    (new Thread(new NetworkUDP("o"))).start(); // o라는 메세지를 라즈베리로 보냄
//                    webView.loadUrl("http://"+ipaddress.getText().toString()+":8080/?action=stream"); // 스트림 주소를 웹뷰에 등록
                    connectText.setText("연결 성공"); // 메세지가 오기전엔 not connect 키고 ㅔㅁ세지를 받으면 connected 로 표시
                    try{
                        tcpClient = new TCPClient(ipaddress.getText().toString());
//                        TCPThread = new Thread(tcpClient);
//                        TCPThread.start();

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
//
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
//                    if(mAudioRecord == null) {
//////                        mAudioRecord =  new AudioRecord(mAudioSource, mSampleRate, mChannelCount, mAudioFormat, mBufferSize);
//////                        mAudioRecord.startRecording();
////                    }
                    Log.e( "run: ", "Starting");
                    byte[] readData = new byte[mBufferSize];
                    while(closed == 0) {
//                        mAudioRecord.read(readData, 0, mBufferSize);
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
//                    audioTrack.write(buf, 0, buf.length);
//                    audioTrack.play();
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



}
