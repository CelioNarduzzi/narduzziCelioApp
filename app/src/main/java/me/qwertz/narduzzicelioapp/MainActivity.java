package me.qwertz.narduzzicelioapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.database.MatrixCursor;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
    JSONArray jsonArrayArticles; //Varaiable pour les article avec json

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //Load l'activité principal
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //Load la toolbar
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab); //Menu a gauche
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Remplacer avec mon action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadCategoriesMenu();
        loadArticles();
        testClick();
    }

    private void getUserInfos(){
        boolean cancel = false;
        sharedPref= getSharedPreferences("user", Context.MODE_PRIVATE); //Affichage utilisateur menu navigateur
        editor=sharedPref.edit();
        if(sharedPref.getString("token", "").isEmpty()){ //Paramètre de token
            cancel = true;
        }
        if(!cancel){
            RequestQueue queue = Volley.newRequestQueue(this);
            final String url = getString(R.string.API_BASE)+"/user"; //Requete pour recuperer l'utilisateur
            sharedPref= getSharedPreferences("user", Context.MODE_PRIVATE);
            editor=sharedPref.edit();

            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("Response", response);
                            try {
                                JSONObject resp = new JSONObject(response);
                                JSONObject user = resp.getJSONObject("user"); //Utilisateur

                                if(resp.getString("success").equals("true")){
                                    TextView name = findViewById(R.id.header_name);
                                    TextView email = findViewById(R.id.header_email);
                                    name.setText(user.getString("users_name")); //Si vrai on affiche le nom et l'email
                                    email.setText(user.getString("users_email"));
                                }else{

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", String.valueOf(error));
                        }
                    }
            ) {

                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<String, String> ();
                    params.put("token", sharedPref.getString("token", ""));  //Paramètre de token

                    return params;
                }
            };
            queue.add(postRequest);
        }

    }

    private void testClick(){
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject a = jsonArrayArticles.getJSONObject(position);
                    Log.d("SSS", a.getString("ads_id"));
                    Intent intent = new Intent(getBaseContext(), ArticleDetail.class);
                    intent.putExtra("id", a.getString("ads_id"));
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });
    }

    private void loadCategoriesMenu() {
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);

        Menu menu = navView.getMenu();
        menu.clear();
        final Menu categoryMenu = menu.addSubMenu("Categories"); //Affichage catégorie
        //categoryMenu.getItem(0).

        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = getString(R.string.API_BASE)+"/categories"; //Requete pour afficher les catégories dans le menu de navigation

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try {
                            JSONArray mainjsonArray = response.getJSONArray("categories");
                            for (int i = 0; i < mainjsonArray.length(); i++) {
                                JSONObject j_object=mainjsonArray.getJSONObject(i);
                                System.out.println();
                                //categoryMenu.add(0,j_object.getString("id"),0,j_object.getString("name"));
                                categoryMenu.add(0, j_object.getInt("id"), 0, j_object.getString("name")); //On ajoute dans le menu catégorie le nom selon l'id de la catégorie
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );

        queue.add(getRequest);
        navView.invalidate();
    }

    private void loadIntoListView(String json) throws JSONException {
        ListView listView = (ListView) findViewById(R.id.listView);
        jsonArrayArticles = new JSONArray(json);
        String[] articles = new String[jsonArrayArticles.length()];
        for (int i = 0; i < jsonArrayArticles.length(); i++) {
            JSONObject obj = jsonArrayArticles.getJSONObject(i);
            articles[i] = obj.getString("ads_name") + "     :"+ obj.getString("ads_price"); //Affichage de l'article dans la page principal du nom + le prix
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, articles);
        listView.setAdapter(arrayAdapter);
    }


    private void loadArticles(){ //Load les article
        ListView listView = (ListView) findViewById(R.id.listView);
        final String[] matrix  = { "_id", "name", "price" };
        final String[] columns = { "name", "value" };
        final Context self = this;

        MatrixCursor  cursor = new MatrixCursor(matrix);
        DecimalFormat formatter = new DecimalFormat("##,##0.00");

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.articlerow);

        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = getString(R.string.API_BASE)+"/articles"; //Requete pour afficher les articles

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Affichage de la reponse
                        try {
                            JSONArray mainjsonArray = response.getJSONArray("articles");
                            loadIntoListView(mainjsonArray.toString());
                            HashMap<String,String> item;
                            for (int i = 0; i < mainjsonArray.length(); i++) {
                                JSONObject j_object=mainjsonArray.getJSONObject(i);
                                item = new HashMap<String,String>();
                                item = new HashMap<String,String>();
                                item.put( "line1", j_object.getString("ads_name")); //Affichage artcle le nom
                                item.put( "line2", j_object.getString("ads_image"));//Affichage article le lîmage (N'affichage pas encore l'image)
                                item.put( "line3", j_object.getString("ads_price") + " CHF"); //Affichage artcle le prix
                                list.add( item );
                            }
                            SimpleAdapter sa = new SimpleAdapter(self, list,
                                    R.layout.articlerow,
                                    new String[] { "line1","line2", "line3" },
                                    new int[] {R.id.line_a, R.id.line_b, R.id.line_c});

                            ((ListView)findViewById(R.id.listView)).setAdapter(sa);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );

        queue.add(getRequest);

        listView.setAdapter(listAdapter);
    }

    public void clickOnItem(View v){
        Log.d("ITEM", v.toString());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        sharedPref= getSharedPreferences("user", Context.MODE_PRIVATE);
        editor=sharedPref.edit();

        Log.d("INFO", sharedPref.getString("token", "")); //Token

        if(!sharedPref.getString("token", "").isEmpty()){//probably connected
            menu.getItem(1).setVisible(false);//Menu add article On ne peut ajouter si l'utilisateur n'est pas connection donc false a visible
            menu.getItem(0).setVisible(true);// Menu login/register
            menu.getItem(2).setVisible(true);//Menu Deconnecter
            getUserInfos();
        }else{ //Pas conneccter

        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        Log.d("INFO", String.valueOf(id));
        if (id == R.id.action_login_register) { //Si on clique sur se login register sa ouvre la page
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

            return true;
        }else if(id == R.id.action_disconnect){ //Si on clique sur deco sa nous deconnect
            sharedPref= getSharedPreferences("user", Context.MODE_PRIVATE);
            editor=sharedPref.edit(); //Supprimer le token
            editor.clear();
            editor.commit();
            finish();
            startActivity(getIntent());
            return true;
        }else if(id == R.id.add_article){ //Si on clique sur ajouter un article sa nous renvoie vers la page
            Intent intent = new Intent(this, ArticleAdd.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}