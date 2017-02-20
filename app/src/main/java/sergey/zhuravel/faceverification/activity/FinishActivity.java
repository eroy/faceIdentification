package sergey.zhuravel.faceverification.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import sergey.zhuravel.faceverification.R;

public class FinishActivity extends AppCompatActivity {

    private String basePath;
    private String currentPath;
    private String name;
    private String coincidence;
    private ImageView currentImage;
    private ImageView baseImage;
    private TextView textDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        Intent intent = getIntent();
        basePath = intent.getStringExtra("basePath");
        currentPath = intent.getStringExtra("currentPath");
        name = intent.getStringExtra("name");
        coincidence = intent.getStringExtra("coincidence");

        currentImage = (ImageView) findViewById(R.id.currentImage);
        baseImage = (ImageView) findViewById(R.id.baseImage);
        textDesc = (TextView) findViewById(R.id.textDesc);


        File baseFile = new File(basePath);
        File currentFile = new File(currentPath);
        Bitmap baseBitmap = BitmapFactory.decodeFile(baseFile.getAbsolutePath());
        Bitmap currentBitmap = BitmapFactory.decodeFile(currentFile.getAbsolutePath());


        currentImage.setImageBitmap(currentBitmap);
        baseImage.setImageBitmap(baseBitmap);

        Float coincidenceFloat = Float.valueOf(coincidence);
        coincidenceFloat = coincidenceFloat * 100;
        String co = String.valueOf(round(coincidenceFloat,2));


        String text = "Имя: " + name + "\n" +
                "Процент распознания: " + co + " %";
        textDesc.setText(text);


    }
    private float round(float number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++)
            pow *= 10;
        float tmp = number * pow;
        return (float) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
    }
}
