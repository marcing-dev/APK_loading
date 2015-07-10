package pl.com.marcing.android.customdex;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by MarcinG on 27-10-2014.
 */
public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        Log.i("TestActivity", "::onStart");
        super.onStart();
        method();
    }

    @Override
    protected void onDestroy() {
        Log.i("TestActivity", "::onDestroy");
        super.onDestroy();
    }

    public void method() {
        ArrayList<String> test = new ArrayList<String>();
        test.add("test value");
        Log.i("TestActivity", "action inside TestActivity");
    }
}
