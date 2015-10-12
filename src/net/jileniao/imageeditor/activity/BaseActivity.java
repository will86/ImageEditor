package net.jileniao.imageeditor.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class BaseActivity<ViewType extends View> extends Activity {

    protected Button mSaveBtn;

    protected ViewType mView;

    private final String DATA_FORMAT = "'IMG_'yyyyMMdd_HHmmss'.jpg'";

    protected void save() {
        mView.setDrawingCacheEnabled(true);
        Bitmap bitmap = mView.getDrawingCache(true);
        try {
            String path = Environment.getExternalStorageDirectory().getPath()
                    + File.separator
                    + new SimpleDateFormat(DATA_FORMAT).format(new Date());
            File f = new File(path);
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            Toast.makeText(getApplication(), "saved: " + path,
                    Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
