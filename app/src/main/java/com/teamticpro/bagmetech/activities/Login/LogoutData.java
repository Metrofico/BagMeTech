package com.teamticpro.bagmetech.activities.Login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.teamticpro.bagmetech.R;
import com.teamticpro.bagmetech.entities.UserEntity;
import com.teamticpro.bagmetech.entities.UserEntityDAO;
import com.teamticpro.bagmetech.entities.UserEntityDatabase;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class LogoutData extends AppCompatActivity {

    public UserEntityDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        final UserEntity entity = (UserEntity) Objects.requireNonNull(getIntent().getExtras()).getSerializable("_logout_data_");
        if (entity == null) {
            setResult(RESULT_OK, new Intent());
            finish();
            return;
        }
        setContentView(R.layout.activity_logout_data);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Cuenta de usuario");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        dao = UserEntityDatabase.getInstance(this).getUserEntityDao();
        Button salir = findViewById(R.id.salir);
        loadDataShow(entity);
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new removeTask(LogoutData.this, entity.getId()).execute();
            }
        });
    }

    public void setTextFromView(@IdRes int id, String text) {
        TextView v = findViewById(id);
        v.setText(text);
    }

    public void loadDataShow(UserEntity entity) {
        setTextFromView(R.id.name_placeholder, entity.getName());
        setTextFromView(R.id.username_placeholder, entity.getUsername());
        setTextFromView(R.id.apellido_placeholder, entity.getLastName());
        setTextFromView(R.id.correo_electronico_placeholder, entity.getEmail());
        setTextFromView(R.id.cedula_placeholder, entity.getNumberId() != null && entity.getNumberId().length() > 0 ? entity.getNumberId() : "No disponible");
        setTextFromView(R.id.technical_placeholder, entity.getTechnical() != null && entity.getTechnical() ? "Modo Técnico" : "No disponible");

    }

    private static class removeTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<LogoutData> activityReference;

        private String id;

        // only retain a weak reference to the activity
        removeTask(LogoutData context, String id) {
            activityReference = new WeakReference<>(context);

            this.id = id;
        }

        // doInBackground methods runs on a worker thread
        @Override
        protected Boolean doInBackground(Void... objs) {
            return activityReference.get().dao.removeUser(id) > 0;
        }

        // onPostExecute runs on main thread
        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool) {
                Toast.makeText(activityReference.get(), "Sesión desconectada!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent();
                activityReference.get().setResult(RESULT_OK, i);
                activityReference.get().finish();
                // .setResult(note, 1);
            }
        }

    }
}
