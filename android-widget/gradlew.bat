@echo off
setlocal
set "ROOT=%~dp0"
set "VERSION=8.13"
set "CACHE=%USERPROFILE%\.gradle\lexica-bootstrap"
if defined GRADLE_HOME if exist "%GRADLE_HOME%\bin\gradle.bat" set "GRADLE_BIN=%GRADLE_HOME%\bin\gradle.bat"
if not defined GRADLE_BIN for %%G in (gradle.bat) do @if not "%%~$PATH:G"=="" set "GRADLE_BIN=%%~$PATH:G"
if not defined GRADLE_BIN if exist "%CACHE%\gradle-%VERSION%\bin\gradle.bat" set "GRADLE_BIN=%CACHE%\gradle-%VERSION%\bin\gradle.bat"
if not defined GRADLE_BIN (
  echo Gradle %VERSION% is not installed. Downloading it from services.gradle.org...
  if not exist "%CACHE%\gradle-%VERSION%\bin\gradle.bat" (
    if not exist "%CACHE%" mkdir "%CACHE%"
    powershell -NoProfile -ExecutionPolicy Bypass -Command "$ProgressPreference='SilentlyContinue'; Invoke-WebRequest 'https://services.gradle.org/distributions/gradle-%VERSION%-bin.zip' -OutFile '%TEMP%\gradle-%VERSION%-bin.zip'; Expand-Archive -Force '%TEMP%\gradle-%VERSION%-bin.zip' '%CACHE%'"
    if errorlevel 1 exit /b 1
    rem Archive extraction lands in a nested gradle-VERSION folder under CACHE.
    if exist "%CACHE%\gradle-%VERSION%\bin\gradle.bat" (set "GRADLE_BIN=%CACHE%\gradle-%VERSION%\bin\gradle.bat")
  )
)
if not exist "%ROOT%gradle\wrapper\gradle-wrapper.jar" (
  echo Generating the standard Gradle wrapper for Android Studio...
  call "%GRADLE_BIN%" -p "%ROOT%" wrapper --gradle-version %VERSION% --distribution-type bin
  if errorlevel 1 exit /b 1
)
call "%ROOT%gradlew.bat" %*
exit /b %ERRORLEVEL%
