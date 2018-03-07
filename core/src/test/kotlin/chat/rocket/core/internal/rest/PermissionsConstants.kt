package chat.rocket.core.internal.rest

const val PERMISSIONS_OK = """
[
  {
    "id": "access-mailer",
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
    "id": "access-permissions",
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
    "id": "access-rocket-mailer",
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
    "id": "add-oath-service",
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
    "id": "add-oauth-service",
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
    "id": "add-user",
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
    "id": "add-user-to-any-c-room",
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
    "id": "add-user-to-any-p-room",
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
    "id": "add-user-to-joined-room",
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
    "id": "archive-room",
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
    "id": "assign-admin-role",
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
    "id": "auto-translate",
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
    "id": "ban-user",
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
    "id": "bulk-create-c",
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
    "id": "bulk-register-user",
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
    "id": "clean-channel-history",
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
    "id": "close-livechat-room",
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
    "id": "close-others-livechat-room",
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
    "id": "create-c",
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
    "id": "create-d",
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
    "id": "create-p",
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
    "id": "create-user",
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
    "id": "delete-c",
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
    "id": "delete-d",
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
    "id": "delete-message",
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
    "id": "delete-p",
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
    "id": "delete-user",
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
    "id": "edit-livechat-settings",
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
    "id": "edit-message",
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
    "id": "edit-other-user-active-status",
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
    "id": "edit-other-user-info",
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
    "id": "edit-other-user-password",
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
    "id": "edit-privileged-setting",
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
    "id": "edit-room",
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
    "id": "force-delete-message",
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
    "id": "join-without-join-code",
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
    "id": "mail-messages",
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
    "id": "manage-assets",
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
    "id": "manage-emoji",
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
    "id": "manage-integrations",
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
    "id": "manage-oauth-apps",
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
    "id": "manage-own-integrations",
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
    "id": "manage-sounds",
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
    "id": "mention-all",
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
    "id": "mute-user",
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
    "id": "pin-message",
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
    "id": "post-readonly",
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
    "id": "preview-c-room",
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
    "id": "receive-livechat",
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
    "id": "remove-user",
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
    "id": "run-import",
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
    "id": "run-migration",
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
    "id": "save-others-livechat-room-info",
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
    "id": "set-moderator",
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
    "id": "set-owner",
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
    "id": "set-react-when-readonly",
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
    "id": "set-readonly",
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
    "id": "snippet-message",
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
    "id": "unarchive-room",
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
    "id": "user-generate-access-token",
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
    "id": "view-c-room",
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
    "id": "view-d-room",
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
    "id": "view-full-other-user-info",
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
    "id": "view-history",
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
    "id": "view-join-code",
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
    "id": "view-joined-room",
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
    "id": "view-l-room",
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
    "id": "view-livechat-manager",
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
    "id": "view-livechat-rooms",
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
    "id": "view-logs",
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
    "id": "view-other-user-channels",
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
    "id": "view-p-room",
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
    "id": "view-privileged-setting",
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
    "id": "view-room-administration",
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
    "id": "view-statistics",
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
    "id": "view-user-administration",
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
    "id": "send-many-messages",
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
    "id": "set-leader",
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
    "id": "view-outside-room",
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
    "id": "mention-here",
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
    "id": "manage-apps",
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