package com.plataformas.modelos;

import android.content.Context;
import android.graphics.Canvas;

import com.plataformas.R;
import com.plataformas.gestores.CargadorGraficos;
import com.plataformas.graficos.Sprite;

import java.util.HashMap;

/**
 * Created by Fernando on 06/11/2017.
 */


public class Gema extends Modelo {

    public static final int ACTIVO = 1;

    public static final int ELIMINAR = -1;

    public int estado = ACTIVO;
    private Sprite sprite;

    public Gema(Context context, double x, double y) {
        super(context, x, y, 32,32);

        this.x = x;
        this.y = y - altura/2;



        inicializar();
    }


    public void inicializar(){

        this.sprite = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.gem),
                ancho, altura,
                8, 8, true);

    }

    public void actualizar (long tiempo) {
        sprite.actualizar(tiempo);
    }


    public void dibujar(Canvas canvas){
        sprite.dibujarSprite(canvas, (int) x - Nivel.scrollEjeX, (int) y-Nivel.scrollEjeY);
    }





}
