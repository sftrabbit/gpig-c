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

- **Core** - The core GPIG-C system, responsible for receiving data, storing
  and analysing it, and sending out notifications.
- **Emitter** - A library that provides common functionality for the following
  emitters:
  - **Test App 1 Emitter** - Emits CPU and memory data about the first
    Thales test application.
  - **Earthquake Emitter** - Emits data about earthquakes gathered from USGS
    Earthquake Hazards Program.
  - **Response Time Emitter** - Emits HTTP response time data.
  - **Android Emitter** - An Android application that contains various emitters.
- User-facing components:
  - **Emitter Launcher** - Provide a GUI for launching the desktop emitters.
  - **Admin Centre** - A web interface for administrating a GPIG-C system.
- Storage:
  - **GPIG-WebApp** - A web application for Google App Engine that provides write
    and read access to a database for storing data from the monitored application.
- Utility:
  - **Proto** - Compiles Protocol Buffers message specifications into a Java
    library so that it can be used to communicate between the data emitters and
    the Core.
