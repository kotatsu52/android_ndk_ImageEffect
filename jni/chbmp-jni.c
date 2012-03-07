#include <string.h>
#include <jni.h>

#include <android/bitmap.h>
#include <android/log.h>

#define  LOG_TAG    "CHGBITMAP"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)


void
Java_my_kotatsu_ImagEffect_ImagEffectActivity_chgBitmapJNI( JNIEnv* env,jobject thiz,
															 jobject bitmap, jint add_r,jint add_g,jint add_b)
{
	int x;
	int y;
	char *pic;
	int chg_rgb[3];
	char r;
	char g;
	char b;
	
	LOGI("chgBitmapJNI START");
	AndroidBitmapInfo info;
	
	if(AndroidBitmap_getInfo(env,bitmap,&info) < 0 ){
		LOGE("AndroidBitmap_getInfo error");
		return;
	}
	
	
	if(info.format != ANDROID_BITMAP_FORMAT_RGBA_8888 ){
		LOGE("Bitmap format error");
		return;
	}
	
	void* pixels;
	
	if( AndroidBitmap_lockPixels(env,bitmap,&pixels) < 0){
		LOGE("AndroidBitmap_lockPixels error");
		return;
	}
	
	pic=pixels;
	for(y=0; y < info.height ; y++){
		for(x=0; x < info.width ; x++){
			
			chg_rgb[0] = ((int)pic[(y*info.width+x)*4  ]) + add_r;
			chg_rgb[1] = ((int)pic[(y*info.width+x)*4+1]) + add_g;
			chg_rgb[2] = ((int)pic[(y*info.width+x)*4+2]) + add_b;
			
			// 255以上は255に補正
			if(chg_rgb[0] > 255) chg_rgb[0]=255;
			if(chg_rgb[1] > 255) chg_rgb[1]=255;
			if(chg_rgb[2] > 255) chg_rgb[2]=255;
			// 0未満は0に補正
			if(chg_rgb[0] < 0) chg_rgb[0]=0;
			if(chg_rgb[1] < 0) chg_rgb[1]=0;
			if(chg_rgb[2] < 0) chg_rgb[2]=0;
			
			pic[(y*info.width+x)*4]   = (char)chg_rgb[0];
			pic[(y*info.width+x)*4+1] = (char)chg_rgb[1];
			pic[(y*info.width+x)*4+2] = (char)chg_rgb[2];
		}
	}
	
	if( AndroidBitmap_unlockPixels(env,bitmap) < 0){
		LOGE("AndroidBitmap_lockPixels error");
		return;
	}
    
	LOGI("chgBitmapJNI END");
}
