package com.github.openthos.printer.testexec;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private TextView cmd_textView;
    private Button cmd_button;
    private EditText cmd_editText;
    private ScrollView cmd_scrollView;
    private Runnable scrollRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    private void init() {
        initView();



    }

    private void initView() {
        cmd_textView = (TextView)findViewById(R.id.cmd_textView);
        cmd_editText = (EditText)findViewById(R.id.cmd_editText);
        cmd_button = (Button)findViewById(R.id.cmd_button);
        cmd_scrollView = (ScrollView)findViewById(R.id.cmd_scrollView);

        cmd_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();

            }
        });

        cmd_editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            cmd_button.callOnClick();
                        }
                    });
                    return true;
                }
                return false;
            }
        });


        scrollRunnable = new Runnable(){

            @Override
            public void run() {
                cmd_scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        };

    }

    private void send() {

        String command = cmd_editText.getText().toString();


        if(command.equals("")){
            return;
        }

        println("# " + command);
        cmd_editText.setText("");

        try {
            final Process p = Runtime.getRuntime().exec(command, null, this.getFilesDir());

            new Thread(new Runnable() {
                @Override
                public void run() {

                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = null;
                    try {
                        while((line = in.readLine()) != null){
                            MainActivity.this.runOnUiThread(new SendLine(line));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void printf(CharSequence text){
        if(text == null){
            return;
        }
        cmd_textView.setText(cmd_textView.getText().toString() + text.toString());
    }

    private void println(CharSequence text){
        if(text == null){
            text = "";
        }
        printf(text + "\n");

        new Handler().postDelayed(scrollRunnable, 500);

    }

    private void clear(){
        cmd_textView.setText("");
    }

    private class SendLine implements Runnable {

        String line;

        public SendLine(String line) {
            this.line = line;
        }

        @Override
        public void run() {
            println(line);
        }
    }
}
