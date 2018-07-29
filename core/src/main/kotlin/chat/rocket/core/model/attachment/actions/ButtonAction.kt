package chat.rocket.core.model.attachment.actions

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ButtonAction(
        override val type: String,
        val text: String?,
        val url: String?,
        @field:Json(name = "is_webview") val isWebView: Boolean?,
        @field:Json(name = "webview_height_ratio") val webViewHeightRatio: String?,
        @field:Json(name = "image_url") val imageUrl: String?,
        @field:Json(name = "msg") val message: String?,
        @field:Json(name = "msg_in_chat_window") val isMessageInChatWindow: Boolean?
) : Action
