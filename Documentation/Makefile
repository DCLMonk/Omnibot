#Thesis Makefile
LATEX= pdflatex						#Which latex to run
BIB= bibtex							#What to run for biliography
THESIS= report						#Which file to run it on
EDITOR= geany


.PHONY: all figures pdf clean open

all: figures pdf

figures:
#	$(FIGRUN) $(FIGLOC)
	
pdf:
	$(LATEX) $(THESIS)
#	$(BIB) $(THESIS)
	$(LATEX) $(THESIS)
	$(LATEX) $(THESIS)

open:
	$(EDITOR) *tex chapters/*tex appendix/*tex *bib &

clean:
	rm -f *pdf *aux *log *bbl *blg ./chapters/*pdf ./chapters/*aux ./chapters/*log ./appendix/*pdf ./appendix/*aux ./appendix/*log ./figures/*pdf
