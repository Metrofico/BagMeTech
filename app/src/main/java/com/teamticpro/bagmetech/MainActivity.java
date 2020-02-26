package com.teamticpro.bagmetech;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.apollographql.apollo.ApolloClient;
import com.google.android.material.navigation.NavigationView;
import com.teamticpro.bagmetech.activities.Login.LoginData;
import com.teamticpro.bagmetech.activities.Login.LogoutData;
import com.teamticpro.bagmetech.activities.m0;
import com.teamticpro.bagmetech.activities.m1;
import com.teamticpro.bagmetech.entities.UserEntity;
import com.teamticpro.bagmetech.entities.UserEntityDatabase;
import com.teamticpro.bagmetech.graphql.Base;
import com.teamticpro.bagmetech.servicesBackground.syncNotifications;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private View viewCustomToolbar;
    private Toolbar searchtollbar;
    private MenuItem item_search;
    private NavigationView navigationView;
    private NavController navController;
    private DrawerLayout drawerLayout;
    private final int LOGIN_ACTIVITY_REQUEST_CODE = 10, LOGOUT_ACTIVITY_REQUEST_CODE = 11;
    private View nav_header_logged, navv_header_nouser;
    private UserEntityDatabase userEntityDatabase;
    private UserEntity entity;
    private ListView view_query;
    private String[] listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(MainActivity.this, syncNotifications.class));
        onCreatedAfterLoad();
        new RetrieveTask(this).execute();
        //onCreatedLoad();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private ApolloClient getApolloClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        return ApolloClient.builder().serverUrl(Base.BASE_URL)
                .okHttpClient(okHttpClient).build();
    }


    @SuppressLint("InflateParams")
    public void allOverCharge(boolean isUser) {

        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(MainActivity.this, R.color.defaultBackground));
        //setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        nav_header_logged = LayoutInflater.from(this).inflate(R.layout.nav_header_logged, null);
        navv_header_nouser = LayoutInflater.from(this).inflate(R.layout.nav_header_nouser, null);
        prepareLoadFromUser(isUser);
        drawerLayout.setVisibility(View.VISIBLE);
        drawerLayout.setFitsSystemWindows(true);
        view_query = findViewById(R.id.list_query);
        listItems = getResources().getStringArray(R.array.problemas);
        view_query.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // TODO Auto-generated method stub
                String value = (String) view_query.getAdapter().getItem(position);
                if (value.equals("Mi monitor no enciende")) {
                    startActivity(new Intent(MainActivity.this, m0.class));
                } else {
                    startActivity(new Intent(MainActivity.this, m1.class));
                }
                //Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();

            }
        });
        //onClickIniciarSesionDrawer();

    }
    public String[] filterAllFrom(String text) {
        ArrayList<String> datas = new ArrayList<>();
        for (String a : listItems) {
            if (a.toLowerCase().contains(text.toLowerCase())) {
                datas.add(a);
            }
        }
        String[] stockArr = new String[datas.size()];
        stockArr = datas.toArray(stockArr);
        return stockArr;
    }


    public void updateFrom(String text) {
        if (text == null || text.trim().length() == 0) {
            view_query.setAdapter(null);
            return;
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, filterAllFrom(text));

        view_query.setAdapter(adapter);
    }

    public void loadNavDrawerHeader(UserEntity entity) {
        View view = navigationView.getHeaderView(0);
        TextView usuario = view.findViewById(R.id.usuario_placeholder_header);
        TextView email = view.findViewById(R.id.email_placeholder_header);
        usuario.setText(entity.getUsername());
        email.setText(entity.getEmail());
    }

    public void prepareLoadFromUser(boolean isUser) {

        if (isUser) {
            try {
                navigationView.removeHeaderView(navv_header_nouser);
            } catch (Exception ignored) {

            }
            if (navigationView.getHeaderView(0) != nav_header_logged) {
                navigationView.addHeaderView(nav_header_logged);
            }
            navigationView.getMenu().findItem(R.id.nav_user_setting).setVisible(true);
            loadNavDrawerHeader(entity);

            return;
        }

        try {
            navigationView.removeHeaderView(nav_header_logged);
        } catch (Exception ignored) {

        }
        if (navigationView.getHeaderView(0) != navv_header_nouser) {
            navigationView.addHeaderView(navv_header_nouser);
        }
        navigationView.getMenu().findItem(R.id.nav_user_setting).setVisible(false);
        onClickIniciarSesionDrawer();
    }


    public void onCreatedLoad(final boolean isUser) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    allOverCharge(isUser);
                } catch (Exception ignored) {
                }

            }
        }, 3000);
    }

    private static class RetrieveTask extends AsyncTask<Void, Void, UserEntity> {

        private WeakReference<MainActivity> activityReference;

        // only retain a weak reference to the activity
        RetrieveTask(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected UserEntity doInBackground(Void... voids) {
            if (activityReference.get() != null)
                return activityReference.get().userEntityDatabase.getUserEntityDao().getUser();
            else
                return null;
        }

        @Override
        protected void onPostExecute(UserEntity userEntity) {

            if (userEntity != null) {
                if (userEntity.getToken() != null) {
                    activityReference.get().entity = userEntity;
                    activityReference.get().onCreatedLoad(true);
                    //Toast.makeText(activityReference.get(), "Inicio correcto con token: " + userEntity.getToken(), Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(activityReference.get(), "No Token", Toast.LENGTH_SHORT).show();
                    activityReference.get().onCreatedLoad(false);
                }

            } else {
                activityReference.get().onCreatedLoad(false);
                //Toast.makeText(activityReference.get(), "No User Entity", Toast.LENGTH_SHORT).show();
            }


        }

    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //now you can display the results
        }
    }

    public void onClickIniciarSesionDrawer() {
        View header = navigationView.getHeaderView(0);
        Button b = header.findViewById(R.id.iniciar_sesion_from_drawer);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawer(navigationView);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivityForResult(new Intent(MainActivity.this, LoginData.class), LOGIN_ACTIVITY_REQUEST_CODE);
                    }
                }, 300);

                //Toast.makeText(MainActivity.this, "CLICKED", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    UserEntity entidad = (UserEntity) data.getSerializableExtra("_ultra-data_user");
                    if (entidad != null) {
                        this.entity = entidad;
                        prepareLoadFromUser(true);
                        //Toast.makeText(this, "Login obtenido: " + entidad.getName(), Toast.LENGTH_SHORT).show();
                    }

                }

            }

        }
        if (requestCode == LOGOUT_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                prepareLoadFromUser(false);
                navController.navigate(R.id.nav_home);
                //Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show();
            }
            if (resultCode == RESULT_CANCELED) {
                navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
                navController.navigate(R.id.nav_home);
                //Toast.makeText(this, "cancelado", Toast.LENGTH_SHORT).show();

            }
        }
    }

    public void onCreatedAfterLoad() {
        userEntityDatabase = UserEntityDatabase.getInstance(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final RelativeLayout boardQuery = findViewById(R.id.query_search);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            View v = LayoutInflater
                    .from(actionBar.getThemedContext())
                    .inflate(R.layout.action_bar_main, null);
            ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT);
            actionBar.setCustomView(v, params);
            actionBar.setDisplayShowCustomEnabled(true);
            getSupportActionBar().setElevation(0);
            viewCustomToolbar = getSupportActionBar().getCustomView();

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        // Passing each main ID as a set of Ids because each
        // main should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        navigationView.getMenu().findItem(R.id.nav_user_setting).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent com = new Intent(MainActivity.this, LogoutData.class);
                Bundle b = new Bundle();
                b.putSerializable("_logout_data_", entity);
                com.putExtras(b);
                startActivityForResult(com, LOGOUT_ACTIVITY_REQUEST_CODE);
                return true;
            }
        });
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        searchtollbar = findViewById(R.id.toolbar_search);
        TextView vsearch = viewCustomToolbar.findViewById(R.id.searchService);
        if (searchtollbar != null) {
            searchtollbar.inflateMenu(R.menu.main);
            Menu search_menu = searchtollbar.getMenu();
            final SearchView searchView =
                    (SearchView) search_menu.findItem(R.id.action_search).getActionView();
            searchView.setQueryHint("Buscar en bagmetech");
            item_search = search_menu.findItem(R.id.action_search);
            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boardQuery.setVisibility(View.VISIBLE);
                }
            });
            searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {

                }
            });
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //Toast.makeText(MainActivity.this, "Buscando: " + query, Toast.LENGTH_SHORT).show();
                    updateFrom(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    updateFrom(newText);
                    return false;
                }
            });
            item_search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem menuItem) {

                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                    boardQuery.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        circleReveal(R.id.search_BarLayout, 0, true, false);
                    }
                    return true;
                }
            });
        }
        vsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    circleReveal(R.id.search_BarLayout, 0, true, true);
                }
                item_search.expandActionView();
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void circleReveal(int viewID, int posFromRight, boolean containsOverflow,
                             final boolean isShow) {
        final View myView = findViewById(viewID);

        int width = myView.getWidth();

        if (posFromRight > 0)
            width -= (posFromRight * getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)) - (getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2);
        if (containsOverflow)
            width -= getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);

        int cx = width;
        int cy = myView.getHeight() / 2;

        Animator anim;
        if (isShow)
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, (float) width);
        else {
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, (float) width, 0);
        }
        anim.setDuration((long) 320);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isShow) {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.INVISIBLE);
                }

            }
        });

        // Empezar la animacion de visibilidad
        if (isShow) {
            myView.setVisibility(View.VISIBLE);
        }
        // Iniciar animacion
        anim.start();
    }

}
