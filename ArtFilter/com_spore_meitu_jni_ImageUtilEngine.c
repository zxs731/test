#include <stdlib.h>
#include "com_spore_meitu_jni_ImageUtilEngine.h"

#include <android/log.h>
#include <android/bitmap.h>
#include <math.h>
#define LOG_TAG "libibmphotophun"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

JNIEXPORT jstring JNICALL Java_com_spore_meitu_jni_ImageUtilEngine_getResultFromJni(
		JNIEnv* env, jobject thiz) {
	return (*env)->NewStringUTF(env, "ArtFilter from JNI !!!");
}

int min(int x, int y) {
    return (x <= y) ? x : y;
}
int max(int x,int y){
	return (x >= y) ? x : y;
}
int alpha(int color) {
    return (color >> 24) & 0xFF;
}
int red(int color) {
    return (color >> 16) & 0xFF;
}
int green(int color) {
    return (color >> 8) & 0xFF;
}
int blue(int color) {
    return color & 0xFF;
}
int ARGB(int alpha, int red, int green, int blue) {
    return (alpha << 24) | (red << 16) | (green << 8) | blue;
}

jintArray Java_com_spore_meitu_jni_ImageUtilEngine_toFudiao(JNIEnv* env,
		jobject thiz, jintArray buf, jint width, jint height)
{
	jint * cbuf;
	cbuf = (*env)->GetIntArrayElements(env, buf, 0);
	//jintArray result = (*env)->NewIntArray(env, width * height);
	LOGE("Bitmap Buffer %d %d",cbuf[0],cbuf[1]);

	int newSize = width * height;
	jint rbuf[newSize]; // 新图像像素值

	int count = 0;
	int preColor = 0;
	int prepreColor = 0;
	int color = 0;
	preColor = cbuf[0];

	int i = 0;
	int j = 0;
	for (i = 0; i < width; i++)
	{
		for (j = 0; j < height; j++)
		{
			int curr_color = cbuf[i * width + j];
			int r = red(curr_color) - red(prepreColor) + 128;
			int g = green(curr_color) - red(prepreColor) + 128;
			int b = green(curr_color) - blue(prepreColor) + 128;
			int a = alpha(curr_color);
			int modif_color = ARGB(a, r, g, b);
			rbuf[i * width + j] = modif_color;
			prepreColor = preColor;
			preColor = curr_color;
		}
	}
	jintArray result = (*env)->NewIntArray(env, newSize); // 新建一个jintArray
	(*env)->SetIntArrayRegion(env, result, 0, newSize, rbuf); // 将rbuf转存入result
	(*env)->ReleaseIntArrayElements(env, buf, cbuf, 0); // 释放int数组元素
	return result;
}

jintArray Java_com_spore_meitu_jni_ImageUtilEngine_toHeibai
  (JNIEnv* env,jobject thiz, jintArray buf, jint width, jint height)
{
	jint * cbuf;
	cbuf = (*env)->GetIntArrayElements(env, buf, 0);
	//jintArray result = (*env)->NewIntArray(env, width * height);
	LOGE("Bitmap Buffer %d %d",cbuf[0],cbuf[1]);

	int newSize = width * height;
	jint rbuf[newSize]; // 新图像像素值

	int count = 0;
	int preColor = 0;
	int prepreColor = 0;
	int color = 0;
	preColor = cbuf[0];

	int i = 0;
	int j = 0;
	int iPixel = 0;
	for (i = 0; i < width; i++) {
		for (j = 0; j < height; j++) {
			int curr_color = cbuf[i * width + j];

			int avg = (red(curr_color) + green(curr_color) + blue(curr_color))
					/ 3;
			if (avg >= 100) {
				iPixel = 255;
			} else {
				iPixel = 0;
			}
			int modif_color = ARGB(255, iPixel, iPixel, iPixel);
			rbuf[i * width + j] = modif_color;
		}
	}
	jintArray result = (*env)->NewIntArray(env, newSize); // 新建一个jintArray
	(*env)->SetIntArrayRegion(env, result, 0, newSize, rbuf); // 将rbuf转存入result
	(*env)->ReleaseIntArrayElements(env, buf, cbuf, 0); // 释放int数组元素
	return result;
}

jintArray Java_com_spore_meitu_jni_ImageUtilEngine_toMohu
  (JNIEnv* env,jobject thiz, jintArray buf, jint width, jint height)
{

}

jintArray Java_com_spore_meitu_jni_ImageUtilEngine_toDipian
  (JNIEnv* env,jobject thiz, jintArray buf, jint width, jint height)
{
	jint * cbuf;
	cbuf = (*env)->GetIntArrayElements(env, buf, 0);
	LOGE("Bitmap Buffer %d %d",cbuf[0],cbuf[1]);

	int newSize = width * height;
	jint rbuf[newSize]; // 新图像像素值

	int count = 0;
	int preColor = 0;
	int prepreColor = 0;
	int color = 0;
	preColor = cbuf[0];

	int i = 0;
	int j = 0;
	int iPixel = 0;
	for (i = 0; i < width; i++) {
		for (j = 0; j < height; j++) {
			int curr_color = cbuf[i * width + j];

			int r = 255 - red(curr_color);
			int g = 255 - green(curr_color);
			int b = 255 - blue(curr_color);
			int a = alpha(curr_color);
			int modif_color = ARGB(a, r, g, b);
			rbuf[i * width + j] = modif_color;
		}
	}
	jintArray result = (*env)->NewIntArray(env, newSize); // 新建一个jintArray
	(*env)->SetIntArrayRegion(env, result, 0, newSize, rbuf); // 将rbuf转存入result
	(*env)->ReleaseIntArrayElements(env, buf, cbuf, 0); // 释放int数组元素
	return result;
}

jintArray Java_com_spore_meitu_jni_ImageUtilEngine_toSunshine
  (JNIEnv* env,jobject thiz, jintArray buf, jint width, jint height,jint centerX, jint centerY, jint radius, jint strength)
{
	jint * cbuf;
	cbuf = (*env)->GetIntArrayElements(env, buf, 0);

	int newSize = width * height;
	jint rbuf[newSize]; // 新图像像素值

	radius = min(centerX, centerY);

	int i = 0;
	int j = 0;
	for (i = 0; i < width; i++) {
		for (j = 0; j < height; j++) {
			int curr_color = cbuf[i * width + j];

			int pixR = red(curr_color);
			int pixG = green(curr_color);
			int pixB = blue(curr_color);

			int newR = pixR;
			int newG = pixG;
			int newB = pixB;
			int distance = (int) ((centerY - j) * (centerY - j) + (centerX - i) * (centerX - i));
			if (distance < radius * radius)
			{
				int result = (int) (strength * (1.0 - sqrt(distance) / radius));
				newR = pixR + result;
				newG = pixG + result;
				newB = pixB + result;
			}
			newR = min(255, max(0, newR));
			newG = min(255, max(0, newG));
			newB = min(255, max(0, newB));

			int a = alpha(curr_color);
			int modif_color = ARGB(a, newR, newG, newB);
			rbuf[i * width + j] = modif_color;
		}
	}
	jintArray result = (*env)->NewIntArray(env, newSize); // 新建一个jintArray
	(*env)->SetIntArrayRegion(env, result, 0, newSize, rbuf); // 将rbuf转存入result
	(*env)->ReleaseIntArrayElements(env, buf, cbuf, 0); // 释放int数组元素
	return result;
}
