#ifndef _FILESYS_H
#define _FILESYS_H

#include "Common.h"

#ifdef __cplusplus
extern "C" {
#endif

//file mode
#define FILEMODE_MASK           0x07
#define FILEMODE_READONLY       0x00           /*read only*/
#define FILEMODE_WRITE          0x01           /*write*/
#define FILEMODE_READWRITE      0x02           /*read,write*/
#define FILEMODE_APPEND         0x03           /*append*/
#define FILEMODE_CREATEWRITE    0x04           /*if file not exit,creat it first, then open with read and write mode*/
#define FILEMODE_NOSHAREWRITE   0x05           /*not shared for other user to write*/

#define FILESEEK_BEGIN 			0
#define FILESEEK_CURRENT 			0
#define FILESEEK_END 			0


#define MAXOPENFILES            20

#define FILE_SUCCESS            0x0F000000
#define FILE_FAIL               0x0F000001
#define ERR_FILEOPEN_MUCH       0x0F000002
#define ERR_FILEOPEN_FAIL       0x0F000003
#define ERR_FILECLOSE_FAIL      0x0F000004

typedef struct
{
	FILE *fp;
	KCInt8 filename[256];
} FILEOPENED;

KCInt FileOpen(const char *pFileName, KCUInt openMode);
KCUInt FileLength(KCInt fd);
KCInt FileSeek(KCInt fd, int offset, int origin);
KCInt32 FileRead(KCInt fd, KCUInt8 *buf, KCInt32 count);
KCInt32 FileWrite(KCInt fd, KCUInt8 *buf, KCInt32 count);
KCInt FileClose(KCInt fd);

#ifdef __cplusplus
}
#endif

#endif
