#include <jni.h>
#include <stddef.h>
#include <android/log.h>
#include "NativeUtil.h"
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include "md5.h"
#include "Common.h"
#include "FileSys.h"

#define LOG_TAG "JNI_JSBridge"
#undef LOG
#include <android/log.h>
#define  KCLogI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  KCLogE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)


#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
//    __android_log_print(ANDROID_LOG_INFO,"JNI","JNI onload!!");
	return JNI_VERSION_1_6;
}

JNIEXPORT jboolean JNICALL Java_com_kercer_kerkee_util_KCNativeUtil_fileExists(JNIEnv *env, jclass thiz, jstring path)
{
	const char *cpath = env->GetStringUTFChars(path, JNI_FALSE);
	int exists = file_exists(cpath);
	env->ReleaseStringUTFChars( path, cpath);

	return exists == 1;
}

JNIEXPORT jlong JNICALL Java_com_kercer_kerkee_util_KCNativeUtil_freeDiskSpace(JNIEnv *env, jclass thiz, jstring path)
{
	const char *cpath = env->GetStringUTFChars(path, NULL);
	long freeDiskSpace = free_disk_space(cpath);
	env->ReleaseStringUTFChars(path, cpath);

	return freeDiskSpace;
}

JNIEXPORT jlong JNICALL Java_com_kercer_kerkee_util_KCNativeUtil_lastAccessTime(JNIEnv *env, jclass thiz, jstring path)
{
	const char *cpath = env->GetStringUTFChars(path, NULL);
	long lastAccessTime = last_access_time(cpath);
	env->ReleaseStringUTFChars(path, cpath);

	return lastAccessTime;
}

JNIEXPORT jboolean JNICALL Java_com_kercer_kerkee_util_KCNativeUtil_copyFile(JNIEnv *env, jclass thiz, jstring srcPath, jstring destPath)
{
	const char *cSrcPath = env->GetStringUTFChars(srcPath, NULL);
	const char *cDestPath = env->GetStringUTFChars(destPath, NULL);

	int result = copy_file(cSrcPath, cDestPath);

	env->ReleaseStringUTFChars(srcPath, cSrcPath);
	env->ReleaseStringUTFChars(destPath, cDestPath);

	return result > 0;
}

JNIEXPORT jboolean JNICALL Java_com_kercer_kerkee_util_KCNativeUtil_renameExt(JNIEnv *env, jclass thiz, jstring oldPath, jstring newPath)
{
	const char *cOldPath = env->GetStringUTFChars(oldPath, NULL);
	const char *cNewPath = env->GetStringUTFChars(newPath, NULL);

	int result = rename_ex(cOldPath, cNewPath);

	env->ReleaseStringUTFChars(oldPath, cOldPath);
	env->ReleaseStringUTFChars(newPath, cNewPath);
	return result != -1;
}

JNIEXPORT jboolean JNICALL Java_com_kercer_kerkee_util_KCNativeUtil_createSparseFile(JNIEnv *env, jclass thiz, jstring path, jlong size)
{
	const char *cpath = env->GetStringUTFChars(path, NULL);
	int succeeded = create_sparse_file(cpath, size);
	env->ReleaseStringUTFChars(path, cpath);

	return succeeded == 1;
}

JNIEXPORT void JNICALL Java_com_kercer_kerkee_util_KCNativeUtil_testCrash(JNIEnv *env, jclass thiz)
{
	abort();
}

JNIEXPORT jstring JNICALL Java_com_kercer_kerkee_util_KCNativeUtil_getMd5(JNIEnv *env, jclass thiz, jstring para)
{
	char buf[33] = { '\0' };
	unsigned char digest[16];
	const char *str = env->GetStringUTFChars(para, NULL);
	md5((char *) str, digest);
	env->ReleaseStringUTFChars(para, str);

	int i;
	for (i = 0; i < 16; i++)
	{
		sprintf(&buf[i * 2], "%02x", (unsigned char) digest[i]);
	}

	return env->NewStringUTF(buf);
}

