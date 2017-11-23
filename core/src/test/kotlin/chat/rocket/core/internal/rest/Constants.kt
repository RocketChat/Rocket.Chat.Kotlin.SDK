package chat.rocket.core.internal.rest

const val DEFAULT_TIMEOUT: Long = 2000

const val LOGIN_SUCCESS = "{\"status\": \"success\",\"data\": {\"authToken\": \"authToken\",\"userId\": \"userId\"}}"
const val LOGIN_ERROR = "{\"status\": \"error\",\"message\": \"Unauthorized\"}"

const val REGISTER_SUCCESS = "{\"user\":{\"_id\":\"userId\",\"createdAt\":\"2017-11-07T15:59:50.432Z\",\"services\":{\"password\":{\"bcrypt\":\"bcrypt-password\"},\"email\":{\"verificationTokens\":[{\"token\":\"verificationToken\",\"address\":\"test@email.com\",\"when\":\"2017-11-07T15:59:50.457Z\"}]}},\"emails\":[{\"address\":\"test@email.com\",\"verified\":false}],\"type\":\"user\",\"status\":\"offline\",\"active\":true,\"name\":\"Test User\",\"_updatedAt\":\"2017-11-07T15:59:51.144Z\",\"roles\":[\"user\"],\"username\":\"testuser\"},\"success\":true}"
const val REGISTER_FAIL_EMAIL_IN_USE = "{\"success\":false,\"error\":\"Email already exists. [403]\",\"errorType\":403}"
const val REGISTER_FAIL_USER_IN_USE = "{\"success\":false,\"error\":\"<strong>testuser</strong> is already in use :( [error-field-unavailable]\",\"errorType\":\"error-field-unavailable\"}"

const val ME_SUCCESS = "{\"_id\":\"userid\",\"name\":\"testuser\",\"emails\":[{\"address\":\"testuser@test.com\",\"verified\":false}],\"status\":\"offline\",\"statusConnection\":\"offline\",\"username\":\"testuser\",\"utcOffset\":-3,\"active\":true,\"success\":true}"
const val ME_UNAUTHORIZED = "{\"status\":\"error\",\"message\":\"You must be logged in to do this.\"}"

const val USER_SUBSCRIPTIONS_OK = "{\"channels\":[{\"_id\":\"GENERAL\",\"ts\":\"2017-10-20T12:51:33.778Z\",\"t\":\"c\",\"name\":\"general\",\"msgs\":23,\"default\":true,\"_updatedAt\":\"2017-11-17T16:16:04.654Z\",\"lm\":\"2017-11-06T16:02:00.611Z\",\"usernames\":[\"testuser\",\"testuser1\"]}],\"offset\":0,\"count\":1,\"total\":1,\"success\":true}"

const val SEND_MESSAGE_OK = "{\"ts\":1511443964815,\"channel\":\"GENERAL\",\"message\":{\"alias\":\"TestingAlias\",\"msg\":\"Sending message from SDK to #general and @here with url https://github.com/RocketChat/Rocket.Chat.Kotlin.SDK/\",\"parseUrls\":true,\"groupable\":false,\"avatar\":\"https://avatars2.githubusercontent.com/u/224255?s=88&v=4\",\"ts\":\"2017-11-23T13:32:44.798Z\",\"u\":{\"_id\":\"userId\",\"username\":\"testuser\",\"name\":\"testuser\"},\"rid\":\"GENERAL\",\"urls\":[{\"url\":\"https://github.com/RocketChat/Rocket.Chat.Kotlin.SDK/\"}],\"mentions\":[{\"_id\":\"here\",\"username\":\"here\"}],\"channels\":[{\"_id\":\"GENERAL\",\"name\":\"general\"}],\"_updatedAt\":\"2017-11-23T13:32:44.808Z\",\"_id\":\"messageId\"},\"success\":true}"