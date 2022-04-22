## Jetispot
_not so broken __UNOFFICIAL__ Spotify client for Android_

#### Note that this client will NEVER offer any kind of a downloader/offline caching. Don't ask, seriously. Also, a Spotify Premium account is REQUIRED.

Also, this project's Proguard rules are heavily optimized for the APK size. For now, it is __approx. 2-3 megabytes__.

What's working:
- sign in (login/pass only) 
- "browse", "home", album, artist and genre screens (some of the blocks might be unsupported)
- basic playback w/ Spotify Connect support (NO album/playlist support inside the app + NO in-app UI yet)

What's in progress:
- "Now Playing" UI
- album support (prev/next/queue management)
- better service (audiofocus handling, notification improvements)

Application stack:
- playback: librespot-java as the core + sinks/decoders from librespot-android + Media2 for the mediasession support
- preferences: Jetpack Datastore (proto)
- UI: Jetpack Compose
- arch: MVVM
- DI: Hilt/Dagger

Credits:
- [librespot-java](https://github.com/librespot-org/librespot-java) for the core API part and playback
- [librespot-android](https://github.com/devgianlu/librespot-android) for sink and decoder source (in Jetispot they are rewritten to Kotlin)  
- Google for Jetpack/Hilt
- [moshi](https://github.com/square/moshi/) and [moshix](https://github.com/ZacSweers/MoshiX/) for the undocumented API JSON parsing
