# Appium-AutoGrid
Starts selenium grid hub &amp; adds appium nodes for all the devices connected to the PC on its on.

<b>Reason to come up with this :</b> When i got a requirement to run test parallel / serial in multiple devices i started exploring selenium grid. Most of the articles / videos in youtube suggested to create config for each devices. Then start selenium jar, manually trigger all those appium nodes in cmd.exe in different ports for each devices. And execute test program by mentioning same device details, port in capabilities of code and do execution this was the general instruction.
What a drag couldnt take it :( 
So i automated the whole damn thing.
With this you just have to connect the device, make sure its debbugable and then run test it take cares rest for you.

<b>Dependencies:</b>
1. Windows 7 or above
2. java 8 or above (Java JDK path set as "JAVA_HOME" in enviroment variables)
3. Node js (Node.exe path set in enviroment variables)
4. ADB (Android SDK path set as ANDROID_HOME or atleast adb.exe path set in enviroment variables)
5. Appium (command line version must be installed) --npm install can be used for the same. 

# FYI its still under test phase so i'm expecting bugs to be present in this.
I'm still working on few optimisations lets see how better things get.
