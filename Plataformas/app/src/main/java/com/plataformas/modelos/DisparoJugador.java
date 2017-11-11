package com.plataformas.modelos;

import android.content.Context;
import android.graphics.Canvas;

import com.plataformas.R;
import com.plataformas.gestores.CargadorGraficos;
import com.plataformas.graficos.Sprite;
import com.plataformas.modelos.controles.BotonDisparar;

import java.util.Vector;

/**
 * Created by Fernando on 01/11/2017.
 *
 */


public class DisparoJugador extends Modelo {
    private Sprite sprite;
    public static double VELOCIDAD = 10;

    public double velocidadX;
    public double velocidadY;

    public DisparoJugador(Context context, double xInicial, double yInicial, float posicionDisparoX, float posicionDiparoY) {
        super(context, xInicial, yInicial, 35, 35);

        setVelocidadDisparo(posicionDisparoX,posicionDiparoY);

        cDerecha = 6;
        cIzquierda = 6;
        cArriba = 6;
        cAbajo = 6;

        inicializar();
    }

    public void inicializar (){
        sprite= new Sprite(
                CargadorGraficos.cargarDrawable(context,
                        R.drawable.animacion_disparo3),
                ancho, altura,
                24, 5, true);
    }

    public void actualizar (long tiempo) {
        sprite.actualizar(tiempo);
    }

    public void dibujar(Canvas canvas){
        sprite.dibujarSprite(canvas, (int) x - Nivel.scrollEjeX, (int) y - Nivel.scrollEjeY);
    }

    public void setVelocidadDisparo(float posicionDisparoX,float posicionDisparoY){
        /*
            vector unitario
         */
        float vecX = (posicionDisparoX)/(float)((Math.sqrt(Math.pow((double)posicionDisparoX,2)+Math.pow((double)posicionDisparoY,2))));
        float vecY = (posicionDisparoY)/(float)((Math.sqrt(Math.pow((double)posicionDisparoX,2)+Math.pow((double)posicionDisparoY,2))));

        float posicionXCalculada = 10*vecX;
        float posicionYCalculada = 10*vecY;

        velocidadY = posicionYCalculada;
        velocidadX = posicionXCalculada;

    }

}