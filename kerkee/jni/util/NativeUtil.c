#include "NativeUtil.h"
#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <stdio.h>
#include <android/log.h>
#include <stdlib.h>
#include <string.h>
#include "md5.h"
#ifndef ANDROID
#include <sys/statvfs.h>
#else
#include <sys/vfs.h>
#define statvfs statfs
#define fstatvfs fstatfs
#endif

int file_exists(const char *path)
{
//    struct stat buffer;
//    return (stat(path, &buffer) == 0);
	if (access(path, F_OK) != -1)
		return 1;
	return 0;
}

// in bytes
long free_disk_space(const char *path)
{
	struct statvfs st;
	if (statvfs(path, &st) == 0)
	{
		return st.f_bsize * st.f_bavail;
	}
	return 0;
}

long last_access_time(const char *path)
{
	struct stat st;
	if (stat(path, &st) == 0)
	{
		return st.st_atime;
	}
	return 0;
}

void last_access_time_batch(const char *path[], long buf[], int count)
{
	memset(buf, 0, count * (sizeof *buf));

	struct stat st;
	int i;

	for (i = 0; i < count; ++i)
	{
		if (stat(path[i], &st) == 0)
		{
			buf[i] = st.st_atime;
		}
	}
}

int copy_file(const char *src, const char *dest)
{
	if(!file_exists(src))
		return -1;
	FILE *fpSrc = fopen(src, "rb");
	FILE *fpDest = fopen(dest, "wb");
	if (NULL == fpSrc || NULL == fpDest)
	{
		fclose(fpSrc);
		fclose(fpDest);
		return -1;
	}

	long nBufSize = 256*1024;
	unsigned char buffer[nBufSize];
	memset(buffer, 0, nBufSize);
	int lenR = 0;
	int lenW = 0;
	unsigned long long bytesCount = 0;
	while ((lenR = fread(buffer, sizeof(char), nBufSize, fpSrc)) > 0)
	{
        if ((lenW = fwrite(buffer, sizeof(char), lenR, fpDest)) != lenR)
        {
        	fclose(fpSrc);
        	fclose(fpDest);
        	return -1;
        }

        bytesCount += lenR * sizeof(char);
		memset(buffer, 0, nBufSize);
	}

//	//use fputc to cpy file
//	int c = EOF;
//	while ((c = fgetc(fpSrc)) != EOF)
//	{
//		fputc(c, fpDest);
//	}

	fclose(fpSrc);
	fclose(fpDest);
	return bytesCount;
}


int rename_ex(const char *oldpath, const char *newpath)
{
	return (rename(oldpath, newpath) == -1 ? 0 : 1);
//   	__android_log_print(ANDROID_LOG_INFO, "DEBUG", ">>> rename: %s, %s", oldpath, newpath);
//	if (rename(oldpath, newpath) == -1) {
	// cannot use copy_file(), sometimes it crashes
//   	    __android_log_print(ANDROID_LOG_INFO, "DEBUG", ">>> copy file: %s, %s", oldpath, newpath);
//        copy_file(oldpath, newpath);
//	    remove(oldpath);
//	}
}

int create_sparse_file(const char *path, long size)
{
	int fd = open(path, O_WRONLY | O_CREAT);
	if (fd)
	{
//    	__android_log_print(ANDROID_LOG_INFO, "DEBUG", ">>> creating sparse file: %s, %ld", path, size);
		return ftruncate(fd, size) == 0 ? 1 : 0;
	}
	return 0;
}

void md5(char *str, unsigned char *digest)
{
	MD5_CTX md5;
	MD5_Init(&md5);
	MD5_Update(&md5, str, strlen(str));
	MD5_Final(digest, &md5);
}

/**
 * get chars from string of mid
 *
 */
char * mid(char *aDes, char *aSrc, int aLen, int aIndex)
{
	char *p = aSrc;
	char *q = aDes;
	int len = strlen(aSrc);
	if (aLen > len)
		aLen = len - aIndex;
	if (aIndex < 0)
		aIndex = 0;
	if (aIndex > len)
		return NULL ;
	p += aIndex;
	while (aLen--)
		*(q++) = *(p++);
	*(q++) = '\0';
	return aDes;
}

