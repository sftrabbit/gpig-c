# List all files that are included by main tex file
set(INPUT_FILES
    listingsPreamble.tex)

# List all files that should be word-counted
set(CONTENT_FILES
    manual.tex)

# List directories containing images
set(IMAGE_DIRS 
	images)

gpigc_add_report(manual.tex
                 INPUT_FILES ${INPUT_FILES}
                 CONTENT_FILES ${CONTENT_FILES}
                 IMAGE_DIRS ${IMAGE_DIRS})
