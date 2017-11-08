package chat.rocket.core.model

import chat.rocket.common.model.BaseRoom
import com.squareup.moshi.Json

data class Room(
        @Json(name = "t") override val roomType: BaseRoom.RoomType?
) : BaseRoom