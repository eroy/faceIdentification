package sergey.zhuravel.faceverification.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import sergey.zhuravel.faceverification.R;
import sergey.zhuravel.faceverification.database.DatabaseHandler;
import sergey.zhuravel.faceverification.database.User;
import sergey.zhuravel.faceverification.face.ImageUtils;
import sergey.zhuravel.faceverification.face.MxNetUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    public static final int CAPTURE_PHOTO_CODE = 1;
    DatabaseHandler db = new DatabaseHandler(this);

    private ImageView inputImageView;
    private Bitmap bitmap;
    private Bitmap processedBitmap;
    private TextView tvText;
    private String currentPhotoPath;

    private Bitmap processBitmap(final Bitmap origin) {
        int width = origin.getWidth();
        int height = origin.getHeight();
        int newWidth = 480;
        int newHeight = 640;
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(
                origin, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }


    private void verify() {
        if (processedBitmap == null) {
            Toast.makeText(this, "No image found", Toast.LENGTH_SHORT).show();
            return;
        }
        List<User> listUser = db.getAllUser();


        getBitmapObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap1 -> {

                    Bitmap sFace = ImageUtils.getAlignedFaceFromImage(bitmap1);
                    List<Float> face = MxNetUtils.getFeaturesList(sFace);
                    for (final User u : listUser) {
                        Log.e("TEST-DB-2", String.valueOf(u.getFeuter()));
                        float s = MxNetUtils.calCosineSimilarity(u.getFeuter(), face);

                        if (s > 0.8) {
                            goToFinishAcitvity(u.getBasePath(), currentPhotoPath, u.getName(), String.valueOf(s));

                            resetVariables();
                            break;
                        } else {
                            Bitmap addBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_face_black_48dp);
                            inputImageView.setImageBitmap(addBitmap);
                            tvText.setText("Ошибка идентификации.\nЛицо в базе данных не обнаружено!");
                            resetVariables();
                        }
                    }

                }, throwable -> {
                    Log.e("ERROR", throwable.getMessage());
                });

    }

    private void goToFinishAcitvity(String basePath, String currentPath, String name, String coincidence) {
        Intent intent = new Intent(this, FinishActivity.class);
        intent.putExtra("basePath", basePath);
        intent.putExtra("currentPath", currentPath);
        intent.putExtra("name", name);
        intent.putExtra("coincidence", coincidence);
        startActivity(intent);
        finish();

    }

    private void resetVariables() {
        bitmap = null;
        processedBitmap = null;
    }

    public Observable<Bitmap> getBitmapObservable() {
        return Observable.defer(() -> Observable.just(processedBitmap));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        long t = System.currentTimeMillis();
//        Bitmap srcOriginImage = BitmapFactory.decodeResource(getResources(), R.drawable.a);
//        Bitmap dstOriginImage = BitmapFactory.decodeResource(getResources(), R.drawable.b);
//
//        Bitmap sFace = UtilImage.getAlignedFaceFromImage(srcOriginImage);
//        Bitmap dFace = UtilImage.getAlignedFaceFromImage(dstOriginImage);
//
//        float s = MxNetUtils.identifyImage(sFace, dFace);
//        Log.d("total time", String.valueOf(System.currentTimeMillis() - t));


        inputImageView = (ImageView) findViewById(R.id.tap_to_add_image);
        tvText = (TextView) findViewById(R.id.tvText);


        inputImageView.setOnClickListener(v -> dispatchTakePictureIntent());

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                startActivityForResult(takePictureIntent, CAPTURE_PHOTO_CODE);
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case CAPTURE_PHOTO_CODE:
                if (resultCode == RESULT_OK) {
                    bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                    processedBitmap = processBitmap(bitmap);
                    inputImageView.setImageBitmap(bitmap);

                    verify();
                }
                break;
        }
    }


}
