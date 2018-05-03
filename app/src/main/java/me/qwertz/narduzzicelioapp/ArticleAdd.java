package me.qwertz.narduzzicelioapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ArticleAdd extends AppCompatActivity { //Class de l'activité
//    SharedPreferences sharedPref;
//    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_article_add); //Load activity
        Button add = (Button) findViewById(R.id.add); //Bouton ajouter un article


        super.onCreate(savedInstanceState);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest(); //Si on clique sur le Boutton add la fonction SendRequest s'execute
            }
        });
    }

    private void sendRequest(){ //La preparation de la requete
        final EditText name = findViewById(R.id.name); //Envoie du nom
        final EditText image = findViewById(R.id.image); //Envoie l'image sous forme d'url
        final EditText ref = findViewById(R.id.reference); //Envie la reference de la catégorie
        final EditText price = findViewById(R.id.price);  //Envoie le prix
        if(!name.getText().toString().isEmpty() && !image.getText().toString().isEmpty() && !ref.getText().toString().isEmpty() && !price.getText().toString().isEmpty()){
            RequestQueue queue = Volley.newRequestQueue(getBaseContext()); //Ajoute la requete a la queau
            final String url = getString(R.string.API_BASE)+"/article"; //Requete pour ajouter un article
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("Response", response);
                            try {
                                JSONObject obj = new JSONObject(response);
                                if(obj.getString("success").equals("true")){ //on recupere le paramètre sous format json en success avec true
                                    finish();
                                }else{
                                    Toast.makeText(getBaseContext(), "Erreur, veuillez verrifier les champs", Toast.LENGTH_SHORT).show(); //Si false
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
                            Log.d("Error.Response", error.toString());
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<String, String>();//Definition des paramètre
                    params.put("name", name.getText().toString());  //Definition du nom
                    params.put("price", price.getText().toString()); //Definition du prix
                    params.put("reference", ref.getText().toString()); //Definition de la reference
                    params.put("image", image.getText().toString()); //Definition de l'url sous format string

                    return params;
                }
            };
            queue.add(postRequest);
        }else{
            Log.d("TEST", "OOPS FAIL");
        }
    }



}
