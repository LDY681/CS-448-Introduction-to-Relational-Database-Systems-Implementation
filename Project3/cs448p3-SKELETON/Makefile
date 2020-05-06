JDKPATH = /usr
LIBPATH = lib/bufmgr.jar:lib/diskmgr.jar:lib/heap.jar:lib/index.jar:lib/junit.jar:lib/org.hamcrest.core_1.3.0.v201303031735.jar
SRCPATH = src/main/java/*/
TESTPATH = src/test/java/*/

CLASSPATH = .:..:$(LIBPATH)
BINPATH = $(JDKPATH)/bin
JAVAC = $(JDKPATH)/bin/javac 
JAVA  = $(JDKPATH)/bin/java 

PROGS = xx

all: $(PROGS)

compile:${SRCPATH}*.java
	$(JAVAC) -cp $(CLASSPATH) -d bin ${SRCPATH}*.java ${TESTPATH}*.java

xx : compile
	$(JAVA) -cp $(CLASSPATH):bin tests.ROTest

