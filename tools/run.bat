SET DIR=%~dp0
START javaw -Dorg.apache.jasper.compiler.disablejsr199=true -jar "%DIR%/Core.jar" > NUL
START javaw -jar "%DIR%/EmitterLauncher.jar" > NUL
