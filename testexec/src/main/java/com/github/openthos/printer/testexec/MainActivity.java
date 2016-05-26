package com.github.openthos.printer.testexec;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView cmd_textView;
    private Button cmd_button;
    private EditText cmd_editText;
    private ScrollView cmd_scrollView;
    private Runnable scrollRunnable;
    private Button stop_button;
    private Process p = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void init() {
        initView();
    }

    private class InRunnable implements Runnable {

        private final InputStream in;

        public InRunnable(InputStream in) {
            this.in = in;
        }

        @Override
        public void run() {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.in));
            String line = null;
            try {
                    while ((line = in.readLine()) != null) {
                        //MainActivity.this.runOnUiThread(new SendLine(line));
                            Log.d(TAG, line + "\n");
                    }

            } catch (IOException e) {
                e.printStackTrace();
            }

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cmd_editText.setEnabled(true);
                }
            });

        }
    }


    private void initView() {
        cmd_textView = (TextView) findViewById(R.id.cmd_textView);
        cmd_editText = (EditText) findViewById(R.id.cmd_editText);
        cmd_button = (Button) findViewById(R.id.cmd_button);
        stop_button = (Button) findViewById(R.id.stop_button);
        cmd_scrollView = (ScrollView) findViewById(R.id.cmd_scrollView);

        cmd_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();

            }
        });

        stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(p != null) {
                    p.destroy();
                }
            }
        });

        cmd_editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {

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


        scrollRunnable = new Runnable() {

            @Override
            public void run() {
                cmd_scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        };

    }

    private void send() {

        String command = cmd_editText.getText().toString();


        if (command.equals("")) {
            return;
        }

        println("# " + command);
        cmd_editText.setText("");

        cmd_editText.setEnabled(false);

        try {

            p = Runtime.getRuntime().exec(command, null, MainActivity.this.getFilesDir());
            InputStream in = p.getInputStream();
            OutputStream out = p.getOutputStream();
            InputStream err = p.getErrorStream();



            new Thread(new InRunnable(in)).start();
            new Thread(new InRunnable(err)).start();


        } catch (IOException e) {
            e.printStackTrace();
            cmd_editText.setEnabled(true);
        }

    }

    private void printf(CharSequence text) {
        if (text == null) {
            return;
        }
        cmd_textView.setText(cmd_textView.getText().toString() + text.toString());
    }

    private void println(CharSequence text) {
        if (text == null) {
            text = "";
        }
        printf(text + "\n");

        new Handler().postDelayed(scrollRunnable, 500);

    }

    private void clear() {
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
