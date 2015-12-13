package com.termproject.termproject.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.termproject.termproject.R;
import com.termproject.termproject.main.MainActivity;
import com.termproject.termproject.manager.ClientManager;
import com.termproject.termproject.manager.GameManager;

/**
 * Created by kk070 on 2015-12-13.
 */
public class MenuActivity extends Activity {
    private GameManager gameManager;
    private ClientManager clientManager;

    private Button button1;
    private Button button2;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameManager = GameManager.getInstance();
        clientManager = ClientManager.getFirstInstance(this);

        setContentView(R.layout.main);

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
                clientManager.setInfo(editText.getText().toString());
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });
    }

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
