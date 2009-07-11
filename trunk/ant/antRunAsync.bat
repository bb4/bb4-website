@echo off
if exist antRunAsync.js goto RUN_IT
echo ERROR: antRunAsync.js was not found!
goto END
:RUN_IT
cscript //NoLogo antRunAsync.js %1 %2 %3 %4 %5 %6 %7 %8 %9
:END
