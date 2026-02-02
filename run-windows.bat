@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "ASSETS_DIR=%SCRIPT_DIR%assets"
set "JAR_FILE="

for %%F in ("%SCRIPT_DIR%sonicBarcoBasurero-*-win.jar") do set "JAR_FILE=%%~fF"
if not defined JAR_FILE (
  for %%F in ("%SCRIPT_DIR%lwjgl3\build\libs\sonicBarcoBasurero-*-win.jar") do set "JAR_FILE=%%~fF"
)

if not defined JAR_FILE (
  echo No se encontro el JAR de Windows.
  echo Ejecuta: gradlew.bat lwjgl3:jarWin
  pause
  exit /b 1
)

if not exist "%ASSETS_DIR%\" (
  echo No se encontro la carpeta assets junto al .bat.
  pause
  exit /b 1
)

pushd "%ASSETS_DIR%"
java -jar "%JAR_FILE%"
set "EXITCODE=%ERRORLEVEL%"
popd
exit /b %EXITCODE%
