package com.teamticpro.bagmetech.activities.Login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.material.textfield.TextInputEditText;
import com.teamticpro.bagmetech.LoginQuery;
import com.teamticpro.bagmetech.R;
import com.teamticpro.bagmetech.entities.UserEntity;
import com.teamticpro.bagmetech.entities.UserEntityDAO;
import com.teamticpro.bagmetech.entities.UserEntityDatabase;
import com.teamticpro.bagmetech.graphql.Base;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;

public class LoginData extends AppCompatActivity {

    private boolean isLogging;
    private UserEntityDAO databaseUserEntityDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_data);
        databaseUserEntityDao = UserEntityDatabase.getInstance(this).getUserEntityDao();
        // toolbar
        isLogging = false;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Iniciar sesión");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        addLoginData();
        onvalidateErrores();
    }

    public void onvalidateErrores() {
        TextInputEditText email = findViewById(R.id.email_login_data);
        TextInputEditText clave = findViewById(R.id.clave_login_data);
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        clave.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private ApolloClient getApolloClient() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        return ApolloClient.builder().serverUrl(Base.BASE_URL)
                .okHttpClient(okHttpClient).build();
    }

    public void setError(String message) {
        TextView view = findViewById(R.id.message_errors_login_data);
        if (message == null) {
            view.setText("");
            return;
        }
        view.setText(message);
    }

    public void loginUser(String email, String password, final Button login) {
        getApolloClient().query(LoginQuery.builder().email(email).pass(password).build()).enqueue(new ApolloCall.Callback<LoginQuery.Data>() {
            @Override
            public void onResponse(@NotNull final Response<LoginQuery.Data> response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        login.setText(getString(R.string.iniciar_sesion));
                        login.setEnabled(true);
                        isLogging = false;
                        LoginQuery.Data data = response.data();
                        if (data != null) {
                            Log.i("GraphqlQuery", "" + data.toString());
                        }
                        List<Error> errors = response.errors();
                        if (!errors.isEmpty()) {
                            setError(errors.get(0).message());
                            //Toast.makeText(LoginData.this, "Errores encontrados!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (data != null) {
                            LoginQuery.Login login = data.login();
                            LoginQuery.User usuario = login.User();
                            UserEntity en = new UserEntity(usuario._id(), usuario.Name(), usuario.LastName(), login.token(), usuario.username(), usuario.email(), usuario.NumberId(), usuario.technical());
                            new InsertTask(LoginData.this, en).execute();

                        }
                    }
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        login.setEnabled(true);
                        login.setText(getString(R.string.iniciar_sesion));
                        isLogging = false;
                        //Toast.makeText(LoginData.this, "Fallo el inicio de sesión", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private static class InsertTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<LoginData> activityReference;
        private UserEntity note;

        // only retain a weak reference to the activity
        InsertTask(LoginData context, UserEntity note) {
            activityReference = new WeakReference<>(context);
            this.note = note;
        }

        // doInBackground methods runs on a worker thread
        @Override
        protected Boolean doInBackground(Void... objs) {
            activityReference.get().databaseUserEntityDao.addUser(note);
            return true;
        }

        // onPostExecute runs on main thread
        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool) {
                activityReference.get().setNotifyDataBackActivity(note);
                //Toast.makeText(activityReference.get(), "Inicio de sesión correcto: " + note.getName(), Toast.LENGTH_SHORT).show();
                activityReference.get().finish();
                // .setResult(note, 1);
            }
        }

    }

    public void setNotifyDataBackActivity(UserEntity entity) {
        Intent intent = new Intent();
        intent.putExtra("_ultra-data_user", entity);
        setResult(RESULT_OK, intent);
    }

    public void addLoginData() {
        final Button login = findViewById(R.id.iniciando_sesion_api_button);
        if (login != null) {
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isLogging) {
                        TextInputEditText email = findViewById(R.id.email_login_data);
                        TextInputEditText clave = findViewById(R.id.clave_login_data);
                        String email_text = Objects.requireNonNull(email.getText()).toString();
                        String clave_text = Objects.requireNonNull(clave.getText()).toString();
                        if (!email_text.trim().isEmpty() && !clave_text.trim().isEmpty()) {
                            loginUser(email_text, clave_text, login);
                            login.setText(getString(R.string.iniciando_sesión_login));
                            login.setEnabled(false);
                            isLogging = true;
                        } else {
                            //Toast.makeText(LoginData.this, "Llene los campos necesarios para continuar", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
