package goronald.web.id.backpackerid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.eyro.mesosfer.LogInCallback;
import com.eyro.mesosfer.Mesosfer;
import com.eyro.mesosfer.MesosferException;
import com.eyro.mesosfer.MesosferUser;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText textEmail, textPassword;
    private ProgressDialog loading;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Mesosfer Project
        Mesosfer.initialize(this, "V3B6udvrwj", "ItzvGjzbQGq0TOx0VgNshLlLcve4Wa09");


        // Set up the login form.
        textEmail = (TextInputEditText)findViewById(R.id.text_email);
        textPassword = (TextInputEditText)findViewById(R.id.text_password);

        loading = new ProgressDialog(this);
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);

        Button btnSignIn = (Button)findViewById(R.id.btn_sign_in);
        Button btnRegister = (Button)findViewById(R.id.btn_register_here);

        btnSignIn.setOnClickListener(operation);
        btnRegister.setOnClickListener(operation);
    }

    View.OnClickListener operation = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_sign_in:
                    attemptLogin();
                    break;
                case R.id.btn_register_here:
                    handleRegister();
                    break;
            }
        }
    };

    private void attemptLogin() {
        String email = textEmail.getText().toString();
        String password = textPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email is empty", Toast.LENGTH_LONG).show();
            textEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password is empty", Toast.LENGTH_LONG).show();
            textPassword.requestFocus();
            return;
        }

        loading.setMessage("Logging in...");
        loading.show();
        MesosferUser.logInAsync(email, password, new LogInCallback() {
            @Override
            public void done(MesosferUser user, MesosferException e) {
                loading.dismiss();
                if (e != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Login Failed");
                    builder.setMessage(
                            String.format(Locale.getDefault(), "Error code: %d\nDescription: %s",
                                    e.getCode(), e.getMessage())
                    );
                    dialog = builder.show();
                    return;
                }

                Toast.makeText(LoginActivity.this, "User logged in...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void handleRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivityForResult(intent, 0);
    }
}

