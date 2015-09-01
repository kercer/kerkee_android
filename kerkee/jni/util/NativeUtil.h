#ifndef NATIVEUTIL_H_
#define NATIVEUTIL_H_


#ifdef __cplusplus
extern "C" {
#endif

int file_exists (const char *path);
long free_disk_space (const char *path);
long last_access_time (const char *path);
void last_access_time_batch (const char *path[], long buf[], int count);
int copy_file (const char *src, const char *dest);
int rename_ex (const char *oldpath, const char *newpath);
int create_sparse_file(const char *path, long size);
void md5(char *str, unsigned char *digest);
char* mid(char *dst,char *src, int n,int m);

#ifdef __cplusplus
}
#endif

#endif
