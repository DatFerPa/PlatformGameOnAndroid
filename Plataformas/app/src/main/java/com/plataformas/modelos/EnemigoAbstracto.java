package com.plataformas.modelos;

import android.content.Context;
import android.graphics.Canvas;

import com.plataformas.graficos.Sprite;

import java.util.HashMap;

/**
 * Created by Fernando on 09/11/2017.
 */

public abstract class EnemigoAbstracto extends Modelo {
    public static final String CAMINANDO_DERECHA = "Caminando_derecha";
    public static final String CAMINANDO_IZQUIERDA = "caminando_izquierda";
    public static final String MUERTE_DERECHA = "muerte_derecha";
    public static final String MUERTE_IZQUIERDA = "muerte_izquierda";
    public int estado = ACTIVO;
    public static final int ACTIVO = 1;
    public static final int INACTIVO = 0;
    public static final int ELIMINAR = -1;
    protected Sprite sprite;
    protected HashMap<String,Sprite> sprites = new HashMap<String,Sprite> ();
    public double velocidadX;
    public EnemigoAbstracto(Context context, double x, double y, int altura, int ancho) {
        super(context, x, y, altura, ancho);
    }
    public void actualizar (long tiempo) {
        boolean finSprite = sprite.actualizar(tiempo);
        if ( estado == INACTIVO && finSprite == true){
            estado = ELIMINAR;
        }

        if (estado == INACTIVO){
            if (velocidadX > 0)
                sprite = sprites.get(MUERTE_DERECHA);
            else
                sprite = sprites.get(MUERTE_IZQUIERDA);
        } else {

            if (velocidadX > 0) {
                sprite = sprites.get(CAMINANDO_DERECHA);
            }
            if (velocidadX < 0) {
                sprite = sprites.get(CAMINANDO_IZQUIERDA);
            }
        }
    }

    public void girar(){
        velocidadX = velocidadX*-1;
    }

    public void dibujar(Canvas canvas){
        sprite.dibujarSprite(canvas, (int) x - Nivel.scrollEjeX, (int) y-Nivel.scrollEjeY);
    }
    public void destruir (){
        velocidadX = 0;
        estado = INACTIVO;
    }

    public abstract void inicializar();

}
