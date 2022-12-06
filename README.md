<div align="center">
</div>
<h1 align="center">Jetispot</h1>

<div align="center">
  
A Spotify unofficial client built with Jetpack Compose, Material You/3 and librespot-java

</div>

## 📣 NOTICE
Spotify Premium account is **REQUIRED***. Offline caching, DRM bypassing or raw file downloading is prohibted by ToS and will NEVER be implemented in Jetispot. Don't waste your time trying to request these features.

## 🔮 App features
- Sign In (login/pass only, no FB/Meta/Google support, no Smart Lock either) 
- "Browse", "Home", Album, Premium Plans overview, Artists and Genres screens (some of the blocks might be unsupported).
- Library: "liked songs" with tag & sort support, rootlist (liked playlists) + pins + artist/album support with nice animations, delta updates and also pub/sub processing support
- Basic playback with Spotify Connect support (Spotify Connect support is actually WIP)
- Fairly optimized R8 rules, providing the release APKs with a size of 4-5mb (with the playback and protobuf parts!)

## 📸 Screentshots

<div>
<img width="300" alt="image" src="https://user-images.githubusercontent.com/60316747/205959791-b3f3098b-0d39-42b3-a4d0-3747245a8511.jpg" />
<img width="300" alt="image" src="https://user-images.githubusercontent.com/60316747/205959820-481963c3-6159-4ccd-adea-f788d7480d83.jpg" />
<img width="300" alt="image" src="https://user-images.githubusercontent.com/60316747/205959868-e82b2278-b1a8-4577-a485-486dec5d9f11.jpg" />
<img width="300" alt="image" src="https://user-images.githubusercontent.com/60316747/205959884-25f94cd1-1dae-47ad-8061-c49f20c2d99b.jpg" />
<img width="300" alt="image" src="https://user-images.githubusercontent.com/60316747/205959913-c855d0b9-39a7-4449-86a6-a6674bee8457.jpg" />
<img width="300" alt="image" src="https://user-images.githubusercontent.com/60316747/205959936-0e064bd6-9731-4015-8294-cff997a1572b.jpg" />
</div>


## 🔨 What's in progress
- "Now Playing" improvements
- Better playback service (notification improvements)
- Fixing "unsupported" warnings

## 👷 App specifications
- Playback: librespot-java as the core + sinks/decoders from librespot-android + Media2 for the mediasession support
- UI: Jetpack Compose with Material You
- DI: Hilt/Dagger
- network: Retrofit w/ Moshi + Protobuf converters
- pictures: Coil
- storage: Room (collection), MMKV (metadata)
- arch: MVVM
- preferences: Jetpack Datastore (proto) [maybe in a future MMKV will be used for some app variables]

## ⬇️ Downloads
You can go to the [releases page](https://github.com/BobbyESP/Jetispot/releases) and download any version updated.

## Credits
- [librespot-java](https://github.com/librespot-org/librespot-java) for the core API part and playback
- [librespot-android](https://github.com/devgianlu/librespot-android) for sink and decoder source (in Jetispot they are rewritten to Kotlin)  
- [moshi](https://github.com/square/moshi/) and [moshix](https://github.com/ZacSweers/MoshiX/) for the undocumented API JSON parsing
- [VK Icons](https://github.com/VKCOM/icons) for the amazing icon set used in the application's icon
- [MMKV](https://github.com/Tencent/MMKV) for ultra-fast way to cache entity extended metadata
- Google for Jetpack Compose, Protocol Buffers and Material UI components

_* Some people can actually login without an Spotify Premium account. Assistance to this accounts may be not be provided and you risk yourself for using a free acount. We will not be held responsible for any ban._
