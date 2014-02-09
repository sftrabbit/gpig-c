set(CONTENT_FILES
    01-introduction.tex
    02-glossary.tex
    03-requirements.tex
    06-team.tex
    07-riskregister.tex
    08-communication.tex)

set(INPUT_FILES
    ${CONTENT_FILES}
    wc.tex)

set(IMAGE_DIRS
    images
    images/Analysis
    images/DataAbstractionLayer
    images/DataInputLayer
    images/Notification)

foreach(CONTENT_FILE ${CONTENT_FILES})
	set(WORD_COUNT_FILES ${WORD_COUNT_FILES}
	    ${CMAKE_CURRENT_SOURCE_DIR}/${CONTENT_FILE})
endforeach()

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
                   INPUTS ${INPUT_FILES}
                   CONFIGURE wc.tex
                   IMAGE_DIRS ${IMAGE_DIRS}
                   DEFAULT_PDF
                   MANGLE_TARGET_NAMES)