JNIEXPORT jstring JNICALL Java_com_kercer_kerkee_util_KCNativeUtil_getMd5Treated(JNIEnv *env, jclass thiz, jstring para)
{
	static const char *suffix = "dikek*dje9j3{-332k";
	const int suffixlen = strlen(suffix);
	char buf[33] = { '\0' };

	unsigned char digest[16];
	const char *str = env->GetStringUTFChars(para, NULL);
	char *resultstr = (char *) malloc(strlen(str) + suffixlen + 1);
	strcpy(resultstr, str);
	strcat(resultstr, suffix);

	md5((char *) resultstr, digest);

	free(resultstr);

	env->ReleaseStringUTFChars(para, str);

	int i, j;
	for (i = 0; i < 16; i++)
	{
		sprintf(&buf[i * 2], "%02x", (unsigned char) digest[i]);
	}

	char result[17] = { '\0' };
	for (i = 16, j = 0; i < 26; ++i, ++j)
	{
		result[j] = buf[i];
	}
	for (i = 2, j = 10; i < 8; ++i, ++j)
	{
		result[j] = buf[i];
	}

	return env->NewStringUTF(result);

}


/**
 * FileSys
 */
JNIEXPORT jint JNICALL Java_com_kercer_kerkee_util_KCNativeUtil_fileOpen
  (JNIEnv *env, jclass jobj, jstring pFileName, jint openMode)
{
	KCInt8 *pbyFileName = (KCInt8 *)env->GetStringUTFChars(pFileName, 0);
	//int len = (int)env->GetArrayLength(pFileName);
	//pbyFileName[20] = '\0';
//	KCLogI("file name:%s---open type:%d", pbyFileName, openMode);
	return FileOpen(pbyFileName, openMode);
}



JNIEXPORT jint JNICALL Java_com_kercer_kerkee_util_KCNativeUtil_fileLength
  (JNIEnv *env, jclass jobj, jint fd)
{
	return FileLength(fd);
}


JNIEXPORT jint JNICALL Java_com_kercer_kerkee_util_KCNativeUtil_fileSeek
  (JNIEnv *env, jclass jobj, jint fd, jint offset, jint origin)
{
	return FileSeek(fd, offset, origin);
}


JNIEXPORT jobject JNICALL Java_com_kercer_kerkee_util_KCNativeUtil_fileRead
  (JNIEnv *env, jclass jobj, jint fd, jint count)
{
	int nReadLen = 0;
	KCUInt8 *pBuf = (KCUInt8 *)malloc(count);
	memset(pBuf, 0, count);

	nReadLen = FileRead(fd, pBuf, count);

	jbyte *pBy = (jbyte *)pBuf;
	jbyteArray jarray = env->NewByteArray(nReadLen);
	env->SetByteArrayRegion(jarray, 0, nReadLen, pBy);

	jclass    m_cls = env->FindClass("com/jsbridge/browser/util/KCCusBuffer");
	jmethodID m_mid = env->GetMethodID(m_cls, "<init>", "()V");
	jfieldID  m_fid1 = env->GetFieldID(m_cls, "buffer", "[B");
	jfieldID  m_fid2 = env->GetFieldID(m_cls, "nBufferLen", "I");

	jobject   m_obj = env->NewObject(m_cls, m_mid);
	env->SetObjectField(m_obj, m_fid1, jarray);
	env->SetIntField(m_obj, m_fid2, nReadLen);
	return m_obj;
}


JNIEXPORT jint JNICALL Java_com_kercer_kerkee_util_KCNativeUtil_fileWrite
  (JNIEnv *env, jclass jobj, jint fd, jbyteArray buf, jint count)
{
	jbyte *pjb = (jbyte *)env->GetByteArrayElements(buf, 0);
	jsize len = env->GetArrayLength(buf);
	KCUInt8 *byBuf = (KCUInt8 *)pjb;
	pjb[len] = '\0';
	return FileWrite(fd, byBuf, count);
}


JNIEXPORT jint JNICALL Java_com_kercer_kerkee_util_KCNativeUtil_fileClose
  (JNIEnv *env, jclass jobj, jint fd)
{
	return FileClose(fd);
}



#ifdef __cplusplus
}
#endif
