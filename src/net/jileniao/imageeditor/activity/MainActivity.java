package net.jileniao.imageeditor.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import net.jileniao.imageeditor.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void mask(View v) {
        Intent intent = new Intent(this, MaskActivity.class);
        startActivity(intent);
    }

    public void demask(View v) {
        Intent intent = new Intent(this, DeMaskActivity.class);
        startActivity(intent);
    }
}
