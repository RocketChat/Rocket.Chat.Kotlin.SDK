package chat.rocket.core.internal.rest

const val DEFAULT_TIMEOUT: Long = 2000

const val LOGIN_SUCCESS = "{\"status\": \"success\",\"data\": {\"authToken\": \"authToken\",\"userId\": \"userId\"}}"
const val LOGIN_ERROR = "{\"status\": \"error\",\"message\": \"Unauthorized\"}"

const val REGISTER_SUCCESS = "{\"user\":{\"_id\":\"userId\",\"createdAt\":\"2017-11-07T15:59:50.432Z\",\"services\":{\"password\":{\"bcrypt\":\"bcrypt-password\"},\"email\":{\"verificationTokens\":[{\"token\":\"verificationToken\",\"address\":\"test@email.com\",\"when\":\"2017-11-07T15:59:50.457Z\"}]}},\"emails\":[{\"address\":\"test@email.com\",\"verified\":false}],\"type\":\"user\",\"status\":\"offline\",\"active\":true,\"name\":\"Test User\",\"_updatedAt\":\"2017-11-07T15:59:51.144Z\",\"roles\":[\"user\"],\"username\":\"testuser\"},\"success\":true}"
const val REGISTER_FAIL_EMAIL_IN_USE = "{\"success\":false,\"error\":\"Email already exists. [403]\",\"errorType\":403}"
const val REGISTER_FAIL_USER_IN_USE = "{\"success\":false,\"error\":\"<strong>testuser</strong> is already in use :( [error-field-unavailable]\",\"errorType\":\"error-field-unavailable\"}"