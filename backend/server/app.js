var express = require("express");
var bcrypt = require("bcryptjs");

var Account = require("./__models__/models").Account;
var Profile = require("./__models__/models").Profile;
var Activity = require("./__models__/models").Activity;

var app = express();
app.use(express.json())


function formatResponse(successVal, status) {
    const resp = {
        success : successVal,
        status : status
    }
    return resp;
}

/****************************************************************************
 ********************** ACCOUNT DATA BASE API CALLS *************************
****************************************************************************/

// list all users in database
app.get('/users', async (req, res) => {
    var retarr = [];
    var cursor = await Account.find().exec();
    return res.json(cursor)
});

function userIsGoodRequest(body){
    if(!body.hasOwnProperty('name')){
        return false;
    }
    if(!body.hasOwnProperty('password')){
        return false;
    }
    return true;
}

// register a user 
app.post('/users/register', async (req, res) => {

    // if username exists in database, fail
    if(!userIsGoodRequest(req.body)){
        return res.json(formatResponse(false, "Not well formed request."));
    }
    findarr = [];
    var response = await Account.findOne({"name" : req.body.name}).exec();
    if(response != null){
        return res.json(formatResponse(false, "User already exists."));
    }

    // enter user into database, encrypt password
    var salt;
    var hashedPassword;
    try{
        salt = await bcrypt.genSalt(); 
        hashedPassword = await bcrypt.hash(req.body.password, salt);
    } catch (e) {
        res.status(500).send("ERROR");
    }

    const user = { 
        name : req.body.name,
        password: hashedPassword
    };

    // insert 'user' into accDB
    try {
        await Account.create(user);
    } catch (err) {
        return res.json(formatResponse(false, err, null));
    }
    return res.json(formatResponse(true, "User registered successfully.", null));
});

// login a user
app.post('/users/login', async (req, res) => {
    var response = await Account.findOne({"name" : req.body.name}).exec();
    if(response == null){
        return res.json(formatResponse(false, "Username does not exist."));
    }
    try {
        if(await bcrypt.compare(req.body.password, response.password)) {
            return res.json(formatResponse(true, "Authentication successful."));
        } else {
            return res.json(formatResponse(false, "Invalid password."));
        }
    } catch (e) {
        return res.json(formatResponse(false, "I dunno"));
    }
});

/****************************************************************************
 ************************* USER DATA BASE API CALLS *************************
****************************************************************************/

// list all user profile in database
app.get('/allprofiles', async (req, res) => {
    var retarr = [];
    var cursor = await Profile.find().exec();
    return res.json(cursor)
});

// search for a user profile in the database
app.post("/usersearch", async (req, res) => {
    var response = await Profile.findOne({"username" : req.body.username}).exec()
    if(response == null) {
        return res.json(formatResponse(false, "User does not exist.", null));
    }
    return res.json(formatResponse(true, "User found successfully.", response));
});

// update profile with new profile
app.post("/userupdate", async (req, res) => {
    var response = await Profile.replaceOne({"username" : req.body.username}, req.body)
    if(response.n == 0){
        return res.json(formatResponse(false, "username does not exist", null));
    }
    return res.json(formatResponse(true, "User profile updated successfully.", null));
});

// add user profile to database
app.post("/userprofile", async (req, res) => {
    // if username is in database, cant add
    var response = await Profile.findOne({"username" : req.body.username}).exec()
    if(response != null) {
        return res.json(formatResponse(false, "username already exists.", null));
    }
    try {
        await Profile.create(req.body);
    } catch (err) {
        return res.json(formatResponse(false, err, null));
    }
    return res.json(formatResponse(true, "User profile insert successfully.", null));
});
/*
app.post("/inActivity", async(req, res) => {
    
    const options = { upsert : false };
    var response = await userDB.collection("profile-data").updateOne({"username" : req.body.username}, {$set: {inActivity: "True"}}, options)
    if(response.result.ok == 1){
        if(response.matchedCount == 1){
            return res.json(formatResponse(true, "User profile updated successfully.", null));
        }
        return res.json(formatResponse(false, "username does not exist", null));
    }
    return res.json(formatResponse(false, "User profile update failed.", null));
   
});*/

/****************************************************************************
 ********************* ACTIVITY DATA BASE API CALLS *************************
****************************************************************************/

// list all activities in database
app.get('/allactivities', async (req, res) => {
    var retarr = [];
    var cursor = await Activity.find().exec();
    return res.json(cursor)
});

// search for an activity in the database
app.post("/activitysearch", async (req, res) => {
    //console.log("hihi " + req.bod.aid);
    var response = await Activity.findOne({"aid" : req.body.aid}).exec()
    if(response == null) {
        return res.json(formatResponse(false, "Activity not exist.", null));
    }
    return res.json(formatResponse(true, "Activity found successfully.", response));
});

// add a new activity
app.post("/addactivity", async (req, res) => {
    var response = await Activity.findOne({"aid" : req.body.aid}).exec()
    if(response != null) {
        return res.json(formatResponse(false, "Activity Id Taken", null));
    }
    try {
        await Activity.create(req.body);
    } catch (err) {
        return res.json(formatResponse(false, err, null));
    }
    return res.json(formatResponse(true, "Activity insert successful.", null));
});

// add a new user to the activity
app.post("/updateUsers", async (req, res) => {
    var response = await Activity.replaceOne({"aid" : req.body.aid}, req.body)
    if(response.n == 0){
        return res.json(formatResponse(false, "Activity does not exist", null));
    }
    return res.json(formatResponse(true, "Activity users updated successfully.", null));
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
    var cursor = await Activity.find().exec();

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
    for(iter=0; iter<cursor.length; iter++) {
        i = sorted_activities.length
        var currentactivity = cursor[iter];

        console.log("i = " + i)
        // Calculate course factor
        var coursefactor = 0;
        for(i_course=0; i_course<currentactivity.course.length; i_course++){
            var activityCourse = currentactivity.course[i_course];
            if(user.courseRegistered.includes(activityCourse)){
                coursefactor += 1;
            }
        }
        coursefactor = coursesweight * parseFloat(coursefactor / user.courseRegistered.length);
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
    }

    for(i=0; i<sorted_activities.length; i++){
        var currentactivity = sorted_activities[i];
        console.log("activity = %o", currentactivity)
        console.log("matchfactor = " + activity_matchfactor[JSON.stringify(currentactivity)])
        console.log("")
        console.log("")
    }

    return res.json(sorted_activities)
});

module.exports = {
    app
}
