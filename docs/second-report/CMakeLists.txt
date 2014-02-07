set(WORD_COUNT_FILES
    ${CMAKE_CURRENT_SOURCE_DIR}/01-introduction.tex
    ${CMAKE_CURRENT_SOURCE_DIR}/02-glossary.tex
    ${CMAKE_CURRENT_SOURCE_DIR}/03-requirements.tex
    ${CMAKE_CURRENT_SOURCE_DIR}/06-team.tex
    ${CMAKE_CURRENT_SOURCE_DIR}/07-riskregister.tex
    ${CMAKE_CURRENT_SOURCE_DIR}/08-communication.tex
    )

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

add_latex_document(second-report.tex
                   BIBFILES report-refs.bib
                   INPUTS wc.tex
                   01-introduction.tex
                   02-glossary.tex
                   03-requirements.tex
                   06-team.tex
                   07-riskregister.tex
                   08-communication.tex
                   CONFIGURE wc.tex
                   IMAGE_DIRS images
                   DEFAULT_PDF
                   MANGLE_TARGET_NAMES)
