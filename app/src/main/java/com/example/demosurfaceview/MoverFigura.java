package com.example.demosurfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    public MoverFigura(Context context) {
        super(context);
        figuras = new ArrayList<>();
        getHolder().addCallback(this);
        setBackgroundColor(Color.WHITE);
        figuras.add(new Circulo(500,500,140));
        figuras.add(new Rectangulo(100,900,280,280));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint p = new Paint();
        p.setColor(Color.RED);
        p.setAntiAlias(true);
        for (Figura f:figuras
        ) {
            f.moverFigura(canvas,p);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("Pulsado");

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{

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
        figuras.add(new Imagen(500F, 500F,bmp, (int) (bmp.getHeight()*0.3), (int) (bmp.getWidth()*0.3)));
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
