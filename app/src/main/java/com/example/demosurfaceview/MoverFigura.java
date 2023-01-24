package com.example.demosurfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class MoverFigura extends SurfaceView implements SurfaceHolder.Callback {
    private float x;
    private float y;
    private HiloJuego hilo;
    private ArrayList<Figura> figuras;
    private Figura figuraActiva;
    private float iniX;
    private float iniY;
    private boolean pulsado = false;
    boolean modificandoFig = false;
    private Boton btnAñadir;
    public MoverFigura(Context context) {
        super(context);
        figuras = new ArrayList<>();
        getHolder().addCallback(this);
        setBackgroundColor(Color.WHITE);
        figuras.add(new Circulo(500,500,140));
        figuras.add(new Rectangulo(100,900,280,280));
        figuras.add(new Circulo(900,900, 70));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint p = new Paint();
        p.setColor(Color.RED);
        p.setAntiAlias(true);
        for (Figura f:figuras
        ) {
            if (f instanceof Rectangulo){
                Paint pRect = new Paint();
                pRect.setColor(Color.BLUE);
                f.moverFigura(canvas,pRect);
            }
            else if (f instanceof Circulo){
                p.setColor(Color.RED);
                f.moverFigura(canvas,p);
            }
        }

        canvas.drawRect(new Rect(0,0, getWidth(), (int) (getHeight()*0.10)),p);
        btnAñadir.dibujarControl(canvas);

    }

    private void generarCirculoAleatorio(){
        float x = (float) (Math.random()*(getWidth()-23));
        float y = (float) (Math.random()*(getHeight()-20));

        synchronized (this.figuras){
            this.figuras.add(new Circulo(x,y,42));

        }



    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("Pulsado");

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{

                if (btnAñadir.estaDentro(x,y)){
                    generarCirculoAleatorio();
                }

                for (Figura f:figuras) {
                    boolean dentro = f.estaDentro(x,y);
                    if (dentro == true){
                        figuraActiva = f;
                        iniX = event.getX();
                        iniY = event.getY();
                        System.out.println("Pulsado sobre figura");
                    }
                }
                System.out.println("Pulsado");
                return true;


            }
            case MotionEvent.ACTION_MOVE:{
                if (figuraActiva!=null){

                    figuraActiva.actualizarFigura(x-iniX,y-iniY);
                    iniX = event.getX();
                    iniY = event.getY();
                    System.out.println("Moviendose");

                    if (figuraActiva instanceof Circulo){
                        Circulo circuloActivo = (Circulo) figuraActiva;

                        synchronized (this.figuras){
                            this.modificandoFig = true;
                            for (int i = 0; i<figuras.size();i++) {
                                Figura f = figuras.get(i);
                                if (f instanceof Circulo){
                                    Circulo c = (Circulo) f;
                                    if (circuloActivo.detectarCirculo(c)){
                                        if (circuloActivo.getRadio()>c.getRadio()){
                                            float radio = c.getRadio()/5;
                                            this.figuras.remove(f);
                                            circuloActivo.aumentarRadio(radio);
                                            break;
                                        }
                                    }
                                }
                            }

                        }
                        this.modificandoFig=false;


                    }
                }


                break;
            }
            case MotionEvent.ACTION_UP:{
                figuraActiva = null;
                System.out.println("Levantado");
                break;
            }

        }

        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.img);
        figuras.add(new Imagen(500F, 500F,bmp, (int) (bmp.getHeight()*0.1), (int) (bmp.getWidth()*0.1)));
        btnAñadir = new Boton("+", 100, 100, 50,Color.BLUE);

        hilo = new HiloJuego(this);
        hilo.setRun(true);
        hilo.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        boolean retry = true;
        hilo.setRun(false);

        while (retry){
            try {
                hilo.join();
                retry=false;
            }catch (InterruptedException e){

            }
        }
    }

    public ArrayList<Figura> getFiguras() {
        return figuras;
    }
}
