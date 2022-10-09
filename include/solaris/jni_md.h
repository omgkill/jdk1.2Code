/*
 * @(#)jni_md.h	1.12 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

#ifndef _JNI_MD_H
#define	_JNI_MD_H

#pragma ident	"@(#)jni_md.h	1.12	01/11/29 SMI"

/*
 * <sys/types.h> and <limits.h> will nest include <sys/int_types.h> and
 * <sys/int_limits.h> on Solaris systems which support the ANSI C
 * <inttypes.h> fixed width types (i.e.: Solaris 2.6 and later).
 */
#include <sys/isa_defs.h>
#include <sys/types.h>
#include <limits.h>

#ifdef	__cplusplus
extern "C" {
#endif

/*
 * These (needed for Windows and possibly other ports) are null on Solaris.
 */
#define	JNIEXPORT
#define	JNIIMPORT
#define	JNICALL

/*
 * Define the ANSI C fixed width types on systems which don't define them
 * (i.e.: Solaris 2.5.1).  The ANSI C supported way to check for the
 * existance of a wixed width type is to check for the existance of a
 * define of a maximum value: see the comment in Solaris's <sys/int_limits.h>
 * implementation of the ANSI C defined <inttypes.h>.
 *
 * The defines of _UINT8_T, _UINT32_T, and _UINT64_T are required for
 * interoperability with <sys/synch.h> on Solaris 2.5.1 which also
 * defines these fixed width types.
 */
#if !defined(INT8_MAX)
#if defined(_CHAR_IS_SIGNED)
typedef char			int8_t;
#else
#if defined(__STDC__)
typedef signed char		int8_t;
#endif
#endif
#endif

#if !defined(UINT8_MAX) && !defined(_UINT8_T)
#define	_UINT8_T
typedef unsigned char		uint8_t;
#endif

#if !defined(INT16_MAX)
typedef short			int16_t;
#endif

#if !defined(UINT16_MAX)
typedef unsigned short		uint16_t;
#endif

#if !defined(INT32_MAX)
typedef int			int32_t;
#endif

#if !defined(UINT32_MAX) && !defined(_UINT32_T)
#define	_UINT32_T
typedef unsigned int		uint32_t;
#endif

#if !defined(INT64_MAX)
#ifdef  _LP64
typedef long			int64_t;
#else   /* _ILP32 */
#if __STDC__ - 0 == 0 && !defined(_NO_LONGLONG)
typedef long long		int64_t;
#endif
#endif
#endif

#if !defined(UINT64_MAX) && !defined(_UINT64_T)
#define	_UINT64_T
#ifdef  _LP64
typedef unsigned long		uint64_t;
#else   /* _ILP32 */
#if __STDC__ - 0 == 0 && !defined(_NO_LONGLONG)
typedef unsigned long long	uint64_t;
#endif
#endif
#endif

#ifdef	__cplusplus
}
#endif

#endif /* _JNI_MD_H */
