var express = require("express");

var usersEndpoint = require("./__modules__/users");
var profilesEndpoint = require("./__modules__/profiles");
var activitiesEndpoint = require("./__modules__/activities");
var notificationsEndpoint = require("./__modules__/notifications").router;

var app = express();
app.use(express.json())
app.use("/users", usersEndpoint);
app.use("/profiles", profilesEndpoint);
app.use("/activities", activitiesEndpoint);
app.use("/notifications", notificationsEndpoint);

module.exports = {
    app
}
