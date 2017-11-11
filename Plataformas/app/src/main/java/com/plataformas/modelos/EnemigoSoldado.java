package com.plataformas.modelos;

import android.content.Context;

import com.plataformas.R;
import com.plataformas.gestores.CargadorGraficos;
import com.plataformas.graficos.Sprite;

/**
 * Created by Fernando on 09/11/2017.
 */

public class EnemigoSoldado extends EnemigoAbstracto {

    public static final String DISPARO_DERECHA = "disparo_derecha";

    public static final String DISPARO_IZQUIERDA = "disparo_izquierda";

    public int orientacion;
    public static final int DERECHA = 1;
    public static final int IZQUIERDA = -1;


    public boolean disparando = false;
    public int tiempoDisparo = 100;
    public int tiempoActual = 0;

    public EnemigoSoldado(Context context, double x, double y) {
        super(context, 0, 0, 50,50 );

        velocidadX = 1;

        this.x = x;
        this.y = y - altura/2;

        orientacion = IZQUIERDA;

        cDerecha = 20;
        cIzquierda = 20;
        cArriba = 25;
        cAbajo = 25;

        inicializar();
    }



    @Override
    public void inicializar (){

        Sprite caminandoDerecha = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.animacion_corre_derecha),
                ancho, altura,
                4, 4, true);
        sprites.put(CAMINANDO_DERECHA, caminandoDerecha);

        Sprite caminandoIzquierda = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.animacion_corre_izquierda),
                ancho, altura,
                4, 4, true);
        sprites.put(CAMINANDO_IZQUIERDA, caminandoIzquierda);

        Sprite muerteDerecha = new Sprite(

                CargadorGraficos.cargarDrawable(context, R.drawable.animacion_derrota_derecha),

                ancho, altura,

                11, 11, false);

        sprites.put(MUERTE_DERECHA, muerteDerecha);

        Sprite muerteIzquierda = new Sprite(

                CargadorGraficos.cargarDrawable(context, R.drawable.animacion_derrota_izquierda),

                ancho, altura,

                11, 11, false);

        sprites.put(MUERTE_IZQUIERDA, muerteIzquierda);

        Sprite disparoIzquierda = new Sprite(

                CargadorGraficos.cargarDrawable(context, R.drawable.animacion_disparo_izquierda),

                ancho, altura,

                8, 8, false);

        sprites.put(DISPARO_IZQUIERDA, disparoIzquierda);

        Sprite disparoDerecha = new Sprite(

                CargadorGraficos.cargarDrawable(context, R.drawable.animacion_disparo_derecha),

                ancho, altura,

                8, 8, false);

        sprites.put(DISPARO_DERECHA, disparoDerecha);

        sprite = caminandoDerecha;
    }

    @Override
    public void actualizar (long tiempo){
        super.actualizar(tiempo);

        if (velocidadX > 0 ) {
            orientacion = DERECHA;
        }
        if (velocidadX < 0 ) {
            orientacion = IZQUIERDA;
        }

        ++tiempoActual;
        if(tiempoActual>= tiempoDisparo &&  this.estado ==EnemigoAbstracto.ACTIVO){
            disparando = true;
            tiempoActual= 0;
        }
    }
}
