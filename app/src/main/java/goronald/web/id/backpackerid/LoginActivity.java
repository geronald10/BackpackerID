package goronald.web.id.backpackerid;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.eyro.mesosfer.LogInCallback;
import com.eyro.mesosfer.MesosferException;
import com.eyro.mesosfer.MesosferUser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TextInputEditText textEmail, textPassword;
    private ProgressDialog loading;
    private AlertDialog dialog;
    private GoogleApiClient mGoogleApiClient;

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        textEmail = (TextInputEditText)findViewById(R.id.text_email);
        textPassword = (TextInputEditText)findViewById(R.id.text_password);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        loading = new ProgressDialog(this);
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);

        Button btnSignIn = (Button)findViewById(R.id.btn_sign_in);
        Button btnRegister = (Button)findViewById(R.id.btn_register_here);
        SignInButton googleSignInButton = (SignInButton) findViewById(R.id.sign_in_google_button);
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);

        googleSignInButton.setOnClickListener(operation);
        btnSignIn.setOnClickListener(operation);
        btnRegister.setOnClickListener(operation);

        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
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
                case R.id.sign_in_google_button:
                    attemptGoogleLogin();
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
                Intent intent = new Intent(LoginActivity.this, BudgetActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void handleRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivityForResult(intent, 0);
    }

    public void attemptGoogleLogin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, REQUEST_RESOLVE_ERROR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("hasil", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Toast.makeText(getApplicationContext(), "Signed in Successfully" + acct, Toast.LENGTH_SHORT);
        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        // dismiss any resource showing
        if (loading != null && loading.isShowing()) {
            loading.dismiss();
        }

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((LoginActivity) getActivity()).onDialogDismissed();
        }
    }
}

