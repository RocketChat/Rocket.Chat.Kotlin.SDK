package chat.rocket.core.internal.rest

const val PERMISSIONS_OK = """
[
  {
    "_id": "access-mailer",
    "roles": [
      "admin"
    ],
    "_updatedAt": "2018-03-07T18:50:53.071Z",
    "meta": {
      "revision": 20,
      "created": 1520418679312,
      "version": 0,
      "updated": 1520448653074
    },
    "loki": 1
  },
  {
    "_id": "access-permissions",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679312,
      "version": 0
    },
    "loki": 2
  },
  {
    "_id": "access-rocket-mailer",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679312,
      "version": 0
    },
    "loki": 3
  },
  {
    "_id": "add-oath-service",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679312,
      "version": 0
    },
    "loki": 4
  },
  {
    "_id": "add-oauth-service",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679312,
      "version": 0
    },
    "loki": 5
  },
  {
    "_id": "add-user",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679312,
      "version": 0
    },
    "loki": 6
  },
  {
    "_id": "add-user-to-any-c-room",
    "roles": [
      "admin"
    ],
    "_updatedAt": "2017-02-03T14:22:56.073Z",
    "meta": {
      "revision": 0,
      "created": 1520418679312,
      "version": 0
    },
    "loki": 7
  },
  {
    "_id": "add-user-to-any-p-room",
    "roles": [],
    "_updatedAt": "2017-02-03T14:22:56.088Z",
    "meta": {
      "revision": 0,
      "created": 1520418679312,
      "version": 0
    },
    "loki": 8
  },
  {
    "_id": "add-user-to-joined-room",
    "roles": [
      "admin",
      "owner",
      "moderator"
    ],
    "_updatedAt": "2017-02-03T14:22:53.493Z",
    "meta": {
      "revision": 0,
      "created": 1520418679312,
      "version": 0
    },
    "loki": 9
  },
  {
    "_id": "archive-room",
    "roles": [
      "admin",
      "owner"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679312,
      "version": 0
    },
    "loki": 10
  },
  {
    "_id": "assign-admin-role",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679312,
      "version": 0
    },
    "loki": 11
  },
  {
    "_id": "auto-translate",
    "roles": [
      "admin"
    ],
    "_updatedAt": "2017-03-05T20:58:58.801Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 12
  },
  {
    "_id": "ban-user",
    "roles": [
      "admin",
      "moderator",
      "owner"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 13
  },
  {
    "_id": "bulk-create-c",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 14
  },
  {
    "_id": "bulk-register-user",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 15
  },
  {
    "_id": "clean-channel-history",
    "roles": [
      "admin"
    ],
    "_updatedAt": "2016-11-30T20:41:01.228Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 16
  },
  {
    "_id": "close-livechat-room",
    "roles": [
      "livechat-agent",
      "livechat-manager",
      "admin"
    ],
    "_updatedAt": "2018-03-07T18:50:52.776Z",
    "meta": {
      "revision": 20,
      "created": 1520418679313,
      "version": 0,
      "updated": 1520448652778
    },
    "loki": 17
  },
  {
    "_id": "close-others-livechat-room",
    "roles": [
      "livechat-manager",
      "admin"
    ],
    "_updatedAt": "2018-03-07T18:50:52.779Z",
    "meta": {
      "revision": 20,
      "created": 1520418679313,
      "version": 0,
      "updated": 1520448652784
    },
    "loki": 18
  },
  {
    "_id": "create-c",
    "roles": [
      "admin",
      "user",
      "bot"
    ],
    "_updatedAt": "2017-01-04T03:47:01.814Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 19
  },
  {
    "_id": "create-d",
    "roles": [
      "admin",
      "user",
      "bot"
    ],
    "_updatedAt": "2017-01-04T03:47:01.814Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 20
  },
  {
    "_id": "create-p",
    "roles": [
      "admin",
      "user",
      "bot"
    ],
    "_updatedAt": "2017-01-04T03:47:01.814Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 21
  },
  {
    "_id": "create-user",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 22
  },
  {
    "_id": "delete-c",
    "roles": [
      "admin",
      "owner"
    ],
    "_updatedAt": "2016-10-07T02:19:04.255Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 23
  },
  {
    "_id": "delete-d",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 24
  },
  {
    "_id": "delete-message",
    "roles": [
      "admin",
      "moderator",
      "owner"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 25
  },
  {
    "_id": "delete-p",
    "roles": [
      "admin",
      "owner"
    ],
    "_updatedAt": "2016-10-07T02:19:11.143Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 26
  },
  {
    "_id": "delete-user",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 27
  },
  {
    "_id": "edit-livechat-settings",
    "roles": [
      "livechat-manager"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 28
  },
  {
    "_id": "edit-message",
    "roles": [
      "admin",
      "moderator",
      "owner"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 29
  },
  {
    "_id": "edit-other-user-active-status",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 30
  },
  {
    "_id": "edit-other-user-info",
    "roles": [
      "admin"
    ],
    "_updatedAt": "2017-06-03T00:11:31.773Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 31
  },
  {
    "_id": "edit-other-user-password",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 32
  },
  {
    "_id": "edit-privileged-setting",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 33
  },
  {
    "_id": "edit-room",
    "roles": [
      "admin",
      "moderator",
      "owner"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 34
  },
  {
    "_id": "force-delete-message",
    "roles": [
      "admin",
      "owner"
    ],
    "_updatedAt": "2017-06-02T21:32:43.592Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 35
  },
  {
    "_id": "join-without-join-code",
    "roles": [
      "admin",
      "bot"
    ],
    "_updatedAt": "2017-03-29T19:48:02.157Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 36
  },
  {
    "_id": "mail-messages",
    "roles": [
      "admin",
      "owner"
    ],
    "_updatedAt": "2018-03-07T18:50:52.485Z",
    "meta": {
      "revision": 20,
      "created": 1520418679313,
      "version": 0,
      "updated": 1520448652603
    },
    "loki": 37
  },
  {
    "_id": "manage-assets",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 38
  },
  {
    "_id": "manage-emoji",
    "roles": [
      "admin"
    ],
    "_updatedAt": "2016-09-05T16:06:21.510Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 39
  },
  {
    "_id": "manage-integrations",
    "roles": [
      "admin",
      "bot"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 40
  },
  {
    "_id": "manage-oauth-apps",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 41
  },
  {
    "_id": "manage-own-integrations",
    "roles": [
      "bot"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 42
  },
  {
    "_id": "manage-sounds",
    "roles": [
      "admin"
    ],
    "_updatedAt": "2018-03-07T18:50:52.528Z",
    "meta": {
      "revision": 20,
      "created": 1520418679313,
      "version": 0,
      "updated": 1520448652605
    },
    "loki": 43
  },
  {
    "_id": "mention-all",
    "roles": [
      "admin",
      "moderator",
      "owner",
      "user"
    ],
    "_updatedAt": "2016-07-07T11:52:18.940Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 44
  },
  {
    "_id": "mute-user",
    "roles": [
      "admin",
      "moderator",
      "owner"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 45
  },
  {
    "_id": "pin-message",
    "roles": [
      "owner",
      "moderator",
      "admin"
    ],
    "_updatedAt": "2018-03-07T18:50:53.141Z",
    "meta": {
      "revision": 20,
      "created": 1520418679313,
      "version": 0,
      "updated": 1520448653144
    },
    "loki": 46
  },
  {
    "_id": "post-readonly",
    "roles": [
      "admin",
      "owner",
      "moderator"
    ],
    "_updatedAt": "2018-03-07T18:50:52.475Z",
    "meta": {
      "revision": 20,
      "created": 1520418679313,
      "version": 0,
      "updated": 1520448652603
    },
    "loki": 47
  },
  {
    "_id": "preview-c-room",
    "roles": [
      "admin",
      "user",
      "anonymous"
    ],
    "_updatedAt": "2017-05-05T00:56:03.249Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 48
  },
  {
    "_id": "receive-livechat",
    "roles": [
      "livechat-agent"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 49
  },
  {
    "_id": "remove-user",
    "roles": [
      "admin",
      "moderator",
      "owner"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 50
  },
  {
    "_id": "run-import",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 51
  },
  {
    "_id": "run-migration",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 52
  },
  {
    "_id": "save-others-livechat-room-info",
    "roles": [
      "livechat-manager"
    ],
    "_updatedAt": "2018-03-07T18:50:52.787Z",
    "meta": {
      "revision": 20,
      "created": 1520418679313,
      "version": 0,
      "updated": 1520448652789
    },
    "loki": 53
  },
  {
    "_id": "set-moderator",
    "roles": [
      "admin",
      "owner"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 54
  },
  {
    "_id": "set-owner",
    "roles": [
      "admin",
      "owner"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 55
  },
  {
    "_id": "set-react-when-readonly",
    "roles": [
      "admin",
      "owner"
    ],
    "_updatedAt": "2018-03-07T18:50:52.482Z",
    "meta": {
      "revision": 20,
      "created": 1520418679313,
      "version": 0,
      "updated": 1520448652603
    },
    "loki": 56
  },
  {
    "_id": "set-readonly",
    "roles": [
      "admin",
      "owner"
    ],
    "_updatedAt": "2018-03-07T18:50:52.479Z",
    "meta": {
      "revision": 20,
      "created": 1520418679313,
      "version": 0,
      "updated": 1520448652603
    },
    "loki": 57
  },
  {
    "_id": "snippet-message",
    "roles": [
      "owner",
      "moderator",
      "admin"
    ],
    "_updatedAt": "2018-03-07T18:50:53.151Z",
    "meta": {
      "revision": 20,
      "created": 1520418679313,
      "version": 0,
      "updated": 1520448653155
    },
    "loki": 58
  },
  {
    "_id": "unarchive-room",
    "roles": [
      "admin",
      "owner"
    ],
    "_updatedAt": "2016-09-22T13:40:54.778Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 59
  },
  {
    "_id": "user-generate-access-token",
    "roles": [
      "admin"
    ],
    "_updatedAt": "2017-05-05T00:56:13.419Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 60
  },
  {
    "_id": "view-c-room",
    "roles": [
      "admin",
      "user",
      "bot",
      "anonymous"
    ],
    "_updatedAt": "2017-05-05T00:56:03.249Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 61
  },
  {
    "_id": "view-d-room",
    "roles": [
      "admin",
      "user"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 62
  },
  {
    "_id": "view-full-other-user-info",
    "roles": [
      "admin"
    ],
    "_updatedAt": "2017-09-26T19:36:47.511Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 63
  },
  {
    "_id": "view-history",
    "roles": [
      "user",
      "guest",
      "anonymous"
    ],
    "_updatedAt": "2017-05-05T00:56:03.249Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 64
  },
  {
    "_id": "view-join-code",
    "roles": [
      "admin"
    ],
    "_updatedAt": "2016-08-27T06:23:15.733Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 65
  },
  {
    "_id": "view-joined-room",
    "roles": [
      "guest",
      "anonymous"
    ],
    "_updatedAt": "2017-05-05T00:56:03.249Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 66
  },
  {
    "_id": "view-l-room",
    "roles": [
      "livechat-agent",
      "livechat-manager",
      "admin"
    ],
    "_updatedAt": "2018-03-07T18:50:52.767Z",
    "meta": {
      "revision": 20,
      "created": 1520418679313,
      "version": 0,
      "updated": 1520448652769
    },
    "loki": 67
  },
  {
    "_id": "view-livechat-manager",
    "roles": [
      "livechat-manager",
      "admin"
    ],
    "_updatedAt": "2018-03-07T18:50:52.770Z",
    "meta": {
      "revision": 20,
      "created": 1520418679313,
      "version": 0,
      "updated": 1520448652772
    },
    "loki": 68
  },
  {
    "_id": "view-livechat-rooms",
    "roles": [
      "livechat-manager",
      "admin"
    ],
    "_updatedAt": "2018-03-07T18:50:52.773Z",
    "meta": {
      "revision": 20,
      "created": 1520418679313,
      "version": 0,
      "updated": 1520448652775
    },
    "loki": 69
  },
  {
    "_id": "view-logs",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 70
  },
  {
    "_id": "view-other-user-channels",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 71
  },
  {
    "_id": "view-p-room",
    "roles": [
      "admin",
      "user",
      "anonymous"
    ],
    "_updatedAt": "2017-05-05T00:56:03.249Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 72
  },
  {
    "_id": "view-privileged-setting",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 73
  },
  {
    "_id": "view-room-administration",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 74
  },
  {
    "_id": "view-statistics",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 75
  },
  {
    "_id": "view-user-administration",
    "roles": [
      "admin"
    ],
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 76
  },
  {
    "_id": "send-many-messages",
    "roles": [
      "admin",
      "bot"
    ],
    "_updatedAt": "2017-08-01T18:26:10.653Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 77
  },
  {
    "_id": "set-leader",
    "roles": [
      "admin",
      "owner"
    ],
    "_updatedAt": "2017-08-01T18:26:10.657Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 78
  },
  {
    "_id": "view-outside-room",
    "roles": [
      "admin",
      "owner",
      "moderator",
      "user"
    ],
    "_updatedAt": "2017-08-25T01:37:18.950Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 79
  },
  {
    "_id": "mention-here",
    "roles": [
      "admin",
      "owner",
      "moderator",
      "user"
    ],
    "_updatedAt": "2018-01-22T13:54:55.235Z",
    "meta": {
      "revision": 0,
      "created": 1520418679313,
      "version": 0
    },
    "loki": 80
  },
  {
    "_id": "manage-apps",
    "roles": [
      "admin"
    ],
    "_updatedAt": "2018-03-07T18:50:53.173Z",
    "meta": {
      "revision": 20,
      "created": 1520418679313,
      "version": 0,
      "updated": 1520448653176
    },
    "loki": 81
  }
]
"""