package com.plataformas.modelos.controles;

import android.content.Context;
import android.provider.Settings;

import com.plataformas.GameView;
import com.plataformas.R;
import com.plataformas.gestores.CargadorGraficos;
import com.plataformas.modelos.Modelo;

/**
 * Created by Fernando on 01/11/2017.
 */

public class BotonDisparar extends Modelo {

    public static int ANCHO_BOTON = 70;
    public static int ALTO_BOTON = 70;


    public BotonDisparar(Context context) {
        super(context, GameView.pantallaAncho*0.90 , GameView.pantallaAlto*0.6,
                ANCHO_BOTON,ALTO_BOTON);

        imagen = CargadorGraficos.cargarDrawable(context, R.drawable.buttonfirepad);
    }

    public boolean estaPulsado(float clickX, float clickY) {
        boolean estaPulsado = false;

        //hacemos click dentro de la circunferencia
        double distancia = Math.sqrt(Math.pow(clickX - x, 2) + Math.pow(clickY - y, 2));

        if(distancia < 35){
            estaPulsado = true;
        }
        //System.out.println("boton disparar Pulasdo: "+estaPulsado);
        return estaPulsado;
    }


    public int getOrientacionX(
            float cliclX) {
        //System.out.println("click en x :"+ (x-cliclX)*-1);
        return (int) (x - cliclX) *-1;
    }

    public int getOrientacionY(
            float cliclY) {
        //System.out.println("click en y :"+ (y-cliclY)*-1);
        return (int) (y - cliclY) *-1;
    }


}
