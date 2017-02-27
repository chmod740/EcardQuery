package me.hupeng.yy.ecardquery;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EcardQuery ecardQuery = new EcardQuery();

        ecardQuery.queryAccountBiils("0141120997", "314132", new AccountBillListener() {
            @Override
            public void done(List<EcardQuery.AccountBill> accountBills, Exception e) {
                if (e == null){
                    Message message = new Message();
                    message.what = 1;
                    message.obj = accountBills;
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==1){
                List<EcardQuery.AccountBill> list= (List<EcardQuery.AccountBill>)msg.obj;
                for(int i = 0 ; i < list.size() ; i ++){
                    Toast.makeText(MainActivity.this, list.get(0).toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

}
