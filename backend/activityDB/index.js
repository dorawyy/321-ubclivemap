const { json } = require("body-parser");
const { reverse } = require("dns");
var express = require("express");
const { stringify } = require("querystring");
var mongoclient = require("mongodb").MongoClient;

// init express
var app = express();
app.use(express.json());

console.log("Activity server starting");


//init database
const MONGO_URL = "mongodb://localhost:27020";
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
app.get('/allactivities', async (req, res) => {
    var retarr = [];
    var cursor = await db.collection("activity-data").find({});
    await cursor.forEach((doc, err) => {
        retarr.push(doc)
    })

    return res.json(retarr)
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
        return res.json(formatResponse(false, "Activity Id Taken", null));
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

// REFERENCE : https://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula
function getDistanceFromLatLonInKm(lat1,lon1,lat2,lon2) {
    var R = 6371; // Radius of the earth in km
    var dLat = deg2rad(lat2-lat1);  // deg2rad below
    var dLon = deg2rad(lon2-lon1); 
    var a = 
      Math.sin(dLat/2) * Math.sin(dLat/2) +
      Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * 
      Math.sin(dLon/2) * Math.sin(dLon/2)
      ; 
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
    var d = R * c; // Distance in km
    return d;
}
  
function deg2rad(deg) {
    return deg * (Math.PI/180)
}

app.post("/sortactivities", async (req, res) => {
    var sorted_activities = []
    var cursor = await db.collection("activity-data").find({});

    // NECESSARY REQUEST INFORMATION !!!
    var user = req.body.user; // user json
    var userlat = req.body.userlocation.lat;
    var userlong = req.body.userlocation.long;
    var maxradius = req.body.radius;
    var locationweight = req.body.locationweight;
    var coursesweight = req.body.coursesweight;
    var majorweight = req.body.majorweight;

    var activity_matchfactor = {} // maps an activity -> their match factor
    var i=0
    await cursor.forEach((doc, err) => {
        i = sorted_activities.length
        var currentactivity = doc;

        console.log("i = " + i)
        // Calculate course factor
        var coursefactor = 0;
        for(i_course=0; i_course<currentactivity.Course.length; i_course++){
            var activityCourse = currentactivity.Course[i_course];
            if(user.CourseRegistered.includes(activityCourse)){
                coursefactor += 1;
            }
        }
        coursefactor = coursesweight * parseFloat(coursefactor / user.CourseRegistered.length);
        console.log("coursefactor = " + coursefactor/coursesweight)

        // Calculate location factor
        var locationfactor = 0
        var dist = getDistanceFromLatLonInKm(parseFloat(userlat), parseFloat(userlong),
                        parseFloat(currentactivity.lat), parseFloat(currentactivity.long));
        locationfactor = locationweight * parseFloat((maxradius - dist) / maxradius)
        console.log("locationfactor = " + locationfactor/locationweight)
        if(locationfactor <= 0){
            // location of activity outside of maxradius
            // dont include activity in sorted list
            return;
        }

        // Calculate major factor
        var majorfactor = 0
        majorfactor = majorweight * (user.major == currentactivity.major) ? 1 : 0;
        console.log("majorfactor = " + majorfactor/majorweight)
        console.log("")
        console.log("")

        // Store matchfactor with their respective activity
        var matchfactor = 0;
        matchfactor = (coursefactor + locationfactor + majorfactor) / (coursesweight + locationweight + majorweight)
        activity_matchfactor[JSON.stringify(currentactivity)] = matchfactor;

        // INSERTION SORT ALGORITHM
        sorted_activities.push(currentactivity); 
        i = sorted_activities.length - 1 
        if(i != 0){
            var j = i-1; 
            while ((j > -1) && (matchfactor > activity_matchfactor[JSON.stringify(sorted_activities[j])])) {
                sorted_activities[j+1] = sorted_activities[j];
                j--;
            }
            sorted_activities[j+1] = currentactivity;
        }
    });

    for(i=0; i<sorted_activities.length; i++){
        var currentactivity = sorted_activities[i];
        console.log("activity = %o", currentactivity)
        console.log("matchfactor = " + activity_matchfactor[JSON.stringify(currentactivity)])
        console.log("")
        console.log("")
    }

    return res.json(sorted_activities)
})
