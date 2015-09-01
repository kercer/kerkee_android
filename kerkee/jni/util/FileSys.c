#include "FileSys.h"

static FILEOPENED fileopened[MAXOPENFILES] = { 0 };
static KCInt g_nOpenFiles = 0;

KCInt FileOpen(const char *pFileName, KCUInt openMode)
{
	char flag[5] = { 0 };
	FILE *fp = NULL;
	int i = 0;

	if (g_nOpenFiles >= MAXOPENFILES)
	{
		return ERR_FILEOPEN_MUCH;
	}

	memset(flag, 0, 5);

	switch (openMode & FILEMODE_MASK)
	{
		case FILEMODE_READONLY:
			strcpy(flag, "rb");
			break;
		case FILEMODE_WRITE:
			strcpy(flag, "wb");
			break;
		case FILEMODE_READWRITE:
			strcpy(flag, "r+b");
			break;
		case FILEMODE_CREATEWRITE:
			strcpy(flag, "w+b");
			break;
		case FILEMODE_APPEND:
			strcpy(flag, "ab");
			break;
		case FILEMODE_NOSHAREWRITE:
			strcpy(flag, "rb");
			break;
		default:
			return -1;
	}

	fp = fopen(pFileName, flag);

	if (fp == NULL)
	{
		return ERR_FILEOPEN_FAIL;
	}
	else
	{
		for (i = 0; i < MAXOPENFILES; i++)
		{
			if ( NULL == fileopened[i].fp)
			{
				break;
			}
		}

		fileopened[i].fp = fp;
		strcpy(fileopened[i].filename, pFileName);
		g_nOpenFiles++;

		return (KCInt) fp;
	}
}

KCUInt FileLength(KCInt fd)
{
	long p = 0;
	long temp = 0;

	temp = ftell((FILE*) fd);

	fseek((FILE*) fd, 0, SEEK_END);
	p = ftell((FILE*) fd);

	fseek((FILE*) fd, temp, SEEK_SET);

	return p;
}

KCInt FileSeek(KCInt fd, int offset, int origin)
{
	fseek((FILE*) fd, offset, origin);

	return ftell((FILE*) fd);
}

KCInt32 FileRead(KCInt fd, KCUInt8 *buf, KCInt32 count)
{
	return fread(buf, 1, count, (FILE*) fd);
}

KCInt32 FileWrite(KCInt fd, KCUInt8 *buf, KCInt32 count)
{
	return fwrite(buf, 1, count, (FILE*) fd);
}

KCInt FileClose(KCInt fd)
{
	int i = 0;
	long ns = 0;

	ns = fd;

	if (fd != 0)
	{
		if (fclose((FILE*) fd) == 0)
		{
			for (i = 0; i < 20; i++)
			{
				if (fileopened[i].fp == (FILE *) ns)
				{
					break;
				}
			}
			memset(&fileopened[i], 0, sizeof(FILEOPENED));
			g_nOpenFiles--;
			return FILE_SUCCESS;
		}
		else
		{
			return ERR_FILECLOSE_FAIL;
		}
	}
	else
	{
		return FILE_FAIL;
	}
}
