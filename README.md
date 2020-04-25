# MMCRPC
This is a simple Java Application, which tracks the last started MultiMC instance and puts it in your Discord RPC.

## Installation
1. Download the jar from the [releases page](https://github.com/BigBotNetwork/MMCRPC/releases) and drop it in a directory (i like C:\MMCRPC)
2. Create a currentcfg.txt in the same directory as the jar
3. Goto MultiMC -> Settings -> Custom Commands (Right Side) -> and paste `java -jar C:\MMCRPC\MMCRPC.jar "$INST_NAME" "C:\MMCRPC\currentcfg.txt"` into the Pre-Launch command line. (You maybe have to change the directory. just replace C:\MMCRPC\ With the directory you dropped the jar). After that you need to paste `java -jar C:\MMCRPC\MMCRPC.jar " " "C:\MMCRPC\currentcfg.txt"` into the Post-exit Command line. (Change the directory if needed)

Now you can start the jar with `java -jar MMCRPC.jar` and the app will change your rpc everytime you start an instance.

If you want you can put a start script in the autostart folder. To open the folder you need to press WIN+R and type `shell:startup`. A Folder opens and you have to create a .bat file with the following content:
```
cd C:\MMCRPC
java -jar MMCRPC.jar
pause
```
