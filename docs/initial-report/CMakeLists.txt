# List all files that will be included by main tex file
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

# List all files that should be word-counted
set(CONTENT_FILES
    initial-report.tex
    risk-register.tex)

# List directories containing images
set(IMAGE_DIRS
    images)

gpigc_add_report(initial-report.tex
                 INPUT_FILES ${INPUT_FILES}
                 CONTENT_FILES ${CONTENT_FILES}
                 IMAGE_DIRS ${IMAGE_DIRS})
