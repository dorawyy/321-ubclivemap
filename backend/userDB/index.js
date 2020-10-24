var express = require("express");
var mongoclient = require("mongodb").MongoClient;

// init express
var app = express();
app.use(express.json());
const APP_PORT = 4000;

//init database
const MONGO_URL = "mongodb://localhost:27019";
mongoclient.connect(MONGO_URL,
{useUnifiedTopology: true},
(err, client) => {
    if(err) return console.log("DATABASE ERROR: " + err.errmsg);
    db = client.db("profile-data");
    var server = app.listen(APP_PORT, () => {
        var port = server.address().port;
        console.log("Listening to %s", port);
    });
});

// FORMAT OF ALL RESPONSES
function formatResponse(successVal, status, val) {
    const resp = {
        success : successVal,
        status : status,
        value : val
    }
    return resp;
}

// list all user profile in database
app.get('/allprofiles', (req, res) => {
    var retarr = [];
    var cursor = db.collection("profile-data").find();
    cursor.forEach((doc,err) => {
        if(err) return console.log("DATABASE ERROR: " + err.errmsg);
        retarr.push(doc);
    }, () => {
        return res.json(retarr);
    });
});

// search for a user profile in the database
app.post("/usersearch", async (req, res) => {
    var response = await db.collection("profile-data").findOne({"userid" : req.body.userid})
    if(response == null) {
        return res.json(formatResponse(false, "User does not exist.", null));
    }
    return res.json(formatResponse(true, "User found successfully.", response));
});

// update profile with new profile
app.post("/userupdate", async (req, res) => {
    const options = { upsert : true };
    var response = await db.collection("profile-data").updateOne({"userid" : req.body.userid}, {$set: req.body}, options)
    if(response.result.ok == 1){
        return res.json(formatResponse(true, "User profile updated successfully.", null));
    }
    return res.json(formatResponse(false, "User profile update failed.", null));
});

// add user profile to database
app.post("/userprofile", async (req, res) => {
    // if userid is in database, cant add
    var response = await db.collection("profile-data").findOne({"userid" : req.body.userid})
    if(response != null) {
        return res.json(formatResponse(false, "Userid already exists.", null));
    }

    var response2 = await db.collection("profile-data").insertOne(req.body);
    console.log(response2);
    if(response2.result.ok == 1){
        return res.json(formatResponse(true, "User profile insert successfully.", null));
    }
    return res.json(formatResponse(false, "User profile insert failed.", null));
});