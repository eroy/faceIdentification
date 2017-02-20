package sergey.zhuravel.faceverification.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import sergey.zhuravel.faceverification.R;
import sergey.zhuravel.faceverification.database.DatabaseHandler;

public class StartActivity extends AppCompatActivity {

    private Button btnVerify;
    private Button btnMain;
    private Button btnDeleteAll;
    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        databaseHandler = new DatabaseHandler(this);

        btnVerify = (Button) findViewById(R.id.btnVerify);
        btnMain = (Button) findViewById(R.id.btnMain);
        btnDeleteAll = (Button) findViewById(R.id.btnDeleteAll);

        btnVerify.setOnClickListener(v -> {
            startActivity(new Intent(this, AddFaceActivity.class));
        });
        btnMain.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });
        btnDeleteAll.setOnClickListener(v -> {
            if (databaseHandler.deleteAllUser()){
                showToast("База данных очищена");
            }
            else {
                showToast("База данных пустая!");
            }
        });

    }

    private void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
