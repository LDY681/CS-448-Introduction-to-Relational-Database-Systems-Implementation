#
# Makefile for Minibase Parser
# CS 448, Spring 2020, Purdue University
#
# This Makefile was designed for Linux.
# For Windows (cygwin), change each : to \; in the variables below.
#

SRCPATH = .:..
BINPATH = ../../bin
LIBPATH = ../../tools/javacc.jar

JAVAC = javac -d $(BINPATH) -sourcepath $(SRCPATH):$(LIBPATH)
JAVA  = java -classpath $(BINPATH):$(LIBPATH)


all: parser

parser:
	$(JAVA) jjtree MiniSql.jjt
	$(JAVA) javacc MiniSql.jj

build:
	$(JAVAC) *.java

test:
	$(JAVA) parser.MiniSql

clean:
	rm -f *.class *~ \#* MiniSql.jj JJTMiniSqlState.java MiniSql.java MiniSqlConstants.java MiniSqlTokenManager.java MiniSqlTreeConstants.java Node.java ParseException.java SimpleCharStream.java SimpleNode.java Token.java TokenMgrError.java
