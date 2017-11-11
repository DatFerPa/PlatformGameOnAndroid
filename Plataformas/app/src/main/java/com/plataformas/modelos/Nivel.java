package com.plataformas.modelos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.plataformas.GameView;
import com.plataformas.R;
import com.plataformas.gestores.CargadorGraficos;
import com.plataformas.gestores.Utilidades;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Nivel {
    private Context context = null;
    private int numeroNivel;
    private Fondo[] fondos;
    private Tile[][] mapaTiles;


    private List<Zombie> zombies;
    private List<EnemigoSoldado> soldados;
    private List<DisparosEnemigo>disparosEnemigos;
    private List<DisparoJugador> disparosJugador;
    private List<Gema> gemas;
    private List<TileAdapterDestructible> tilesPisados;
    public Jugador jugador;

    public float orientacionPad = 0;

    public boolean inicializado;

    public static int scrollEjeX = 0;
    public static int scrollEjeY = GameView.pantallaAlto;

    public boolean botonSaltarPulsado = false;

    //Disparo
    public boolean botonDispararPulsado = false;

    public float posicionDisparoX;
    public float posicionDisparoY;


    private float velocidadGravedad = 0.8f;
    private float velocidadMaximaCaida = 10 ;

    public GameView gameView;

    private Meta meta;

    public Bitmap mensaje;

    public boolean nivelPausado;

    public Nivel(Context context, int numeroNivel) throws Exception {
        inicializado = false;

        this.context = context;
        this.numeroNivel = numeroNivel;
        inicializar();

        inicializado = true;

    }

    public void inicializar()throws Exception {
        scrollEjeX = 0;
        scrollEjeY = GameView.pantallaAlto;

        mensaje = CargadorGraficos.cargarBitmap(context, R.drawable.description);
        nivelPausado = true;
        zombies = new LinkedList<Zombie>();
        soldados = new LinkedList<EnemigoSoldado>();
        disparosJugador = new LinkedList<DisparoJugador>();
        disparosEnemigos = new LinkedList<DisparosEnemigo>();
        tilesPisados = new LinkedList<TileAdapterDestructible>();
        gemas = new LinkedList<Gema>();
        fondos = new Fondo[2];
        fondos[0] = new Fondo(context,CargadorGraficos.cargarBitmap(context,
                R.drawable.capa1), 0);
        fondos[1] = new Fondo(context,CargadorGraficos.cargarBitmap(context,
                R.drawable.capa2), 1f);

        inicializarMapaTiles();
    }


    public void actualizar (long tiempo) throws Exception {
        if (inicializado) {
            for(Zombie zombie: zombies){
                zombie.actualizar(tiempo);
            }

            for(EnemigoSoldado soldado:soldados){
                soldado.actualizar(tiempo);
                if(soldado.disparando){
                    //generado disparos enemigos
                    disparosEnemigos.add(new DisparosEnemigo(context,soldado.x,soldado.y,soldado.orientacion));
                    soldado.disparando = false;
                }
            }

            for(TileAdapterDestructible tile:tilesPisados){
                tile.pasarTiempo();
                if(tile.destruido()){
                    mapaTiles[tile.x][tile.y].tipoDeColision = Tile.PASABLE;
                    mapaTiles[tile.x][tile.y].imagen = CargadorGraficos.cargarDrawable(context,
                            R.drawable.tile_transparente);
                }
            }

            for(DisparoJugador disparoJugador: disparosJugador) {
                disparoJugador.actualizar(tiempo);
            }
            for(Gema gema : gemas){
                gema.actualizar(tiempo);
            }
            jugador.procesarOrdenes(orientacionPad, botonSaltarPulsado,botonDispararPulsado);
            if (botonSaltarPulsado) {
                botonSaltarPulsado = false;
            }
            if (botonDispararPulsado) {
                disparosJugador.add(new DisparoJugador(context,jugador.x,jugador.y,posicionDisparoX,posicionDisparoY));
                botonDispararPulsado = false;
            }
            jugador.actualizar(tiempo);
            aplicarReglasMovimiento();
        }
    }


    public void dibujar (Canvas canvas) {
        if(inicializado) {
            fondos[0].dibujar(canvas);
            fondos[1].dibujar(canvas);
            dibujarTiles(canvas);
            jugador.dibujar(canvas);


            for(DisparoJugador disparoJugador: disparosJugador){
                disparoJugador.dibujar(canvas);
            }

            for(DisparosEnemigo disparoEnemigo: disparosEnemigos){
                disparoEnemigo.dibujar(canvas);
            }

            for(Zombie zombie: zombies){
                zombie.dibujar(canvas);
            }

            for(EnemigoSoldado soldado: soldados){
                soldado.dibujar(canvas);
            }

            for(Gema gema: gemas){
                gema.dibujar(canvas);
            }
            meta.dibujar(canvas);

            if (nivelPausado){
                // la foto mide 480x320
                Rect orgigen = new Rect(0,0 ,
                        480,320);

                Paint efectoTransparente = new Paint();
                efectoTransparente.setAntiAlias(true);

                Rect destino = new Rect((int)(GameView.pantallaAncho/2 - 480/2),
                        (int)(GameView.pantallaAlto/2 - 320/2),
                        (int)(GameView.pantallaAncho/2 + 480/2),
                        (int)(GameView.pantallaAlto/2 + 320/2));
                canvas.drawBitmap(mensaje,orgigen,destino, null);
            }
        }
    }

    public int anchoMapaTiles(){
        return mapaTiles.length;
    }

    public int altoMapaTiles(){

        return mapaTiles[0].length;
    }

    private void inicializarMapaTiles() throws Exception {
        InputStream is = context.getAssets().open(numeroNivel+".txt");
        int anchoLinea;

        List<String> lineas = new LinkedList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        {
            String linea = reader.readLine();
            anchoLinea = linea.length();
            while (linea != null)
            {
                lineas.add(linea);
                if (linea.length() != anchoLinea)
                {
                    Log.e("ERROR", "Dimensiones incorrectas en la línea");
                    throw new Exception("Dimensiones incorrectas en la línea.");
                }
                linea = reader.readLine();
            }
        }

        // Inicializar la matriz
        mapaTiles = new Tile[anchoLinea][lineas.size()];
        // Iterar y completar todas las posiciones
        for (int y = 0; y < altoMapaTiles(); ++y) {
            for (int x = 0; x < anchoMapaTiles(); ++x) {
                char tipoDeTile = lineas.get(y).charAt(x);//lines[y][x];
                mapaTiles[x][y] = inicializarTile(tipoDeTile,x,y);
            }
        }
    }

    private Tile inicializarTile(char codigoTile,int x, int y) {
        switch (codigoTile) {
            case 'G':
                //Gema
                int xCentroAbajoTileG = x * Tile.ancho + Tile.ancho/2;
                int yCentroAbajoTileG = y * Tile.altura + Tile.altura;
                gemas.add(new Gema(context,xCentroAbajoTileG,yCentroAbajoTileG));

                return new Tile(null,Tile.PASABLE);
            case 'Z':
                // Zombie
                // Posición centro abajo
                int xCentroAbajoTileZ = x * Tile.ancho + Tile.ancho/2;
                int yCentroAbajoTileZ = y * Tile.altura + Tile.altura;
                zombies.add(new Zombie(context,xCentroAbajoTileZ,yCentroAbajoTileZ));

                return new Tile(null, Tile.PASABLE);
            case 'S':
                // Soldado
                // Posición centro abajo
                int xCentroAbajoTileS = x * Tile.ancho + Tile.ancho/2;
                int yCentroAbajoTileS = y * Tile.altura + Tile.altura;
                soldados.add(new EnemigoSoldado(context,xCentroAbajoTileS,yCentroAbajoTileS));

                return new Tile(null, Tile.PASABLE);


            case 'M':
                int xCentroAbajoTileM = x * Tile.ancho + Tile.ancho/2;
                int yCentroAbajoTileM = y * Tile.altura + Tile.altura;
                meta = new Meta(context,xCentroAbajoTileM,yCentroAbajoTileM);

                return new Tile(null, Tile.PASABLE);
            case '1':
                // Jugador
                // Posicion centro abajo
                int xCentroAbajoTile = x * Tile.ancho + Tile.ancho/2;
                int yCentroAbajoTile = y * Tile.altura + Tile.altura;
                jugador = new Jugador(context,xCentroAbajoTile,yCentroAbajoTile);

                return new Tile(null, Tile.PASABLE);
            case '.':
                // en blanco, sin textura
                return new Tile(null, Tile.PASABLE);
            case '#':
                // bloque de musgo, no se puede pasar
                return new Tile(CargadorGraficos.cargarDrawable(context,
                        R.drawable.musgo), Tile.SOLIDO);
            case 'W':
                return new Tile(CargadorGraficos.cargarDrawable(context,
                        R.drawable.tierra_dos),Tile.DESTRUCTIBLE);

            default:
                //cualquier otro caso
                return new Tile(null, Tile.PASABLE);
        }
    }

    private void dibujarTiles(Canvas canvas){
        // Calcular que tiles serán visibles en la pantalla
        // La matriz de tiles es más grande que la pantalla
        int tileXJugador = (int) jugador.x / Tile.ancho;
        int izquierda = (int) (tileXJugador - tilesEnDistanciaX(jugador.x - scrollEjeX));
        izquierda = Math.max(0,izquierda); // Que nunca sea < 0, ej -1

        int tileYJugador = (int) jugador.y / Tile.altura;
        int abajo = (int) (tileYJugador - tilesEnDistanciaX(jugador.y - scrollEjeY));
        abajo = Math.max(0,abajo);

        if ( jugador .x  < anchoMapaTiles()* Tile.ancho - GameView.pantallaAncho*0.3 )
            if( jugador .x - scrollEjeX > GameView.pantallaAncho * 0.7 ){
                fondos[0].mover((int) (jugador .x - GameView.pantallaAncho* 0.7 - scrollEjeX));
                fondos[1].mover((int) (jugador .x - GameView.pantallaAncho* 0.7 - scrollEjeX));
                scrollEjeX = (int) ((jugador .x ) - GameView.pantallaAncho* 0.7);

            }

         if(jugador.y < altoMapaTiles()*Tile.altura - GameView.pantallaAlto*0.3){
                if(jugador.y - scrollEjeY > GameView.pantallaAlto * 0.7){
                    scrollEjeY = (int) ((jugador.y)-GameView.pantallaAlto*0.7);
                }
            }

          if(jugador.y > GameView.pantallaAlto*0.3){
            if(jugador.y - scrollEjeY < GameView.pantallaAlto * 0.3){
                scrollEjeY = (int)(jugador.y - GameView.pantallaAlto * 0.3);
            }
          }


        if ( jugador .x  > GameView.pantallaAncho*0.3 )
            if( jugador .x - scrollEjeX < GameView.pantallaAncho *0.3 ){
                fondos[0].mover((int) (jugador .x - GameView.pantallaAncho*0.3 - scrollEjeX));
                fondos[1].mover((int) (jugador .x - GameView.pantallaAncho*0.3 - scrollEjeX));
                scrollEjeX = (int)(jugador .x - GameView.pantallaAncho*0.3);
            }


        int derecha = izquierda +
                GameView.pantallaAncho / Tile.ancho + 1;

        // el ultimo tile visible, no puede superar el tamaño del mapa
        derecha = Math.min(derecha, anchoMapaTiles() - 1);


        for (int y = 0; y < altoMapaTiles() ; ++y) {
            for (int x = izquierda; x <= derecha; ++x) {
                if (mapaTiles[x][y].imagen != null) {
                    // Calcular la posición en pantalla correspondiente
                    // izquierda, arriba, derecha , abajo

                    mapaTiles[x][y].imagen.setBounds(
                            (x  * Tile.ancho) - scrollEjeX,
                            (y * Tile.altura)-scrollEjeY,
                            (x * Tile.ancho) + Tile.ancho - scrollEjeX,
                            (y * Tile.altura )+ Tile.altura-scrollEjeY);

                    mapaTiles[x][y].imagen.draw(canvas);
                }
            }
        }
    }

    private float tilesEnDistanciaX(double distanciaX){
        return (float) distanciaX/Tile.ancho;
    }


    private void aplicarReglasMovimiento() throws Exception {



        int tileXJugadorIzquierda
                = (int) (jugador.x - (jugador.ancho / 2 - 1)) / Tile.ancho;
        int tileXJugadorDerecha
                = (int) (jugador.x + (jugador.ancho / 2 - 1 )) / Tile.ancho;
        int tileXJugadorCentro  = (int) jugador.x / Tile.ancho;

        int tileYJugadorInferior
                = (int) (jugador.y + (jugador.altura / 2 - 1))/ Tile.altura;
        int tileYJugadorCentro
                = (int) jugador.y / Tile.altura;
        int tileYJugadorSuperior
                = (int) (jugador.y - (jugador.altura / 2 - 1)) / Tile.altura;


        if( jugador.colisiona(meta) ) {
            gameView.nivelCompleto();
        }

        // System.out.println(mapaTiles[tileXJugadorCentro][tileYJugadorInferior+1].tipoDeColision);
        if(mapaTiles[tileXJugadorCentro][tileYJugadorInferior+1].tipoDeColision == Tile.DESTRUCTIBLE){
            //System.out.println("Se ha pisado un tile destructible");
            mapaTiles[tileXJugadorCentro][tileYJugadorInferior+1].tipoDeColision = Tile.PISADO;
            tilesPisados.add(new TileAdapterDestructible(tileXJugadorCentro,tileYJugadorInferior+1));

        }

        for(Iterator<Gema> iterator = gemas.iterator();iterator.hasNext();){
            Gema gema = iterator.next();

            if(gema.estado == Gema.ELIMINAR){
                iterator.remove();
                continue;
            }

            if(jugador.colisiona(gema)){
                //sumar puntos
                jugador.puntos++;
                //marcamos para eliminar
                gema.estado = Gema.ELIMINAR;
            }


        }

        for(Iterator<EnemigoSoldado> iterator = soldados.iterator(); iterator.hasNext();) {
            EnemigoSoldado soldado = iterator.next();
            if (soldado.estado == EnemigoSoldado.ELIMINAR) {

                iterator.remove();
                continue;
            }

            if (soldado.estado != EnemigoSoldado.ACTIVO) {
                continue;
            }


            int tileXDerechaSoldado = (int) (soldado.x + (soldado.ancho / 2 - 1)) / Tile.ancho;
            int tileXizquierdaSoldado = (int) (soldado.x - (soldado.ancho / 2 - 1)) / Tile.ancho;
            int tileYInferiorSoldado =
                    (int) (soldado.y + (soldado.altura / 2 - 1)) / Tile.altura;
            int tileYCentroSoldado =
                    (int) soldado.y / Tile.altura;
            int tileYSuperiorEnemigo =
                    (int) (soldado.y - (soldado.altura / 2 - 1)) / Tile.altura;


            int rango = 4;
            if (tileXJugadorIzquierda - rango < tileXizquierdaSoldado &&
                    tileXJugadorIzquierda + rango > tileXizquierdaSoldado) {

                if (jugador.colisiona(soldado)) {
                    if (jugador.golpeado() <= 0) {
                        jugador.restablecerPosicionInicial();
                        scrollEjeX = 0;
                        scrollEjeY = GameView.pantallaAlto;
                        nivelPausado = true;
                        mensaje = CargadorGraficos.cargarBitmap(context, R.drawable.you_lose);
                    }
                }

            }


            if(soldado.velocidadX > 0){
                //  Solo una condicion para pasar:  Tile delante libre, el de abajo solido
                if (tileXDerechaSoldado + 1 <= anchoMapaTiles() - 1 &&
                        mapaTiles[tileXDerechaSoldado + 1][tileYInferiorSoldado].tipoDeColision ==
                                Tile.PASABLE &&
                        mapaTiles[tileXDerechaSoldado + 1][tileYCentroSoldado].tipoDeColision ==
                                Tile.PASABLE &&
                        mapaTiles[tileXDerechaSoldado + 1][tileYSuperiorEnemigo].tipoDeColision ==
                                Tile.PASABLE &&
                        mapaTiles[tileXDerechaSoldado + 1][tileYInferiorSoldado + 1].tipoDeColision ==
                                Tile.SOLIDO) {

                    soldado.x += soldado.velocidadX;

                    // Sino, me acerco al borde del que estoy
                } else if (tileXDerechaSoldado + 1 <= anchoMapaTiles() - 1 ) {

                    int TileEnemigoDerecho = tileXDerechaSoldado*Tile.ancho + Tile.ancho ;
                    double distanciaX = TileEnemigoDerecho - (soldado.x +  soldado.ancho/2);

                    if( distanciaX  > 0) {
                        double velocidadNecesaria = Math.min(distanciaX, soldado.velocidadX);
                        soldado.x += velocidadNecesaria;
                    } else {
                        soldado.girar();
                    }

                    // No hay Tile, o es el final del mapa
                } else {
                    soldado.girar();
                }
            }


            if(soldado.velocidadX < 0){
                // Solo una condición para pasar: Tile izquierda pasable y suelo solido.
                if (tileXizquierdaSoldado - 1 >= 0 &&
                        mapaTiles[tileXizquierdaSoldado-1][tileYInferiorSoldado].tipoDeColision ==
                                Tile.PASABLE &&
                        mapaTiles[tileXizquierdaSoldado-1][tileYCentroSoldado].tipoDeColision ==
                                Tile.PASABLE &&
                        mapaTiles[tileXizquierdaSoldado-1][tileYSuperiorEnemigo].tipoDeColision ==
                                Tile.PASABLE &&
                        mapaTiles[tileXizquierdaSoldado-1][tileYInferiorSoldado +1].tipoDeColision
                                == Tile.SOLIDO) {

                    soldado.x += soldado.velocidadX;

                    // Solido / borde del tile acercarse.
                } else if (tileXizquierdaSoldado -1  >= 0 ) {

                    int TileEnemigoIzquierdo= tileXizquierdaSoldado*Tile.ancho ;
                    double distanciaX =  (soldado.x -  soldado.ancho/2) - TileEnemigoIzquierdo;

                    if( distanciaX  > 0) {
                        double velocidadNecesaria =
                                Utilidades.proximoACero(-distanciaX, soldado.velocidadX);
                        soldado.x += velocidadNecesaria;
                    } else {
                        soldado.girar();
                    }
                } else {
                    soldado.girar();
                }
            }

        }//fin soldados


        for (Iterator<DisparosEnemigo> iterator = disparosEnemigos.iterator(); iterator.hasNext();) {

            DisparosEnemigo disparoEnemigo = iterator.next();

            int tileXDisparo = (int)disparoEnemigo.x / Tile.ancho ;
            int tileYDisparoInferior =
                    (int) (disparoEnemigo.y  + disparoEnemigo.cAbajo) / Tile.altura;

            int tileYDisparoSuperior =
                    (int) (disparoEnemigo.y  - disparoEnemigo.cArriba)  / Tile.altura;

            if (disparoEnemigo.colisiona(jugador)){
                iterator.remove();
                if(jugador.golpeado() <= 0) {
                    jugador.restablecerPosicionInicial();
                    scrollEjeX = 0;
                    scrollEjeY = GameView.pantallaAlto;
                    nivelPausado = true;
                    mensaje = CargadorGraficos.cargarBitmap(context, R.drawable.you_lose);
                }
                break;



            }

            if(disparoEnemigo.velocidadX > 0){
                // Tiene delante un tile pasable, puede avanzar.
                if (tileXDisparo+1 <= anchoMapaTiles()-1 &&
                        mapaTiles[tileXDisparo+1][tileYDisparoInferior].tipoDeColision
                                == Tile.PASABLE &&
                        mapaTiles[tileXDisparo+1][tileYDisparoSuperior].tipoDeColision
                                == Tile.PASABLE ){

                    disparoEnemigo.x +=  disparoEnemigo.velocidadX;

                } else if (tileXDisparo <= anchoMapaTiles() - 1){

                    int TileDisparoBordeDerecho = tileXDisparo*Tile.ancho + Tile.ancho ;
                    double distanciaX =
                            TileDisparoBordeDerecho - (disparoEnemigo.x +  disparoEnemigo.cDerecha);

                    if( distanciaX  > 0) {
                        double velocidadNecesaria =
                                Math.min(distanciaX, disparoEnemigo.velocidadX);
                        disparoEnemigo.x += velocidadNecesaria;
                    } else {
                        iterator.remove();
                        continue;
                    }
                }
            }

            // izquierda
            if (disparoEnemigo.velocidadX <= 0){
                if (tileXDisparo-1 >= 0 &&
                        tileYDisparoSuperior < altoMapaTiles()-1 &&
                        mapaTiles[tileXDisparo-1][tileYDisparoSuperior].tipoDeColision ==
                                Tile.PASABLE &&
                        mapaTiles[tileXDisparo-1][tileYDisparoInferior].tipoDeColision ==
                                Tile.PASABLE){

                    disparoEnemigo.x +=  disparoEnemigo.velocidadX;

                    // No tengo un tile PASABLE detras
                    // o es el INICIO del nivel o es uno SOLIDO
                } else if(tileXDisparo >= 0 ){
                    // Si en el propio tile del jugador queda espacio para
                    // avanzar más, avanzo
                    int TileDisparoBordeIzquierdo = tileXDisparo*Tile.ancho ;
                    double distanciaX =
                            (disparoEnemigo.x - disparoEnemigo.cIzquierda) - TileDisparoBordeIzquierdo ;

                    if( distanciaX  > 0) {
                        double velocidadNecesaria =
                                Utilidades.proximoACero(-distanciaX, disparoEnemigo.velocidadX);
                        disparoEnemigo.x += velocidadNecesaria;
                    } else {
                        iterator.remove();
                        continue;
                    }
                }
            }
        }//Fin disparos  soldados


        for (Iterator<Zombie> iterator = zombies.iterator(); iterator.hasNext();) {
            Zombie enemigo = iterator.next();
            if (enemigo.estado == Zombie.ELIMINAR){

                iterator.remove();
                continue;
            }

            if(enemigo.estado != Zombie.ACTIVO){
                continue;
            }


            int tileXEnemigoIzquierda =
                    (int)(enemigo.x - ( enemigo.ancho/2 - 1)) / Tile.ancho;
            int tileXEnemigoDerecha =
                    (int)(enemigo.x + (enemigo.ancho/2 -1))/ Tile.ancho ;

            int tileYEnemigoInferior =
                    (int) (enemigo.y  + (enemigo.altura/2 - 1)) / Tile.altura;
            int tileYEnemigoCentro =
                    (int) enemigo.y  / Tile.altura;
            int tileYEnemigoSuperior =
                    (int) (enemigo.y  - (enemigo.altura/2 - 1)) / Tile.altura;

            int rango = 4;
            if (tileXJugadorIzquierda - rango < tileXEnemigoIzquierda &&
                    tileXJugadorIzquierda + rango > tileXEnemigoIzquierda){

                if(jugador.colisiona(enemigo)){
                    if(jugador.golpeado() <= 0) {
                        jugador.restablecerPosicionInicial();
                        scrollEjeX = 0;
                        scrollEjeY = GameView.pantallaAlto;
                        nivelPausado = true;
                        mensaje = CargadorGraficos.cargarBitmap(context, R.drawable.you_lose);
                    }
                }
        }


            if(enemigo.velocidadX > 0){
                //  Solo una condicion para pasar:  Tile delante libre, el de abajo solido
                if (tileXEnemigoDerecha + 1 <= anchoMapaTiles() - 1 &&
                        mapaTiles[tileXEnemigoDerecha + 1][tileYEnemigoInferior].tipoDeColision ==
                                Tile.PASABLE &&
                        mapaTiles[tileXEnemigoDerecha + 1][tileYEnemigoCentro].tipoDeColision ==
                                Tile.PASABLE &&
                        mapaTiles[tileXEnemigoDerecha + 1][tileYEnemigoSuperior].tipoDeColision ==
                                Tile.PASABLE &&
                        mapaTiles[tileXEnemigoDerecha + 1][tileYEnemigoInferior + 1].tipoDeColision ==
                                Tile.SOLIDO) {

                    enemigo.x += enemigo.velocidadX;

                    // Sino, me acerco al borde del que estoy
                } else if (tileXEnemigoDerecha + 1 <= anchoMapaTiles() - 1 ) {

                    int TileEnemigoDerecho = tileXEnemigoDerecha*Tile.ancho + Tile.ancho ;
                    double distanciaX = TileEnemigoDerecho - (enemigo.x +  enemigo.ancho/2);

                    if( distanciaX  > 0) {
                        double velocidadNecesaria = Math.min(distanciaX, enemigo.velocidadX);
                        enemigo.x += velocidadNecesaria;
                    } else {
                        enemigo.girar();
                    }

                    // No hay Tile, o es el final del mapa
                } else {
                    enemigo.girar();
                }
            }


            if(enemigo.velocidadX < 0){
                // Solo una condición para pasar: Tile izquierda pasable y suelo solido.
                if (tileXEnemigoIzquierda - 1 >= 0 &&
                        mapaTiles[tileXEnemigoIzquierda-1][tileYEnemigoInferior].tipoDeColision ==
                                Tile.PASABLE &&
                        mapaTiles[tileXEnemigoIzquierda-1][tileYEnemigoCentro].tipoDeColision ==
                                Tile.PASABLE &&
                        mapaTiles[tileXEnemigoIzquierda-1][tileYEnemigoSuperior].tipoDeColision ==
                                Tile.PASABLE &&
                        mapaTiles[tileXEnemigoIzquierda-1][tileYEnemigoInferior +1].tipoDeColision
                                == Tile.SOLIDO) {

                    enemigo.x += enemigo.velocidadX;

                    // Solido / borde del tile acercarse.
                } else if (tileXEnemigoIzquierda -1  >= 0 ) {

                    int TileEnemigoIzquierdo= tileXEnemigoIzquierda*Tile.ancho ;
                    double distanciaX =  (enemigo.x -  enemigo.ancho/2) - TileEnemigoIzquierdo;

                    if( distanciaX  > 0) {
                        double velocidadNecesaria =
                                Utilidades.proximoACero(-distanciaX, enemigo.velocidadX);
                        enemigo.x += velocidadNecesaria;
                    } else {
                        enemigo.girar();
                    }
                } else {
                    enemigo.girar();
                }
            }
        }//fin enemigo

        for (Iterator<DisparoJugador> iterator = disparosJugador.iterator(); iterator.hasNext();) {

            DisparoJugador disparoJugador = iterator.next();

            //para la x
            int tileXDisparo = (int)disparoJugador.x / Tile.ancho ;
            int tileYDisparoInferior =
                    (int) (disparoJugador.y  + disparoJugador.cAbajo) / Tile.altura;

            int tileYDisparoSuperior =
                    (int) (disparoJugador.y  - disparoJugador.cArriba)  / Tile.altura;

            //para la y
            int tileYDisparo = (int) disparoJugador.y / Tile.altura;

            int tileXDisparoDerecha = (int) (disparoJugador.x + disparoJugador.cDerecha)/Tile.ancho;
            int tileXDisparoizquierda = (int) (disparoJugador.x - disparoJugador.cIzquierda)/Tile.ancho;

            for(Zombie zombie : zombies){
                if (disparoJugador.colisiona(zombie)){
                    zombie.destruir();
                    iterator.remove();
                    break;
                }
            }

            for(EnemigoSoldado soldado:soldados){
                if (disparoJugador.colisiona(soldado)){
                    soldado.destruir();
                    iterator.remove();
                    break;
                }
            }

            //Abajo
            if(disparoJugador.velocidadY > 0){
                if(tileYDisparo+1 <= altoMapaTiles()-1 &&
                        mapaTiles[tileXDisparoDerecha][tileYDisparo+1].tipoDeColision
                                == Tile.PASABLE &&
                        mapaTiles[tileXDisparoizquierda][tileYDisparo+1].tipoDeColision
                                == Tile.PASABLE){
                    disparoJugador.y += disparoJugador.velocidadY;
                }else if(tileYDisparo <= altoMapaTiles() -1){
                    int TileDisparoBordeAbajo = tileYDisparo*Tile.altura + Tile.altura;
                    double distanciaY = TileDisparoBordeAbajo - (disparoJugador.y + disparoJugador.cAbajo);

                    if(distanciaY > 0){
                        double velocidadNecesaria = Math.min(distanciaY,disparoJugador.velocidadY);
                        disparoJugador.y += velocidadNecesaria;
                    }else{
                        iterator.remove();
                        continue;
                    }
                }
            }
            //Derecha
            if(disparoJugador.velocidadX > 0){
                // Tiene delante un tile pasable, puede avanzar.
                if (tileXDisparo+1 <= anchoMapaTiles()-1 &&
                        mapaTiles[tileXDisparo+1][tileYDisparoInferior].tipoDeColision
                                == Tile.PASABLE &&
                        mapaTiles[tileXDisparo+1][tileYDisparoSuperior].tipoDeColision
                                == Tile.PASABLE ){

                    disparoJugador.x +=  disparoJugador.velocidadX;
                } else if (tileXDisparo <= anchoMapaTiles() - 1){

                    int TileDisparoBordeDerecho = tileXDisparo*Tile.ancho + Tile.ancho ;
                    double distanciaX =
                            TileDisparoBordeDerecho - (disparoJugador.x +  disparoJugador.cDerecha);

                    if( distanciaX  > 0) {
                        double velocidadNecesaria =
                                Math.min(distanciaX, disparoJugador.velocidadX);
                        disparoJugador.x += velocidadNecesaria;
                    } else {
                        iterator.remove();
                        continue;
                    }
                }
            }


            //Arriba
            if(disparoJugador.velocidadY < 0){
                if(tileYDisparo-1>=0 && tileYDisparoSuperior < anchoMapaTiles()-1 &&
                        mapaTiles[tileXDisparoDerecha][tileYDisparo-1].tipoDeColision == Tile.PASABLE &&
                        mapaTiles[tileXDisparoizquierda][tileYDisparo-1].tipoDeColision == Tile.PASABLE){
                    disparoJugador.y += disparoJugador.velocidadY;
                }else if(tileYDisparo >= 0){
                    int TileDisparoBordeArriba = tileYDisparo*Tile.altura;
                    double distanciaY = (disparoJugador.y - disparoJugador.cArriba) - TileDisparoBordeArriba;
                    if(distanciaY > 0){
                        double velocidadNecesaria = Utilidades.proximoACero(-distanciaY,disparoJugador.velocidadY);
                        disparoJugador.y += velocidadNecesaria;
                    }else{
                        iterator.remove();
                        continue;
                    }
                }
            }

            // izquierda
            if (disparoJugador.velocidadX <= 0){
                if (tileXDisparo-1 >= 0 &&
                        tileYDisparoSuperior < altoMapaTiles()-1 &&
                        mapaTiles[tileXDisparo-1][tileYDisparoSuperior].tipoDeColision ==
                                Tile.PASABLE &&
                        mapaTiles[tileXDisparo-1][tileYDisparoInferior].tipoDeColision ==
                                Tile.PASABLE){

                    disparoJugador.x +=  disparoJugador.velocidadX;
                    // No tengo un tile PASABLE detras
                    // o es el INICIO del nivel o es uno SOLIDO
                } else if(tileXDisparo >= 0 ){
                    // Si en el propio tile del jugador queda espacio para
                    // avanzar más, avanzo
                    int TileDisparoBordeIzquierdo = tileXDisparo*Tile.ancho ;
                    double distanciaX =
                            (disparoJugador.x - disparoJugador.cIzquierda) - TileDisparoBordeIzquierdo ;

                    if( distanciaX  > 0) {
                        double velocidadNecesaria =
                                Utilidades.proximoACero(-distanciaX, disparoJugador.velocidadX);
                        disparoJugador.x += velocidadNecesaria;
                    } else {
                        iterator.remove();
                        continue;
                    }
                }
            }
        }//fin disparo

        // Gravedad Jugador
        if(jugador.enElAire){
            // Recordar los ejes:
            // - es para arriba       + es para abajo.
            jugador.velocidadY += velocidadGravedad;
            if(jugador.velocidadY > velocidadMaximaCaida){
                jugador.velocidadY = velocidadMaximaCaida;
            }
        }

        // derecha o parado

        if (jugador.velocidadX > 0) {
            // Tengo un tile delante y es PASABLE
            // El tile de delante está dentro del Nivel
            if (tileXJugadorDerecha + 1 <= anchoMapaTiles() - 1 &&
                    tileYJugadorInferior <= altoMapaTiles() - 1 &&
                    mapaTiles[tileXJugadorDerecha + 1][tileYJugadorInferior].tipoDeColision ==
                            Tile.PASABLE &&
                    mapaTiles[tileXJugadorDerecha + 1][tileYJugadorCentro].tipoDeColision ==
                            Tile.PASABLE &&
                    mapaTiles[tileXJugadorDerecha + 1][tileYJugadorSuperior].tipoDeColision ==
                            Tile.PASABLE &&
                    mapaTiles[tileXJugadorDerecha][tileYJugadorInferior].tipoDeColision ==
                            Tile.PASABLE &&
                    mapaTiles[tileXJugadorDerecha][tileYJugadorCentro].tipoDeColision ==
                            Tile.PASABLE &&
                    mapaTiles[tileXJugadorDerecha][tileYJugadorSuperior].tipoDeColision ==
                            Tile.PASABLE) {

                jugador.x += jugador.velocidadX;

                // No tengo un tile PASABLE delante
                // o es el FINAL del nivel o es uno SOLIDO
            } else if (tileXJugadorDerecha <= anchoMapaTiles() - 1 &&
                    tileYJugadorInferior <= altoMapaTiles() - 1 &&
                    mapaTiles[tileXJugadorDerecha][tileYJugadorInferior].tipoDeColision ==
                            Tile.PASABLE &&
                    mapaTiles[tileXJugadorDerecha][tileYJugadorCentro].tipoDeColision ==
                            Tile.PASABLE &&
                    mapaTiles[tileXJugadorDerecha][tileYJugadorSuperior].tipoDeColision ==
                            Tile.PASABLE) {

                // Si en el propio tile del jugador queda espacio para
                // avanzar más, avanzo
                int TileJugadorBordeDerecho = tileXJugadorDerecha * Tile.ancho + Tile.ancho;
                double distanciaX = TileJugadorBordeDerecho - (jugador.x + jugador.ancho / 2);

                if (distanciaX > 0) {
                    double velocidadNecesaria = Math.min(distanciaX, jugador.velocidadX);
                    jugador.x += velocidadNecesaria;
                } else {
                    // Opcional, corregir posición
                    jugador.x = TileJugadorBordeDerecho - jugador.ancho / 2;
                }
            }
        }

        // izquierda
        if (jugador.velocidadX <= 0) {
            // Tengo un tile detrás y es PASABLE
            // El tile de delante está dentro del Nivel
            if (tileXJugadorIzquierda - 1 >= 0 &&
                    tileYJugadorInferior < altoMapaTiles() - 1 &&
                    mapaTiles[tileXJugadorIzquierda - 1][tileYJugadorInferior].tipoDeColision ==
                            Tile.PASABLE &&
                    mapaTiles[tileXJugadorIzquierda - 1][tileYJugadorCentro].tipoDeColision ==
                            Tile.PASABLE &&
                    mapaTiles[tileXJugadorIzquierda - 1][tileYJugadorSuperior].tipoDeColision ==
                            Tile.PASABLE &&
                    mapaTiles[tileXJugadorIzquierda][tileYJugadorInferior].tipoDeColision ==
                            Tile.PASABLE &&
                    mapaTiles[tileXJugadorIzquierda][tileYJugadorCentro].tipoDeColision ==
                            Tile.PASABLE &&
                    mapaTiles[tileXJugadorIzquierda][tileYJugadorSuperior].tipoDeColision ==
                            Tile.PASABLE) {

                jugador.x += jugador.velocidadX;

                // No tengo un tile PASABLE detrás
                // o es el INICIO del nivel o es uno SOLIDO
            } else if (tileXJugadorIzquierda >= 0 && tileYJugadorInferior <= altoMapaTiles() - 1 &&
                    mapaTiles[tileXJugadorIzquierda][tileYJugadorInferior].tipoDeColision
                            == Tile.PASABLE &&
                    mapaTiles[tileXJugadorIzquierda][tileYJugadorCentro].tipoDeColision
                            == Tile.PASABLE &&
                    mapaTiles[tileXJugadorIzquierda][tileYJugadorSuperior].tipoDeColision
                            == Tile.PASABLE) {

                // Si en el propio tile del jugador queda espacio para
                // avanzar más, avanzo
                int TileJugadorBordeIzquierdo = tileXJugadorIzquierda * Tile.ancho;
                double distanciaX = (jugador.x - jugador.ancho / 2) - TileJugadorBordeIzquierdo;

                if (distanciaX > 0) {
                    double velocidadNecesaria = Utilidades.proximoACero(-distanciaX, jugador.velocidadX);
                    jugador.x += velocidadNecesaria;
                } else {
                    // Opcional, corregir posición
                    jugador.x = TileJugadorBordeIzquierdo + jugador.ancho / 2;
                }
            }
        }
        // Hacia arriba
        if(jugador.velocidadY < 0){
            // Tile superior PASABLE
            // Podemos seguir moviendo hacia arriba
            if (tileYJugadorSuperior-1 >= 0 &&
                    mapaTiles[tileXJugadorIzquierda][tileYJugadorSuperior-1].tipoDeColision
                            == Tile.PASABLE
                    && mapaTiles[tileXJugadorDerecha][tileYJugadorSuperior-1].tipoDeColision
                    == Tile.PASABLE){

                jugador.y +=  jugador.velocidadY;

                // Tile superior != de PASABLE
                // O es un tile SOLIDO, o es el TECHO del mapa
            } else {

                // Si en el propio tile del jugador queda espacio para
                // subir más, subo
                int TileJugadorBordeSuperior = (tileYJugadorSuperior)*Tile.altura;
                double distanciaY =  (jugador.y - jugador.altura/2) - TileJugadorBordeSuperior;

                if( distanciaY  > 0) {
                    jugador.y += Utilidades.proximoACero(-distanciaY, jugador.velocidadY);

                } else {
                    // Efecto Rebote -> empieza a bajar;
                    jugador.velocidadY = velocidadGravedad;
                    jugador.y +=  jugador.velocidadY;
                }

            }
        }


        // Hacia abajo
        if (jugador.velocidadY >= 0) {
            // Tile inferior PASABLE
            // Podemos seguir moviendo hacia abajo
            // NOTA - El ultimo tile es especial (caer al vacío )
            if (tileYJugadorInferior + 1 <= altoMapaTiles() - 1 &&
                    mapaTiles[tileXJugadorIzquierda][tileYJugadorInferior + 1].tipoDeColision
                            == Tile.PASABLE
                    && mapaTiles[tileXJugadorDerecha][tileYJugadorInferior + 1].tipoDeColision
                    == Tile.PASABLE) {
                // si los dos están libres cae

                jugador.y += jugador.velocidadY;
                jugador.enElAire = true; // Sigue en el aire o se cae
                // Tile inferior SOLIDO
                // El ULTIMO, es un caso especial

            } else if (tileYJugadorInferior + 1 <= altoMapaTiles() - 1 &&
                    (mapaTiles[tileXJugadorIzquierda][tileYJugadorInferior + 1].tipoDeColision
                            == Tile.SOLIDO ||
                            mapaTiles[tileXJugadorDerecha][tileYJugadorInferior + 1].tipoDeColision ==
                                    Tile.SOLIDO ||
                            mapaTiles[tileXJugadorIzquierda][tileYJugadorInferior + 1].tipoDeColision
                            == Tile.DESTRUCTIBLE ||
                            mapaTiles[tileXJugadorDerecha][tileYJugadorInferior + 1].tipoDeColision ==
                                    Tile.DESTRUCTIBLE || mapaTiles[tileXJugadorDerecha][tileYJugadorInferior + 1].tipoDeColision ==
                            Tile.PISADO)) {

                // Con que uno de los dos sea solido ya no puede caer
                // Si en el propio tile del jugador queda espacio para bajar más, bajo
                int TileJugadorBordeInferior =
                        tileYJugadorInferior * Tile.altura + Tile.altura;

                double distanciaY =
                        TileJugadorBordeInferior - (jugador.y + jugador.altura / 2);

                jugador.enElAire = true; // Sigue en el aire o se cae
                if (distanciaY > 0) {
                    jugador.y += Math.min(distanciaY, jugador.velocidadY);

                } else {
                    // Toca suelo, nos aseguramos de que está bien
                    jugador.y = TileJugadorBordeInferior - jugador.altura / 2;
                    jugador.velocidadY = 0;
                    jugador.enElAire = false;
                }

                // Esta cayendo por debajo del ULTIMO
                // va a desaparecer y perder.
            } else {

                jugador.y += jugador.velocidadY;
                jugador.enElAire = true;

                if (jugador.y + jugador.altura / 2 > GameView.pantallaAlto) {
                    // ha perdido
                    scrollEjeX = 0;
                    scrollEjeY = GameView.pantallaAlto;
                    jugador.restablecerPosicionInicial();
                    nivelPausado = true;
                    mensaje = CargadorGraficos.cargarBitmap(context, R.drawable.you_lose);
                    return;
                }

            }
        }
    }//aplicar reglas de movimiento


}

