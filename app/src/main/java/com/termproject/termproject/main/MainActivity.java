package com.termproject.termproject.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.termproject.termproject.R;
import com.termproject.termproject.manager.ClientManager;
import com.termproject.termproject.manager.GameManager;

public class MainActivity extends Activity {
    private MainView mainView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //View를 직접 만들어 셋팅한다.
        GameManager.getInstance();
        ClientManager.getFirstInstance(this);
        mainView = new MainView(this);
        setContentView(mainView);
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

    public void dialogSimple(){
        final Activity activity = this;
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setMessage("게임을 다시 시작하시겠습니까?").setCancelable(false).setPositiveButton("네",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        }).setNegativeButton("프로그램 종료", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Action for 'NO' Button
                dialog.cancel();
                moveTaskToBack(true);finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }


    @Override
    protected void onPause(){ super.onPause();    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onDestroy(){ super.onDestroy();  }
}
