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

import com.eyro.mesosfer.ChangePasswordCallback;
import com.eyro.mesosfer.GetCallback;
import com.eyro.mesosfer.MesosferException;
import com.eyro.mesosfer.MesosferObject;
import com.eyro.mesosfer.MesosferUser;
import com.eyro.mesosfer.SaveCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText textOldPassword, textNewPassword, textConfirmPassword,
            textNama, textTglLahir, textKotaAsal;
    private String oldpassword, newpassword, confirmpassword, nama, tgllahir, kotaasal;
    private Date tanggallahir;

    private ProgressDialog loading;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Profile");
        }

        // initialize input form view
        textOldPassword = (TextInputEditText) findViewById(R.id.text_password_old);
        textNewPassword = (TextInputEditText) findViewById(R.id.text_password_new);
        textConfirmPassword = (TextInputEditText) findViewById(R.id.text_password_confirm);
        textNama = (TextInputEditText) findViewById(R.id.text_firstname);
        textTglLahir= (TextInputEditText) findViewById(R.id.text_tgllahir);
        textKotaAsal = (TextInputEditText) findViewById(R.id.text_kotaasal);

        loading = new ProgressDialog(this);
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);

        Button btnUpdateProfile = (Button)findViewById(R.id.UpdateProfile);
        Button btnUpdatePassword = (Button)findViewById(R.id.UpdatePassword);
        btnUpdateProfile.setOnClickListener(operation);
        btnUpdatePassword.setOnClickListener(operation);

        // fetch user and show profile data
        this.fetchUser();
    }

    View.OnClickListener operation = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.UpdateProfile:
                    attemptToUpdateProfile();
                    break;
                case R.id.UpdatePassword:
                    attemptToUpdatePassword();
                    break;
            }
        }
    };

    private void fetchUser() {
        // showing a progress dialog loading
        loading.setMessage("Fetching user profile...");
        loading.show();

        final MesosferUser user = MesosferUser.getCurrentUser();
        if (user != null) {
            user.fetchAsync(new GetCallback<MesosferUser>() {
                @Override
                public void done(MesosferUser mesosferUser, MesosferException e) {
                    // hide progress dialog loading
                    loading.dismiss();

                    // check if there is an exception happen
                    if (e != null) {
                        // setup alert dialog builder
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                        builder.setNegativeButton(android.R.string.ok, null);
                        builder.setTitle("Error Happen");
                        builder.setMessage(
                                String.format(Locale.getDefault(), "Error code: %d\nDescription: %s",
                                        e.getCode(), e.getMessage())
                        );
                        dialog = builder.show();
                        return;
                    }
                    updateView(user);
                    Toast.makeText(ProfileActivity.this, "Profile Fetched", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateView(MesosferUser user) {
        if (user != null) {
            MesosferObject data = user.getData();
            if (data != null) {
                textNama.setText(user.getFirstName());
                Date date = data.optDate("tanggalLahir");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                textTglLahir.setText(format.format(date));

                kotaasal = data.optString("kotaAsal");
                textKotaAsal.setText(String.valueOf(kotaasal));
            }
        }
    }

    public void attemptToUpdateProfile() {
        // get all value from input
        nama = textNama.getText().toString();
        tgllahir = textTglLahir.getText().toString();
        kotaasal = textKotaAsal.getText().toString();

        // validating input values
        if (!isInputProfileValid()) {
            // return if there is an invalid input
            return;
        }

        // execute update profile
        updateProfile();
    }

    private boolean isInputProfileValid() {
        // validating all input values if it is empty
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

    private void updateProfile() {
        // showing a progress dialog loading
        loading.setMessage("Updating user profile...");
        loading.show();

        MesosferUser user = MesosferUser.getCurrentUser();
        if (user != null) {
            user.setFirstName(nama);
            // set custom field
            user.setData("tanggalLahir", tgllahir);
            user.setData("kotaAsal", kotaasal);
            // execute update user
            user.updateDataAsync(new SaveCallback() {
                @Override
                public void done(MesosferException e) {
                    // hide progress dialog loading
                    loading.dismiss();

                    // setup alert dialog builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
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
                    builder.setMessage("Update Profile Succeeded");
                    dialog = builder.show();
                }
            });
        }
    }

    public void attemptToUpdatePassword() {
        oldpassword = textOldPassword.getText().toString();
        newpassword = textNewPassword.getText().toString();
        confirmpassword = textConfirmPassword.getText().toString();

        // validating input values
        if (!isInputPasswordValid()) {
            // return if there is an invalid input
            return;
        }

        // execute update password
        updatePassword();
    }

    private boolean isInputPasswordValid() {
        // validating all input values if it is empty
        if (TextUtils.isEmpty(oldpassword)) {
            Toast.makeText(this, "Old password is empty", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(newpassword)) {
            Toast.makeText(this, "New password is empty", Toast.LENGTH_LONG).show();
            return false;
        }
        if (oldpassword.equals(newpassword)) {
            Toast.makeText(this, "Old and new password are equal", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(confirmpassword)) {
            Toast.makeText(this, "Confirmation password is empty", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!newpassword.equals(confirmpassword)) {
            Toast.makeText(this, "Confirmation password is not match", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void updatePassword() {
        // showing a progress dialog loading
        loading.setMessage("Updating user profile...");
        loading.show();

        MesosferUser user = MesosferUser.getCurrentUser();
        if (user != null) {
            // execute update user
            user.changePasswordAsync(oldpassword, newpassword, new ChangePasswordCallback() {
                @Override
                public void done(MesosferException e) {
                    // hide progress dialog loading
                    loading.dismiss();

                    // setup alert dialog builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
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

                    builder.setTitle("Update Password Succeeded");
                    builder.setMessage("You need to re-login to use new password!");
                    dialog = builder.show();
                }
            });
        }
    }
}
