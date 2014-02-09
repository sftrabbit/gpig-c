FUNCTION(GPIGC_LIST_CONTAINS var value)
  SET(input_list ${ARGN})
  LIST(FIND input_list "${value}" index)
  IF (index GREATER -1)
    SET(${var} TRUE PARENT_SCOPE)
  ELSE (index GREATER -1)
    SET(${var} PARENT_SCOPE)
  ENDIF (index GREATER -1)
ENDFUNCTION(GPIGC_LIST_CONTAINS)

FUNCTION(GPIGC_PARSE_ARGUMENTS prefix arg_names)
  SET(DEFAULT_ARGS)
  FOREACH(arg_name ${arg_names})
    SET(${prefix}_${arg_name} CACHE INTERNAL "${prefix} argument" FORCE)
  ENDFOREACH(arg_name)

  SET(current_arg_name DEFAULT_ARGS)
  SET(current_arg_list)
  FOREACH(arg ${ARGN})
    GPIGC_LIST_CONTAINS(is_arg_name ${arg} ${arg_names})
    IF (is_arg_name)
      SET(${prefix}_${current_arg_name} ${current_arg_list}
        CACHE INTERNAL "${prefix} argument" FORCE)
      SET(current_arg_name ${arg})
      SET(current_arg_list)
    ELSE (is_arg_name)
      SET(current_arg_list ${current_arg_list} ${arg})
    ENDIF (is_arg_name)
  ENDFOREACH(arg)
  SET(${prefix}_${current_arg_name} ${current_arg_list}
    CACHE INTERNAL "${prefix} argument" FORCE)
ENDFUNCTION(GPIGC_PARSE_ARGUMENTS)

FUNCTION(PARSE_ADD_REPORT_ARGUMENTS command)
  GPIGC_PARSE_ARGUMENTS(
    GPIGC
    "INPUT_FILES;CONTENT_FILES;IMAGE_DIRS"
    ${ARGN}
    )

  IF (GPIGC_DEFAULT_ARGS)
    LIST(GET GPIGC_DEFAULT_ARGS 0 main_input)
    LIST(REMOVE_AT GPIGC_DEFAULT_ARGS 0)

    GET_FILENAME_COMPONENT(name ${main_input} NAME)
    STRING(REGEX REPLACE "\\.[^.]*\$" "" target "${name}")

    SET(GPIGC_MAIN_INPUT ${main_input} CACHE INTERNAL "" FORCE)
    SET(GPIGC_TARGET ${target} CACHE INTERNAL "" FORCE)
  ELSE (GPIGC_DEFAULT_ARGS)
    message(FATAL_ERROR "No tex file target given to ${command}.")
  ENDIF (GPIGC_DEFAULT_ARGS)

  IF (GPIGC_DEFAULT_ARGS)
    message(FATAL_ERROR "Invalid or depricated arguments: ${GPIGC_DEFAULT_ARGS}")
  ENDIF (GPIGC_DEFAULT_ARGS)
ENDFUNCTION(PARSE_ADD_REPORT_ARGUMENTS)

FUNCTION(GPIGC_ADD_REPORT)
  PARSE_ADD_REPORT_ARGUMENTS(GPIGC_ADD_REPORT ${ARGV})

  foreach(GPIGC_CONTENT_FILE ${GPIGC_CONTENT_FILES})
    set(GPIGC_WORD_COUNT_FILES ${GPIGC_WORD_COUNT_FILES}
        ${CMAKE_CURRENT_SOURCE_DIR}/${GPIGC_CONTENT_FILE})
  endforeach()

  add_latex_document(${GPIGC_MAIN_INPUT}
                     BIBFILES report-refs.bib
                     INPUTS ${GPIGC_INPUT_FILES}
                     IMAGE_DIRS ${GPIGC_IMAGE_DIRS}
                     DEFAULT_PDF
                     MANGLE_TARGET_NAMES)

  add_custom_target(${GPIGC_TARGET}_wordcount
                    COMMAND ${CMAKE_SOURCE_DIR}/tools/wordcount.sh
                    ${CMAKE_SOURCE_DIR}
                    ${GPIGC_WORD_COUNT_FILES})

  add_dependencies(${GPIGC_TARGET}_pdf ${GPIGC_TARGET}_wordcount)
ENDFUNCTION(GPIGC_ADD_REPORT)
