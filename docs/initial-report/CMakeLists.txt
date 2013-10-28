set(WORD_COUNT_FILES
    ${CMAKE_CURRENT_SOURCE_DIR}/initial-report.tex
    ${CMAKE_CURRENT_SOURCE_DIR}/risk-register.tex)

set(TEXCOUNT_COMMAND
    ${CMAKE_SOURCE_DIR}/tools/texcount/texcount.pl)
set(TEXCOUNT_ARGS
    -v0 -brief -nosub -noinc -total ${WORD_COUNT_FILES})

execute_process(COMMAND ${TEXCOUNT_COMMAND} ${TEXCOUNT_ARGS}
                COMMAND sed "-e s/+/ /g"
                COMMAND awk "{ print $1 }"
                OUTPUT_VARIABLE WORD_COUNT
                OUTPUT_STRIP_TRAILING_WHITESPACE)

set(LATEX_OUTPUT_PATH "${CMAKE_BINARY_DIR}/bin/docs")

add_latex_document(initial-report.tex
                   BIBFILES report-refs.bib
                   INPUTS admin-roles.tex func-req-table.tex risklevel-table.tex
                   pert-table.tex risk-register.tex const-req-table.tex
                   tech-roles.tex design-dec-table.tex  nfunc-req-table.tex
                   riskass-table.tex wc.tex
                   CONFIGURE wc.tex
                   IMAGE_DIRS images
                   DEFAULT_PDF
                   MANGLE_TARGET_NAMES)
