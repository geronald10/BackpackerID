package goronald.web.id.backpackerid;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.eyro.mesosfer.Mesosfer;
import com.eyro.mesosfer.MesosferException;
import com.eyro.mesosfer.MesosferUser;
import com.eyro.mesosfer.RegisterCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText textEmail, textPassword, textNama, textTglLahir, textKotaAsal;
    private String email, password, nama, tgllahir, kotaasal;

    private Date tanggallahir;

    private ProgressDialog loading;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Create BackpackerID Account");
        }

        // Initialize Mesosfer Project
//        Mesosfer.initialize(this, "V3B6udvrwj", "ItzvGjzbQGq0TOx0VgNshLlLcve4Wa09");

        // initialize input form view
        textEmail = (TextInputEditText) findViewById(R.id.text_email);
        textPassword = (TextInputEditText) findViewById(R.id.text_password);
        textNama = (TextInputEditText) findViewById(R.id.text_firstname);
        textTglLahir= (TextInputEditText) findViewById(R.id.text_tgllahir);
        textKotaAsal = (TextInputEditText) findViewById(R.id.text_kotaasal);

        loading = new ProgressDialog(this);
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);

        Button btnRegister = (Button)findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(operation);
    }

    View.OnClickListener operation = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_register:
                    handleRegister();
                    break;
            }
        }
    };

    public void handleRegister() {
        // get all value from input
        email = textEmail.getText().toString();
        password = textPassword.getText().toString();
        nama = textNama.getText().toString();
        tgllahir = textTglLahir.getText().toString();
        kotaasal = textKotaAsal.getText().toString();
        // validating input values
        if (!isInputValid()) {
            // return if there is an invalid input
            return;
        }

        registerUser();
    }

    private boolean isInputValid() {
        // validating all input values if it is empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email is empty", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password is empty", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(nama)) {
            Toast.makeText(this, "Nama is empty", Toast.LENGTH_LONG).show();
            return false;
        }

        if (TextUtils.isEmpty(kotaasal)) {
            Toast.makeText(this, "Kota Asal is empty", Toast.LENGTH_LONG).show();
            return false;
        }

        if (TextUtils.isEmpty(tgllahir)) {
            Toast.makeText(this, "Tanggal lahir is empty", Toast.LENGTH_LONG).show();
            return false;
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                tanggallahir = format.parse(tgllahir);
            } catch (ParseException e) {
                // show error message when user input invalid format of date
                Toast.makeText(this, "Invalid format of date of birth, use `yyyy-mm-dd`", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private void registerUser() {
        // showing a progress dialog loading
        loading.setMessage("Registering new user...");
        loading.show();

        // create new instance of Mesosfer User
        MesosferUser newUser = MesosferUser.createUser();
        // set default field
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setFirstName(nama);
        // set custom field
        newUser.setData("tanggalLahir", tanggallahir);
        newUser.setData("kotaAsal", kotaasal);
        // execute register user asynchronous
        newUser.registerAsync(new RegisterCallback() {
            @Override
            public void done(MesosferException e) {
                // hide progress dialog loading
                loading.dismiss();

                // setup alert dialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setNegativeButton(android.R.string.ok, null);

                // check if there is an exception happen
                if (e != null) {
                    builder.setTitle("Error Happen");
                    builder.setMessage(
                            String.format(Locale.getDefault(), "Error code: %d\nDescription: %s",
                                    e.getCode(), e.getMessage())
                    );
                    dialog = builder.show();
                    return;
                }

                builder.setTitle("Register Succeeded");
                builder.setMessage("Thank you for registering.");
                dialog = builder.show();
            }
        });
    }
}
