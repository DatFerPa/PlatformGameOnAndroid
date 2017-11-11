package com.plataformas.modelos;

import android.content.Context;
import android.graphics.Canvas;

import com.plataformas.R;
import com.plataformas.gestores.CargadorGraficos;
import com.plataformas.graficos.Sprite;

import java.util.HashMap;

/**
 * Created by Fernando on 01/11/2017.
 */

public class Zombie extends EnemigoAbstracto{

    public Zombie(Context context, double xInicial, double yInicial) {
        super(context, 0, 0, 40, 40);

        velocidadX = 1.5;

        this.x = xInicial;
        this.y = yInicial - altura/2;

        cDerecha = 15;
        cIzquierda = 15;
        cArriba = 20;
        cAbajo = 20;

        inicializar();
    }

    @Override
    public void inicializar (){

        Sprite caminandoDerecha = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.enemyrunright),
                ancho, altura,
                4, 4, true);
        sprites.put(CAMINANDO_DERECHA, caminandoDerecha);

        Sprite caminandoIzquierda = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.enemyrun),
                ancho, altura,
                4, 4, true);
        sprites.put(CAMINANDO_IZQUIERDA, caminandoIzquierda);


        Sprite muerteDerecha = new Sprite(

                CargadorGraficos.cargarDrawable(context, R.drawable.enemydieright),

                ancho, altura,

                4, 8, false);

        sprites.put(MUERTE_DERECHA, muerteDerecha);



        Sprite muerteIzquierda = new Sprite(

                CargadorGraficos.cargarDrawable(context, R.drawable.enemydie),

                ancho, altura,

                4, 8, false);

        sprites.put(MUERTE_IZQUIERDA, muerteIzquierda);

        sprite = caminandoDerecha;
    }


}