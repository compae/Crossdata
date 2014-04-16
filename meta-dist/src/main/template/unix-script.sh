#!/bin/sh
@LICENSE_HEADER@

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

PRGDIR=`dirname "$PRG"`
BASEDIR=`cd "$PRGDIR/.." >/dev/null; pwd`


if [ -z "$META_CONF" ]; then
    META_CONF="$BASEDIR/conf"
fi

if [ -f "$META_CONF/meta-env.sh" ]; then
    source "$META_CONF/meta-env.sh"
fi

if [ -z "$META_HOME" ]; then
    META_HOME="$BASEDIR"
fi

if [ -z "$META_LIB" ]; then
    META_LIB="$META_HOME/lib"
fi

if [ -z "$META_BIN" ]; then
    META_BIN="$META_HOME/bin"
fi

echo "META_HOME = $META_HOME"
echo "META_CONF = $META_CONF"
echo "META_LIB  = $META_LIB"
echo "META_BIN  = $META_BIN"


# Reset the REPO variable. If you need to influence this use the environment setup file.
REPO=
@ENV_SETUP@

# OS specific support.  $var _must_ be set to either true or false.
if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD=`which java`
  fi
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly." 1>&2
  echo "  We cannot execute $JAVACMD" 1>&2
  exit 1
fi

if [ -z "$REPO" ]
then
  REPO="$BASEDIR"/@REPO@
fi

CLASSPATH="$CLASSPATH:$META_CONF/:$META_LIB/*:"


exec "$JAVACMD" ${JAVA_OPTS} @EXTRA_JVM_ARGUMENTS@ \
  -classpath "$CLASSPATH" \
  -Dapp.name="@APP_NAME@" \
  -Dapp.pid="$$" \
  @MAINCLASS@ \
  @APP_ARGUMENTS@"$@"@UNIX_BACKGROUND@