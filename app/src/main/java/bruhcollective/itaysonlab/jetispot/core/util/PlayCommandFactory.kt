package bruhcollective.itaysonlab.jetispot.core.util

import bruhcollective.itaysonlab.jetispot.core.objs.player.*
import com.spotify.dac.player.v1.proto.PlayCommand
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter

@DslMarker
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
annotation class PlayCommandDsl

inline fun playCommand(uri: String, init: (@PlayCommandDsl PlayCommandBuilder).() -> Unit): PlayFromContextData {
    return PlayCommandBuilder(uri).apply(init).build()
}

class PlayCommandBuilder (
    private val contentUri: String
) {
    var contextUri: String = ""
    var skipToUri: String? = null
    var shuffle: Boolean = false

    fun build(): PlayFromContextData {
        require(contextUri.isNotEmpty()) { "Context URI should not be empty!" }
        require(contextUri.startsWith("spotify:")) { "Context URI should be a Spotify URI!" }

        skipToUri?.let {
            require(it.startsWith("spotify:track")) { "Context Skip-To URI should be a Spotify Track URI!" }
        }

        return PlayFromContextData(
            uri = contentUri,
            player = PlayFromContextPlayerData(
                context = PfcContextData(url = "context://$contextUri", uri = contextUri),
                options = PfcOptions(skip_to = skipToUri?.let { PfcOptSkipTo(track_uri = it) }, player_options_override = PfcStateOptions(shuffling_context = shuffle))
            )
        )
    }
}

fun PlayCommand.toApplicationPlayCommand(moshi: Moshi): PlayFromContextData {
    val context = moshi.adapter(PfcContextData::class.java).fromJson(this.context.toStringUtf8())!!
    val options = moshi.adapter(PlayFromContextPlayerData::class.java).fromJson(this.options.toStringUtf8())!!.options!!

    return PlayFromContextData(
        uri = context.uri,
        player = PlayFromContextPlayerData(
            context = context,
            options = options
        )
    )
}