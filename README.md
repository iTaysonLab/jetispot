## Jetispot
_not so broken __UNOFFICIAL__ Spotify client for Android_

#### Note that this client will NEVER offer any kind of a downloader/offline caching. Don't ask, seriously. Also, a Spotify Premium account is REQUIRED.

Also, this project's Proguard rules are heavily optimized for the APK size. For now, it is __approx. 3.5-4 megabytes__.

What's working:
- sign in (login/pass only) 
- "browse", "home", album, artist and genre screens (some of the blocks might be unsupported)
- basic library
- basic playback w/ Spotify Connect support (NO in-app UI yet)

What's in progress:
- "Now Playing" UI
- better service (notification improvements)

Application stack:
- playback: librespot-java as the core + sinks/decoders from librespot-android + Media2 for the mediasession support
- UI: Jetpack Compose
- DI: Hilt/Dagger
- storage: Room
- arch: MVVM
- preferences: Jetpack Datastore (proto)

Credits:
- [librespot-java](https://github.com/librespot-org/librespot-java) for the core API part and playback
- [librespot-android](https://github.com/devgianlu/librespot-android) for sink and decoder source (in Jetispot they are rewritten to Kotlin)  
- [moshi](https://github.com/square/moshi/) and [moshix](https://github.com/ZacSweers/MoshiX/) for the undocumented API JSON parsing
- [VK Icons](https://github.com/VKCOM/icons) for the amazing icon set used in the application's icon
- Google for Android/Jetpack/Hilt
