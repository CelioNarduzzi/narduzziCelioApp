package me.qwertz.narduzzicelioapp;

import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class ArticleDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail); //Affichage de l'activité

        Bundle extras = getIntent().getExtras();
        populateArticles(extras.getString("id"));
    }

    private void populateArticles(String id){
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = getString(R.string.API_BASE)+"/article/"+id; //Requete sous form d'url

        final TextView name = (TextView) findViewById(R.id.articleName); //Definit que le article name est egal au nom qui est envoie dans la requete
        final ImageView image = (ImageView) findViewById(R.id.image);
        final TextView author = (TextView) findViewById(R.id.author);
        final TextView reference = (TextView) findViewById(R.id.reference);
        final TextView price = (TextView) findViewById(R.id.price);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try {
                            if(response.getString("success").equals("true")){ //Si le paramètre succes est true :
                                JSONObject article = response.getJSONObject("article"); //article
                                name.setText(article.getString("ads_name")); //Nom de l'article ou annonce
                                author.setText("Createur: " + article.getString("ads_creator")); //Pour l'instant on recupere que 0 pour le createur
                                price.setText("Prix: " + article.getString("ads_price") + " CHF"); //Recuperer le prix dans la BDD + ajout CHF pour la forme
                                reference.setText("Reference: " + article.getString("ads_number")); //Le numero de catégorie qui n'est pas encore fonctionnelle
                                Picasso.get().load(article.getString("ads_image")).into(image); //Recupere l'image
                            }else{
                                Log.d("INFO", "EXIT");
                                Toast.makeText(getBaseContext(), "Erreur article non trouvé !", Toast.LENGTH_LONG); //Si false (Success = false) aFFICHE AERREUR
                                //finish();
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
    }

}
