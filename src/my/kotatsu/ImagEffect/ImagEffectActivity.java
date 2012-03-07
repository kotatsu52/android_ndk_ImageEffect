package my.kotatsu.ImagEffect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ImagEffectActivity extends Activity {

	private ImageView imgView;
	private Bitmap Soucebmp;
	private ProgressDialog progressDialog;
	private TextView textView;
	Handler handler= new Handler();
	Bitmap Chgbmp;
	long start;
	long stop;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        imgView = (ImageView) findViewById(R.id.imageView1);
        textView = (TextView) findViewById(R.id.textView1);
        
        // 文字の設定
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(25.0f);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        
        // bmp読み込み
        Resources r = getResources();
        Soucebmp = BitmapFactory.decodeResource(r, R.drawable.test001);
        Chgbmp=Soucebmp.copy(Bitmap.Config.ARGB_8888, true);
        imgView.setImageBitmap(Chgbmp);
        
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("処理を実行しています");
        
    }
    
    public void onClickButton(View view){
    	Log.d("IMAGE EFFECT", "onClickButton");
    	
    	final int btn = view.getId();    		
    	if( btn == R.id.button1 ||
    		btn == R.id.button2
    	) {
    		progressDialog.show();
    	}
    	else{
    		return;
    	}
    	// スレッドで処理開始
		new Thread(new Runnable() {
			@Override
			public void run() {
				int add_r=0;
				int add_g=0;
				int add_b=0;
				final Bitmap releasebmp = Chgbmp;
				
				// Java
				if( btn ==  R.id.button1 ){
					add_r=100;
					Chgbmp=Soucebmp.copy(Bitmap.Config.ARGB_8888, true);
					
					start = System.currentTimeMillis();
					chgBitmap(Chgbmp,add_r,add_g,add_b);
					stop = System.currentTimeMillis();
				}
				// NDK
				else if( btn ==  R.id.button2 ){
					add_r=100;
					
					Chgbmp=Soucebmp.copy(Bitmap.Config.ARGB_8888, true);
					
					start = System.currentTimeMillis();
					chgBitmapJNI(Chgbmp,add_r,add_g,add_b);
					stop = System.currentTimeMillis();
				}
			
			    handler.post(new Runnable() {
			    	@Override
			    	public void run() {			    		
			    		imgView.setImageBitmap(Chgbmp);
			    		progressDialog.dismiss();
			    		textView.setText("処理時間：" + (stop - start) + " msec");
			    		releasebmp.recycle();
			    	}
			    });
			}
			
		}).start();
    
    	Log.d("IMAGE EFFECT", "onClickButton END");
    }   
    
    private void chgBitmap(Bitmap chgbmp,int add_r,int add_g, int add_b){
    	Log.d("IMAGE EFFECT", "chgBitmap");
    	
		int width = chgbmp.getWidth();  
		int height = chgbmp.getHeight();  
	     
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int color = chgbmp.getPixel(x, y);
	            int r = Color.red(color) + add_r;
	            int g = Color.green(color) + add_g;  
	            int b = Color.blue(color) + add_b; 
	            
	            // 255以上は255に補正
	            if(r > 255) r=255;
	            if(g > 255) g=255;
	            if(b > 255) b=255;
	            // 0未満は0に補正
	            if(r < 0) r=0;
	            if(g < 0) g=0;
	            if(b < 0) b=0;
	            
	            chgbmp.setPixel(x, y, Color.rgb(r, g, b));            
			}  
		}
    }
    
    public native void chgBitmapJNI(Bitmap source,int add_r,int add_g, int add_b);

    static {
        System.loadLibrary("chbmp-jni");
    }
    
}