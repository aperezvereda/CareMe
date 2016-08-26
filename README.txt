Instructions:

Android Studio is the platform used to implement the app
The minimum SDK required is 16 and target 22, so install with the SDK manager the required packages

Nimbees functionality is unable, this means that the app won’t communicate with the caregiver app. However, the Nimbees code is still there, just commented in class AccumulatorService and in app.properties. To fulfill the app.properties you will have to create an account at Nimbees website and follow the instructions there to get all the necessary codes.

You can open the project with Android Studio and the Gradle build will do the rest, once imported, you can press play button and it will install on the smartphone plugged to the pc, you will need to activate the debug mode on the smartphone, press 7 times the compilation number at settings->phone information. A new menu at settings will unlock with the developer tools, activate the USB debug and you will be ready.
Another way of installing the app is going to the project folder app->build->outputs and you will find the app to install the app.

Once installed the app, the first window is to connect with the caregiver, here you can put whatever you want because it is unabled. Then everything will start to work and the main view will open, here you can’t do anything, only, in the three dots menu, the print data option will generate a .txt file called RoutineLog.txt containing the data in the db at the moment.

The phone will only make an analysis and update the bd when you plug the phone to charge the battery, you don’t need to open the app, a little message should appear saying “Empezamos analyiis”.

Some functionalities are still unimplemented like the old data deleting, but the app is completely functional. If you have any doubt just mail me and I will answer as son as possible.
