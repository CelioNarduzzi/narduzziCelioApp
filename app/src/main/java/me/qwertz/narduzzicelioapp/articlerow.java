package me.qwertz.narduzzicelioapp;

import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class articlerow{ //Deffinition de chaque variable (Nom id,prix, categorie,iamage) Pas encore le créateur pour l'affichage sur le main activity.
    public String name;
    public Integer id;
    public double price;
    public Integer category;
    public String image;
    //getter setter
    public String toString() {return name;}
}
