package goronald.web.id.backpackerid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.eyro.mesosfer.LogOutCallback;
import com.eyro.mesosfer.MesosferException;
import com.eyro.mesosfer.MesosferUser;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog loading;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loading = new ProgressDialog(this);
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);

        Button btnLogOut = (Button)findViewById(R.id.btn_log_out);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogOut();
            }
        });
    }

    public void attemptLogOut() {
        loading.setMessage("Logging out...");
        loading.show();
        MesosferUser.logOutAsync(new LogOutCallback() {
            @Override
            public void done(MesosferException e) {
                loading.dismiss();

                if (e != null) {
                    // setup alert dialog builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setNegativeButton(android.R.string.ok, null);
                    builder.setTitle("Log Out Error");
                    builder.setMessage(
                            String.format(Locale.getDefault(), "Error code: %d\nDescription: %s",
                                    e.getCode(), e.getMessage())
                    );
                    dialog = builder.show();
                }

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent,0);
                finish();
            }
        });
    }
}
