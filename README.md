## Jetispot
_broken __UNOFFICIAL__ Spotify client for Android_

#### Note that this client will NEVER offer any kind of a downloader/offline caching. Don't ask, seriously. Also, a Spotify Premium account is REQUIRED.

Built on Jetpack Compose for UI, Jetpack Datastore for preferences, librespot-java for the API connection and playback, Media3 for session management, and Hilt for DI.

Also, this project's Proguard rules are heavily optimized for the APK size. For now, it is __approx. 3 megabytes__.

What's working:
- sign in (login/pass only) 
- "browse", "home", album, artist and genre screens
- basic playback w/ Spotify Connect support (NO notification displaying yet bcs Media3 API is stupidly difficult)

Credits:
- [librespot-java](https://github.com/librespot-org/librespot-java) for the core API part and playback
- Google for Jetpack/Hilt
- [moshi](https://github.com/square/moshi/) and [moshix](https://github.com/ZacSweers/MoshiX/) for the undocumented API JSON parsing
