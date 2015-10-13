package net.jileniao.imageeditor.activity;

import net.jileniao.imageeditor.R;
import net.jileniao.imageeditor.util.BitmapUtils;
import net.jileniao.imageeditor.view.BaseMaskView;
import net.jileniao.imageeditor.view.ColorPickerDialog;
import net.jileniao.imageeditor.view.ColorPickerDialog.OnColorChangedListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MaskActivity extends BaseActivity<BaseMaskView> {

    private static final int PICK_PIC = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mask);
        mSaveBtn = (Button) findViewById(R.id.button_resolve);
        mView = (BaseMaskView) findViewById(R.id.my_slider);
        // mView.setOnLongClickListener(new OnLongClickListener() {
        //
        // @Override
        // public boolean onLongClick(View v) {
        // return false;
        // }
        // });
        registerForContextMenu(mSaveBtn);

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PIC);

        mSaveBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {

        if (v == mSaveBtn) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.mask_menu, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.mask_color:
            ColorPickerDialog dialog = new ColorPickerDialog(this,
                    new OnColorChangedListener() {

                        @Override
                        public void colorChanged(int color) {
                            mView.changeColor(color);
                        }
                    }, 0);
            dialog.show();
            break;

        case R.id.mask_width:
            break;

        case R.id.mask_text:
            break;

        case R.id.mask_clear:
            break;

        default:
            break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "result not ok", Toast.LENGTH_SHORT).show();
            return;
        }
        if (data == null) {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (requestCode) {
        case PICK_PIC:
            Bitmap bitmap = BitmapUtils.getBitmapFromUri(this, data.getData());
            mView.changeBackground(bitmap);
            break;

        default:
            break;
        }
    }
}
