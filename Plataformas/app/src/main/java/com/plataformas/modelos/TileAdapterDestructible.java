package com.plataformas.modelos;

/**
 * Created by Fernando on 11/11/2017.
 */

public class TileAdapterDestructible {


    private int tiempoHastaDestruccion;
    public int x;
    public int y;

    public TileAdapterDestructible(int x, int y) {
        this.x = x;
        this.y = y;
        tiempoHastaDestruccion = 50;
    }

    public void pasarTiempo(){
        tiempoHastaDestruccion--;
    }

    public boolean destruido(){
        if(tiempoHastaDestruccion == 0){
            return true;
        }
        return false;
    }

}
