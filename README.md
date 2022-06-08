</p>
## Jetispot
_probably usable __UNOFFICIAL__ Spotify client for Android, built with Jetpack Compose and librespot-java_

#### Spotify Premium account is REQUIRED*. Offline caching, DRM bypassing or raw file downloading is prohibted by ToS and will NEVER be implemented in Jetispot. Don't waste your time trying to request these features.

__What's working:__
- sign in (login/pass only, no FB/Meta/whatsoever support, no Smart Lock either) 
- "browse", "home", album, premium plan, artist and genre screens (some of the blocks might be unsupported)
- library: "liked songs" w/ tag&sort support, rootlist (liked playlists) + pins + artist/album support w/ nice animations, delta updates + pub/sub processing support
- basic playback w/ Spotify Connect support (connect support is very WIP)
- fairly optimized R8 rules, providing __approx. 5-6 megabytes__ release APK size (with the playback and protobuf parts!)

__What's in progress:__
- "Now Playing" improvements
- better service (notification improvements)

__Application stack:__
- playback: librespot-java as the core + sinks/decoders from librespot-android + Media2 for the mediasession support
- UI: Jetpack Compose
- DI: Hilt/Dagger
- network: Retrofit w/ Moshi+Protobuf converters
- pictures: Coil
- storage: Room (collection), MMKV (metadata)
- arch: MVVM
- preferences: Jetpack Datastore (proto)

__Credits:__
- [librespot-java](https://github.com/librespot-org/librespot-java) for the core API part and playback
- [librespot-android](https://github.com/devgianlu/librespot-android) for sink and decoder source (in Jetispot they are rewritten to Kotlin)  
- [moshi](https://github.com/square/moshi/) and [moshix](https://github.com/ZacSweers/MoshiX/) for the undocumented API JSON parsing
- [VK Icons](https://github.com/VKCOM/icons) for the amazing icon set used in the application's icon
- [MMKV](https://github.com/Tencent/MMKV) for ultra-fast way to cache entity extended metadata
- Google for Android/Jetpack/Hilt

_* I heard some people can log in with a free account, but I won't provide any assistance to people without premium subscription. There is a possibility that a subscription check may be added to the client side in the future._

Now Playing design credits to <a href="https://dribbble.com/shots/15706284-Spotify-You-Material-You-Redesign/attachments/7505834?mode=media" target="_blank">Nazım Can Tunç</a>
<p>Now Playing design implementation credits to <a href="https://t.me/DotsTeaLab/119" target="_blank">Dot's Tea Lab</a>