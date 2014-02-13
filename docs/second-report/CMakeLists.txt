# List all files that are included by main tex file
set(INPUT_FILES
    introduction.tex
    glossary.tex
    requirements.tex
    team.tex
    riskregister.tex
    communication.tex)

# List all files that should be word-counted
set(CONTENT_FILES
    second-report.tex
    ${INPUT_FILES})

# List directories containing images
set(IMAGE_DIRS
    images
    images/Analysis
    images/DataAbstractionLayer
    images/DataInputLayer
    images/Notification)

gpigc_add_report(second-report.tex
                 INPUT_FILES ${INPUT_FILES}
                 CONTENT_FILES ${CONTENT_FILES}
                 IMAGE_DIRS ${IMAGE_DIRS})
