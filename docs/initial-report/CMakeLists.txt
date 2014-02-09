set(INPUT_FILES
    admin-roles.tex
    func-req-table.tex
    risklevel-table.tex
    pert-table.tex 
    risk-register.tex
    const-req-table.tex
    tech-roles.tex
    design-dec-table.tex
    nfunc-req-table.tex
    riskass-table.tex)

set(CONTENT_FILES
    initial-report.tex
    risk-register.tex)

set(IMAGE_DIRS
    images)

foreach(CONTENT_FILE ${CONTENT_FILES})
	set(WORD_COUNT_FILES ${WORD_COUNT_FILES}
	    ${CMAKE_CURRENT_SOURCE_DIR}/${CONTENT_FILE})
endforeach()

add_latex_document(initial-report.tex
                   BIBFILES report-refs.bib
                   INPUTS ${INPUT_FILES}
                   IMAGE_DIRS ${IMAGE_DIRS}
                   DEFAULT_PDF
                   MANGLE_TARGET_NAMES)

add_custom_target(initial_report_wordcount
                  COMMAND ${CMAKE_SOURCE_DIR}/tools/wordcount.sh
                  ${CMAKE_SOURCE_DIR}
                  ${WORD_COUNT_FILES})

add_dependencies(initial-report_pdf initial_report_wordcount)
