package sergey.zhuravel.faceverification.activity;

import android.app.AlertDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import sergey.zhuravel.faceverification.R;
import sergey.zhuravel.faceverification.database.DatabaseHandler;
import sergey.zhuravel.faceverification.database.User;
import sergey.zhuravel.faceverification.face.ImageUtils;
import sergey.zhuravel.faceverification.face.MxNetUtils;

public class AddFaceActivity extends AppCompatActivity {

    public static final int CAPTURE_PHOTO_CODE = 1;
    private DatabaseHandler db;
    private ImageView inputImageView;
    private Bitmap processedBitmap;
    private Bitmap bitmap;
    private String currentPhotoPath;
    private List<Float> list;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_face);
        db = new DatabaseHandler(this);
        list = new ArrayList<>();
        inputImageView = (ImageView) findViewById(R.id.tap_to_add_image);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);


        inputImageView.setOnClickListener(v -> dispatchTakePictureIntent());


    }

    private void showDialogAdd() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_dialog_add, null);
        final EditText inputName = (EditText) dialogView.findViewById(R.id.input_name);
        dialogBuilder.setView(dialogView);

        User user = new User();
        dialogBuilder
                .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                    String name = inputName.getText().toString();
                    if (name.isEmpty()) {
                        user.setName("Unknown name");
                    }
                    else {
                        user.setName(name);
                    }
                    user.setBasePath(currentPhotoPath);
                    addFaceDb(user);
                })
                .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.dismiss())
                .setCancelable(false).create().show();
    }


    private void addFaceDb(User user) {
        if (processedBitmap == null) {
            Toast.makeText(this, "No image found", Toast.LENGTH_SHORT).show();
            return;
        }
        getBitmapObservable()

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnRequest(request -> showProgressBar())
                .doOnUnsubscribe(this::hideProgressBar)
                .subscribe(bitmap1 -> {
                    Bitmap sFace = ImageUtils.getAlignedFaceFromImage(bitmap1);
                    if (sFace != null) {
                        list = MxNetUtils.getFeaturesList(sFace);

                        user.setFeuter(list);
                        db.addUser(user);
                        Log.e("TEST-2", String.valueOf(list));
                        Log.e("TEST-2", user.getName());
                        Log.e("TEST-2", user.getBasePath());

                        showToast(getString(R.string.face_add_success));
                        resetVariables();
                        Bitmap addBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_face_black_48dp);
                        inputImageView.setImageBitmap(addBitmap);

                    } else {
                        showToast(getString(R.string.face_add_error));
                        resetVariables();
                        Bitmap addBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_face_black_48dp);
                        inputImageView.setImageBitmap(addBitmap);
                    }


                }, throwable -> {
                    showToast(getString(R.string.face_add_error));
                    Log.e("ERROR", throwable.getMessage());
                });
    }

    private void resetVariables() {
        bitmap = null;
        processedBitmap = null;
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

                    showDialogAdd();

                }
                break;
        }
    }

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

    private Observable<Bitmap> getBitmapObservable() {
        return Observable.defer(() -> Observable.just(processedBitmap));
    }

    private void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }


}
