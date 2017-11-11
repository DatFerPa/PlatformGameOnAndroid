package com.plataformas.modelos.controles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.plataformas.GameView;
import com.plataformas.R;
import com.plataformas.gestores.CargadorGraficos;
import com.plataformas.modelos.Modelo;

/**
 * Created by Fernando on 06/11/2017.
 */

public class Marcador extends Modelo {

    private int puntos;

    public Marcador(Context context) {
        super(context, GameView.pantallaAncho*0.70, GameView.pantallaAlto*0.1, GameView.pantallaAlto, GameView.pantallaAncho);

        altura = 40;
        ancho = 40;

        imagen = CargadorGraficos.cargarDrawable(context, R.drawable.score);
    }


    public void dibujarPuntuacion(Canvas canvas,int puntos){
        dibujar(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setTextSize(30);
        canvas.drawText( String.valueOf(puntos),(int)(GameView.pantallaAncho*0.80), (int)(GameView.pantallaAlto*0.15), paint);


    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public int getPuntos() {
        return puntos;
    }

}
