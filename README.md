# EMGFlappy
**A cross-platform port of the EMGame game**

## Running development builds
> [See libGDX documentation](https://libgdx.badlogicgames.com/documentation/gettingstarted/Running%20and%20Debugging.html)


### Android
1. [Install Android Studio](https://developer.android.com/studio)
1. Open project in Android Studio
2. Choose to run either in an emulator or in a physical device

Notes:
* When first time starting the project Android Studio should prompt to install all the requisites
* Running in a physical device requires unlocking Developer Settings and enabling USB debugging


### Desktop
***The following instructions are for Android Studio (based on [libGDX wiki](https://github.com/libgdx/libgdx/wiki/Gradle-and-Intellij-IDEA))***

1. Create a Desktop run profile
 1. Click "Run" (top navigation bar)
 2. Select "Edit Configurations..." from the dropdown menu
 3. Click "+" symbol which is in the top left corner of the popup dialog
 4. Select "Application" from the dropdown menu
 5. Set "Name": ***Desktop***
 6. Set "Main class": ***games.emgflappy.project.desktop.DesktopLauncher***
 7. Click "Ok"
2. Select "Desktop" as the run profile, defaults to "android"
3. Run application normally (just like Android application)

Notes:
* Debugger and Logcat do not work when the application is run like described above


### Web, HTML
1. Navigate to the base directory of the project: EMGFlappy/
2. Start the development server setup <br>&ensp;Linux: ```./gradlew html:superDev ``` <br>&ensp;Windows:  ``` gradlew html:superDev```
3. Browse ```http://localhost:8080/index.html```


### iOS
- Not tested
