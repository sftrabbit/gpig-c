# List files that will be included into the report
set(INPUT_FILES
    01-introduction.tex
    02-glossary.tex
    03-requirements.tex
    06-team.tex
    07-riskregister.tex
    08-communication.tex)

# List files that contain content that should be word-counted
set(CONTENT_FILES
    second-report.tex
    ${INPUT_FILES})

# List directories that contain images
set(IMAGE_DIRS
    images
    images/Analysis
    images/DataAbstractionLayer
    images/DataInputLayer
    images/Notification)


### Let everything after here do its thing

foreach(CONTENT_FILE ${CONTENT_FILES})
	set(WORD_COUNT_FILES ${WORD_COUNT_FILES}
	    ${CMAKE_CURRENT_SOURCE_DIR}/${CONTENT_FILE})
endforeach()

add_latex_document(second-report.tex
                   BIBFILES report-refs.bib
                   INPUTS ${INPUT_FILES}
                   IMAGE_DIRS ${IMAGE_DIRS}
                   DEFAULT_PDF
                   MANGLE_TARGET_NAMES)

add_custom_target(second_report_wordcount
                  COMMAND ${CMAKE_SOURCE_DIR}/tools/wordcount.sh
                  ${CMAKE_SOURCE_DIR}
                  ${WORD_COUNT_FILES})

add_dependencies(second-report_pdf second_report_wordcount)
