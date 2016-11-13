# WeatherQuote
Android native weather app developed with Android Studio 2.2.2.

Available in the Google Play Store:
https://play.google.com/store/apps/details?id=com.angelosoft.angelo_romel.weatherquote

You need to provide your own API keys to the following locations if you wish you test the application:

strings.xml
<!--openweathermap api key/appid-->
    <string name="weather_appid">Your api key goes here</string>

    <!--firebase url-->
    <string name="firebase_url">Your Firebase url goes here</string>

<!--AdMob-->
    <string name="banner_ad_unit_id">Your AdMob banner ad unit id goes here</string>
    <string name="interstitial_ad_unit_id">Your AdMob interstitial ad unit id goes here</string>

<!--firebase url-->
    <string name="firebase_url">Your Firebase url goes here</string>


AndroidManifest.xml
<meta-data
            android:name="com.google.android.gms.version"
            android:value="@integer/google_play_services_version" />
        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="Your api key goes here" />
            
**The Firebase URL is optional. The initial production build of the app used Firebase authentication and synchronized cloud storage
across multiple devices. The classes with the word "auth" in their names uses Firebase authentication and cloud data storage/retrieval. 
Please ignore these classes if you are not using Firebase.

**Dependencies:
dependencies {
    compile fileTree(include: ['*.jar'], dir: 'libs')
    testCompile 'junit:junit:4.12'
    compile 'com.android.support:appcompat-v7:24.2.0'
    compile 'com.firebase:firebase-client-android:2.5.2'
    compile 'com.android.support:design:24.2.0'
    compile 'com.android.support:recyclerview-v7:24.2.0'
    compile 'com.google.android.gms:play-services-location:9.0.1'
    compile 'com.google.code.gson:gson:2.6.2'
    compile 'com.google.firebase:firebase-ads:9.0.1'
    compile 'com.github.clans:fab:1.6.2'
}

Weather forecast and api available at:
https://openweathermap.org/api

Firebase available at:
https://firebase.google.com/

AdMob available at:
https://www.google.com/admob/landing/sign-up-003a.html?subid=emea-semexp3a-r3&gclid=CJ-Cm9HFpdACFUcQ0wodg-oKbw
