package ca.uqac.es0x.travailclasseandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.icu.util.LocaleData;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Random;

public class DrawingLetter extends DrawingZone {

    public int drawingColor=Color.GREEN;

    protected int numberOfBlackPixel=0;
    protected int numberOfOtherPixel=0;
    protected TextView letter;

    protected boolean first=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        letter = new TextView(this);

        mPaint.setColor(drawingColor);
        mPaint.setStrokeWidth(110);

        letter.setText(randomLetter());
        letter.setTextColor(Color.BLACK);
        letter.setTextSize(400);
        letter.setGravity(Gravity.CENTER);
        letter.setTypeface(null,Typeface.BOLD);

        DrawingViewCatched canvas = new DrawingViewCatched(this);

        FrameLayout frame = new FrameLayout(this);
        frame.addView(letter);
        frame.addView(canvas);
        setContentView(frame);

    }

    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    class DrawingViewCatched extends DrawingView {

        public DrawingViewCatched(Context c) {
            super(c);
        }

        @Override
        protected void touch_start(float x, float y){
            if(!first){
                new Thread(new Runnable() {
                    public void run() {
                        Bitmap b = getScreenBitmap();

                        numberOfBlackPixel = countColorInBitmap(b,-16777216);
                        numberOfOtherPixel = countColorInBitmap(b,-328966);

                    }
                }).start();
                first=true;
            }
            super.touch_start(x,y);
        }

        @Override
        protected void touch_up() {
            super.touch_up();

            new Thread(new Runnable() {
                public void run() {
                    Bitmap b = getScreenBitmap();
                    int countOfBlackPixel=0,countOfOtherPixel=0;

                    countOfBlackPixel = countColorInBitmap(b,-16777216);
                    countOfOtherPixel = countColorInBitmap(b,-328966);

                    if((numberOfOtherPixel-countOfOtherPixel)>numberOfOtherPixel*0.03){
                        showToast("Tu as depacé");
                        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    }else if((numberOfBlackPixel-countOfBlackPixel)>numberOfBlackPixel*0.7){
                        showToast("Tu as gagné");
                        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                        letter.setText(randomLetter());
                    }
                }
            }).start();



        }
    }


    private Bitmap getScreenBitmap(){
        View v1 = getWindow().getDecorView().getRootView();
        v1.setDrawingCacheEnabled(true);
        return Bitmap.createBitmap(v1.getDrawingCache());
    }

    public int countColorInBitmap(Bitmap bitmap,int color){
        int buff, count=0;
        for(int x=0; x < bitmap.getWidth(); x++){
            for(int y=0; y < bitmap.getHeight(); y++){
                buff = bitmap.getPixel(x,y);
                if(buff==color) count++;
            }
        } return count;
    }

    public static String randomLetter(){
        return String.valueOf((char) (new Random().nextInt(26)+'a')).toUpperCase();
    }

}
