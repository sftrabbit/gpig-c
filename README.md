GPIG-C
======

A tailorable HUMS solution.

**Sense. Store. Analyse. Report.**

Directory structure
-------------------

Main directories:

- `projects` - all subprojects have directories in here
- `docs` - documentation source (including reports)

Auxiliary directories:

- `tools` - external tools required to build gpig-c
- `CMakeModules` - modules for the CMake build system to use

Projects
--------

- **Admin Centre** - A web interface for administering a GPIG-C system.
- **Core** - The core GPIG-C system, responsible for receiving data, storing
  and analysing it, and sending out notifications.
- **Data Emitter** - Contains wrappers for various applications that need to be
  monitored. It extracts data from the applications and emits it to the GPIG-C
  system.
- **GPIG-WebApp** - A web application for Google App Engine that provides write
  and read access to a database for storing data from the monitored application.
- **Proto** - Compiles Protocol Buffers message specifications into a Java
  library so that it can be used to communicate between the Data Emitter and
  the Core.
