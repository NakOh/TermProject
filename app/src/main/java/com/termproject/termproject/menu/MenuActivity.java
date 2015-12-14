package com.termproject.termproject.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.termproject.termproject.R;
import com.termproject.termproject.main.MainActivity;
import com.termproject.termproject.manager.TCPManager;
import com.termproject.termproject.manager.GameManager;

/**
 * Created by kk070 on 2015-12-13.
 */
public class MenuActivity extends Activity {
    private GameManager gameManager;
    private TCPManager TCPManager;

    private Button button1;
    private Button button2;
    private EditText editText;

    private RadioButton option1;
    private RadioButton option2;
    private RadioButton option3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameManager = GameManager.getInstance();
        TCPManager = TCPManager.getFirstInstance(this);

        setContentView(R.layout.main);

        option1 = (RadioButton) findViewById(R.id.radioButton1);
        option2 = (RadioButton) findViewById(R.id.radioButton2);
        option3 = (RadioButton) findViewById(R.id.radioButton3);
        option1.setOnClickListener(optionOnClickListener);
        option2.setOnClickListener(optionOnClickListener);
        option3.setOnClickListener(optionOnClickListener);
        option2.setChecked(true);
        gameManager.setDifficulty(1);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        editText = (EditText) findViewById(R.id.editText);

        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gameManager.setMulti(false);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gameManager.setMulti(true);
                //멀티 버튼을 누르면 일단 연결을 시도합니다. 소켓을 만들어 연결을 시도하여 실패할 시 자기 자신이 서버를 만들어냅니다.
                TCPManager.setInfo(editText.getText().toString());
                if (TCPManager.getConnect() != null) {
                    try {
                        TCPManager.getConnect().join();
                        System.out.println("연결 완료");
                        TCPManager.getRecvSocket().start();
                        System.out.println("Client 받는 소켓 계쏙 작동");
                    } catch (Exception e) {
                    }
                }
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });
    }

    RadioButton.OnClickListener optionOnClickListener
            = new RadioButton.OnClickListener() {

        public void onClick(View v) {
            if (option1.isChecked()) {
                gameManager.setDifficulty(0);
            } else if (option2.isChecked()) {
                gameManager.setDifficulty(1);
            } else if (option3.isChecked()) {
                gameManager.setDifficulty(2);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
