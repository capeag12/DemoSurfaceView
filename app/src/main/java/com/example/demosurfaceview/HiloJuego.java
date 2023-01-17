package com.example.demosurfaceview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

public class HiloJuego extends Thread{
    private SurfaceHolder holder;
    private MoverFigura mover;
    private boolean run;


    public HiloJuego(MoverFigura mover) {
        this.mover = mover;
        this.holder = this.mover.getHolder();
        run = false;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    @Override
    public void run() {
        System.out.println("Pintado");
        super.run();
        while (run==true){
            Canvas canvas = null;

            try{
                canvas = holder.lockCanvas();
                if (canvas!=null){
                    synchronized (holder){
                        Paint p = new Paint();
                        p.setColor(Color.RED);
                        p.setAntiAlias(true);
                        for (Figura figura:mover.getFiguras()) {
                            figura.moverFigura(canvas,p);
                        }
                        mover.postInvalidate();
                    }
                }
            } finally {
                if (canvas!=null){
                    holder.unlockCanvasAndPost(canvas);
                }
            }

            canvas = holder.lockCanvas();
            mover.postInvalidate();
            holder.unlockCanvasAndPost(canvas);
        }
    }
}
