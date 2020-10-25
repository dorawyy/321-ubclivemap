var express = require("express");
var mongoclient = require("mongodb").MongoClient;

// init express
var app = express();
app.use(express.json());

console.log("Activity server starting");


//init database
const MONGO_URL = "mongodb://localhost:27019";
mongoclient.connect(MONGO_URL,
{useUnifiedTopology: true},
(err, client) => {
    if(err) return console.log("DATABASE ERROR: " + err.errmsg);
    db = client.db("activity-data");
    var server = app.listen(5000, () => {
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

// list all activities in database
app.get('/allactivities', (req, res) => {
    var retarr = [];
    var cursor = db.collection("activity-data").find();
    cursor.forEach((doc,err) => {
        if(err) return console.log("DATABASE ERROR: " + err.errmsg);
        retarr.push(doc);
    }, () => {
        return res.json(retarr);
    });
});

// search for an activity in the database
app.post("/activitysearch", async (req, res) => {
    //console.log("hihi " + req.bod.aid);
    var response = await db.collection("activity-data").findOne({"aid" : req.body.aid})
    if(response == null) {
        return res.json(formatResponse(false, "Activity not exist.", null));
    }
    return res.json(formatResponse(true, "Activity found successfully.", response));
});

// add a new activity
app.post("/addactivity", async (req, res) => {

    var response = await db.collection("activity-data").findOne({"aid" : req.body.aid})
    if(response != null) {
        return res.json(formatResponse(false, "Activity already exists.", null));
    }

    response = await db.collection("activity-data").insertOne(req.body);
    console.log(response);
    if(response.result.ok == 1){
        return res.json(formatResponse(true, "Activity insert successful.", null));
    }
    return res.json(formatResponse(false, "Activity insert failed.", null));
});

// add a new user to the activity
app.post("/updateUsers", async (req, res) => {
    const options = { upsert : false };
    var response = await db.collection("activity-data").updateOne({"aid" : req.body.aid}, { $set: req.body}, options)
    if(response.result.ok == 1){
        if(response.matchedCount == 1){
            return res.json(formatResponse(true, "Activity users updated successfully.", null));
        }
        return res.json(formatResponse(false, "Activity does not exist", null));
    }
    return res.json(formatResponse(false, "Activity Users update failed.", null));
});