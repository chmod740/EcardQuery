package me.hupeng.yy.ecardquery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
//        ecardQuery.login("0141122299", "030023");
        ecardQuery.login("0141120997", "314132");
    }
}
