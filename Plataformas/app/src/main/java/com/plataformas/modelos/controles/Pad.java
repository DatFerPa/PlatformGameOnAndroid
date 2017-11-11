package com.plataformas.modelos.controles;

import android.content.Context;

import com.plataformas.GameView;
import com.plataformas.R;
import com.plataformas.gestores.CargadorGraficos;
import com.plataformas.modelos.Modelo;

/**
 * Created by Fernando on 01/11/2017.
 */

public class Pad extends Modelo {

    public Pad(Context context) {
        super(context, GameView.pantallaAncho*0.15 , GameView.pantallaAlto*0.75 ,
                GameView.pantallaAlto, GameView.pantallaAncho);

        altura = 100;
        ancho = 100;
        imagen = CargadorGraficos.cargarDrawable(context, R.drawable.pad);
    }

    public boolean estaPulsado(float clickX, float clickY) {
        boolean estaPulsado = false;

        if (clickX <= (x + ancho / 2) && clickX >= (x - ancho / 2)
                && clickY <= (y + altura / 2) && clickY >= (y - altura / 2)

                ) {
            estaPulsado = true;
        }
        return estaPulsado;
    }

    public int getOrientacionX(
            float cliclX) {

        //System.out.println("click en x :"+ (x-cliclX));
        return (int) (x - cliclX);
    }

}