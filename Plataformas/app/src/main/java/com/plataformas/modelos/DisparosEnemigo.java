package com.plataformas.modelos;

import android.content.Context;
import android.graphics.Canvas;

import com.plataformas.R;
import com.plataformas.gestores.CargadorGraficos;
import com.plataformas.graficos.Sprite;

/**
 * Created by Fernando on 10/11/2017.
 */

public class DisparosEnemigo extends Modelo {

    //40 x 32

    private Sprite sprite;
    public static double VELOCIDAD = 10;

    public double velocidadX;


    public DisparosEnemigo(Context context, double x, double y, int orientacion) {
        super(context, x, y, 32, 32);

        cDerecha = 4;
        cIzquierda = 4;
        cArriba = 4;
        cAbajo = 4;

        this.velocidadX = VELOCIDAD;

        if(orientacion == EnemigoSoldado.IZQUIERDA){
            velocidadX = velocidadX*-1;
        }

        inicializar();
    }

    public void inicializar (){
        sprite= new Sprite(
                CargadorGraficos.cargarDrawable(context,
                        R.drawable.animacion_disparo1),
                ancho, altura,
                8, 4, true);
    }

    public void actualizar (long tiempo) {
        sprite.actualizar(tiempo);
    }


    public void dibujar(Canvas canvas){
        sprite.dibujarSprite(canvas, (int) x - Nivel.scrollEjeX, (int) y - Nivel.scrollEjeY);
    }


}
