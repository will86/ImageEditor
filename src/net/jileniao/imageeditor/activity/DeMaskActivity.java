package net.jileniao.imageeditor.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import net.jileniao.imageeditor.R;
import net.jileniao.imageeditor.view.DeMaskView;

public class DeMaskActivity extends BaseActivity<DeMaskView> {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demask);
        mView = (DeMaskView) findViewById(R.id.my_slider);
        mSaveBtn = (Button) findViewById(R.id.button_resolve);
        mSaveBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                save();
            }
        });
    }
}
