INCS = -I./include 
OUTDIR = ./output
OBJS = $(OUTDIR)/CommandPromptTest.o 
LIBS = -lPPPagSeguro -lBTSerial -ldl

all: $(OUTDIR)/CommandPromptTest

clean: 
	rm -r $(OBJS) $(OUTDIR)/CommandPromptTest $(OUTDIR)

$(OUTDIR)/CommandPromptTest: main
	gcc  -o $(OUTDIR)/CommandPromptTest $(OBJS) $(LIBS)
	
main:
	mkdir -p $(OUTDIR)
	gcc -D__linux__ $(INCS)  -g3 -Wall -c CommandPromptTest.c -o $(OUTDIR)/CommandPromptTest.o

