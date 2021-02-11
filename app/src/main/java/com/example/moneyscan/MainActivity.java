package com.example.moneyscan;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private SurfaceView surfaceView;

    private CameraSource cameraSource;
    private TextRecognizer textRecognizer;

    private String stringResult = null;

    private String vegeredmeny = "";

    Database database;

    boolean mehet = true;

    Date date = new Date();
    int ideiEv  = Integer.parseInt(DateFormat.format("yyyy", date.getTime()).toString());


    String currentVersion = "1.7";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{CAMERA}, PackageManager.PERMISSION_GRANTED);

        database = new Database(this);

        versionCheck();
    }

    void versionCheck(){
        versionCheck v = new versionCheck();
        v.execute("https://fullbaro.com/moneyupload/version.html");
        try{
            if(!v.get().equals(currentVersion)){
                String text = textView.getText().toString();
                text += "<br><font color='red'>Új verzió érhető el!</font>";
                textView.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraSource.release();
    }

    private void textRecognizer(){
        if(mehet) {
            if (textRecognizer == null) {
                textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                        .setRequestedPreviewSize(1280, 1024)
                        .setAutoFocusEnabled(true)
                        .build();
            }

            surfaceView = findViewById(R.id.surfaceView);
            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @SuppressLint("MissingPermission")
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        cameraSource.start(surfaceView.getHolder());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });


            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {

                    SparseArray<TextBlock> sparseArray = detections.getDetectedItems();
                    StringBuilder stringBuilder = new StringBuilder();

                    for (int i = 0; i < sparseArray.size(); ++i) {
                        TextBlock textBlock = sparseArray.valueAt(i);
                        if (textBlock != null && textBlock.getValue() != null) {
                            stringBuilder.append(textBlock.getValue() + " ");
                        }
                    }

                    final String stringText = stringBuilder.toString();

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                                stringResult = stringText;
                                resultObtained();
                        }
                    });
                }
            });
        }
    }

    public boolean letezik(String kod){
        boolean re = false;
        if(kod.startsWith("A") || kod.startsWith("B") || kod.startsWith("C") || kod.startsWith("D") || kod.startsWith("E") || kod.startsWith("F") || kod.startsWith("G") || kod.startsWith("I"))
            re = true;
        return re;
    }

    private void resultObtained(){
        Log.e("Ezt találtam: ",stringResult);

        // A kód megtalálása
        String kod = "";
        String ev = "";
        String regex = "[A-Z]{2}\\s*[0-9]{7}";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(stringResult);

        while (matcher.find())
            kod = matcher.group();
        if(kod.length() < 10 && kod.length() > 0)
            kod = kod.substring(0,2)+" "+kod.substring(2);
        if(kod.length() > 0 && letezik(kod)){ // ha van kód és érvényes
            // évszám megtalál
            regex = "EST\\s*[0-9]{4}";
            pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(stringResult);
            while (matcher.find())
                ev = matcher.group();
            if(ev.length() > 0) // létezik
                if(Integer.parseInt(ev.substring(ev.length()-4)) >= 2014 && Integer.parseInt(ev.substring(ev.length()-4)) <= ideiEv) //újabb mint 2015, de nem újabb mint mostani év
                    ev = "-" + ev.substring(ev.length() - 4);
                else
                    ev = chooseYear();
            else
                ev = chooseYear(); // ha nem talált évszámot ki kell választani

            //kiír
            vegeredmeny = kod+ev;
            show();
        }else {// csináld újra
            //toast("Nem találtam kódot");
            buttonStart(surfaceView);
        }
    }

    public void show(){
        Log.e("Ez írtam fel", vegeredmeny);
        if(vegeredmeny.length() == 15){
            setContentView(R.layout.activity_main);
            textView = findViewById(R.id.textView);
            textView.setText(vegeredmeny);
            database.send(vegeredmeny);
        }
    }


    public String chooseYear(){
        mehet = false;
        final String[] re = {""};
        String[] years = {"2021", "2020", "2019", "2018", "2017", "2016", "2015", "2014"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Milyen évjárat?");
        builder.setItems(years, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                re[0] = years[which];
                vegeredmeny += "-"+years[which];
                show();
                mehet = true;
            }
        });
        builder.show();

        return re[0];
    }

    public void buttonStart(View view){
        setContentView(R.layout.surfaceview);
        textRecognizer();
    }

    public void toast(String s){
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
    }
}