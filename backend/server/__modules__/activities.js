var express = require("express");
var axios = require("axios");

var router = express.Router();
router.use(express.json());

var Activity = require("../__models__/models").Activity;
var Profile = require("../__models__/models").Profile;

/****************************************************************************
 ********************* ACTIVITY DATA BASE API CALLS *************************
****************************************************************************/

function formatResponse(successVal, status, val) {
    const resp = {
        success : successVal,
        status : status,
        value : val
    }
    return resp;
}

// list all activities in database
router.get('/all', async (req, res) => {
    var retarr = [];
    var cursor = await Activity.find().exec();
    return res.json(cursor)
});

function activityIsGoodRequest(body){
    if(!body.hasOwnProperty('aid')){
        return false;
    }
    if(!body.hasOwnProperty('name')){
        return false;
    }
    if(!body.hasOwnProperty('leader')){
        return false;
    }
    if(!body.hasOwnProperty('usernames')){
        return false;
    }
    if(!body.hasOwnProperty('info')){
        return false;
    }
    if(!body.hasOwnProperty('major')){
        return false;
    }
    if(!body.hasOwnProperty('course')){
        return false;
    }
    if(!body.hasOwnProperty('school')){
        return false;
    }
    if(!body.hasOwnProperty('lat')){
        return false;
    }
    if(!body.hasOwnProperty('long')){
        return false;
    }
    if(!body.hasOwnProperty('status')){
        return false;
    }
    return true;
}

function profileIsGoodRequest(body){
    if(!body.hasOwnProperty('name')){
        return false;
    }
    if(!body.hasOwnProperty('username')){
        return false;
    }
    if(!body.hasOwnProperty('major')){
        return false;
    }
    if(!body.hasOwnProperty('CourseRegistered')){
        return false;
    }
    if(!body.hasOwnProperty('school')){
        return false;
    }
    if(!body.hasOwnProperty('phone')){
        return false;
    }
    if(!body.hasOwnProperty('private')){
        return false;
    }
    if(!body.hasOwnProperty('inActivity')){
        return false;
    }
    if(!body.hasOwnProperty('activityID')){
        return false;
    }
    return true;
}

// search for an activity in the database
router.post("/search", async (req, res) => {
    if(!req.body.hasOwnProperty("aid")){
        return res.json(formatResponse(false, "Not well formed request.", null));
    }
    var response = await Activity.findOne({"aid" : req.body.aid}).exec()
    if(response == null) {
        return res.json(formatResponse(false, "Activity does not exist.", null));
    }
    return res.json(formatResponse(true, "Activity found successfully.", response));
});

// add a new activity
router.post("/add", async (req, res) => {
    if(!activityIsGoodRequest(req.body)){
        return res.json(formatResponse(false, "Not well formed request.", null));
    }
    var response = await Activity.findOne({"aid" : req.body.aid}).exec()
    if(response != null) {
        return res.json(formatResponse(false, "Activity Id Taken", null));
    }
    
    var userJoin = {
        username : req.body.leader,
        aid : req.body.aid
    }
    var profileupdate;
    try{
        var url = req.protocol + "://localhost:3000/profiles/join";
        //var url = req.protocol + "://" + req.get('host') +"/profiles/join";
        profileupdate = await axios.post(url, userJoin);
    } catch (err) {
        return res.json(formatResponse(false, "ERROR: " + err, null));
    }
    if(profileupdate.data.success == true){
        await Activity.create(req.body);
        return res.json(formatResponse(true, "Activity insert successful.", null));
    } else {
        return res.json(profileupdate.data);
    }
});

//No Need
router.post("/addleader", async (req, res) => {
    if(!activityIsGoodRequest(req.body)){
        return res.json(formatResponse(false, "Not well formed request.", null));
    }
    var response = await Activity.findOne({"aid" : req.body.aid}).exec()
    if(response != null) {
        return res.json(formatResponse(false, "Activity Id Taken", null));
    }

    response = await Profile.findOne({"username" : req.body.leader}).exec();
    if(response == null) {
        return res.json(formatResponse(false, "Leader does not exist.", null));
    }
    if(response.inActivity == true){
        return res.json(formatResponse(false, "Leader is in an activity.", null));
    }
    await Activity.create(req.body);
    return res.json(formatResponse(true, "Activity insert successful.", null));
});

// No Need
router.post("/usercreate", async (req, res) => {
    if(!activityIsGoodRequest(req.body)){
        return res.json(formatResponse(false, "Not well formed request.", null));
    }
    var response = await Activity.findOne({"aid" : req.body.aid}).exec()
    if(response != null) {
        return res.json(formatResponse(false, "Activity Id Taken", null));
    }

    await Activity.create(req.body);
    return res.json(formatResponse(true, "Activity insert successful.", null));
});

//No Need
router.post("/update", async (req, res) => {
    if(!activityIsGoodRequest(req.body)){
        return res.json(formatResponse(false, "Not well formed request.", null));
    }
    var response = await Activity.replaceOne({"aid" : req.body.aid}, req.body)
    if(response.n == 0){
        return res.json(formatResponse(false, "Activity does not exist.", null));
    }
    return res.json(formatResponse(true, "Activity updated successfully.", null));
});

//No Need
router.post("/join",async(req,res)=>{
    if(!req.body.hasOwnProperty("aid") || !req.body.hasOwnProperty("username")){
        return res.json(formatResponse(false, "Not well formed request.", null));
    }
    var response = await Activity.findOne({"aid" : req.body.aid}).exec()
    if(response == null) {
        return res.json(formatResponse(false, "Activity does not exist.", null));
    }
    response.usernames.push(req.body.user);
    var result = await Activity.replaceOne({"aid" : req.body.aid}, response)
    return res.json(formatResponse(true, "Activity Joined successfully.", result));
});

router.post("/joinupdate",async(req,res)=>{
    if(!req.body.hasOwnProperty("aid") || !req.body.hasOwnProperty("username")){
        return res.json(formatResponse(false, "Not well formed request.", null));
    }
    var response = await Activity.findOne({"aid" : req.body.aid}).exec()
    if(response == null) {
        return res.json(formatResponse(false, "Activity does not exist.", null));
    }

    var profileupdate;
    try{
        var url = req.protocol + "://localhost:3000/profiles/join";
        //var url = req.protocol + "://" + req.get('host') + "/profiles/join";
        profileupdate = await axios.post(url,
                        req.body);
    } catch (err) {
        return res.json(formatResponse(false, "ERROR: " + err, null));
    }
    if(profileupdate.data.success == true){
        response.usernames.push(req.body.username);
        var result = await Activity.replaceOne({"aid" : req.body.aid}, response)
        return res.json(formatResponse(true, "Activity Joined successfully.", result));
    } else {
        return res.json(profileupdate.data);
    }
});

router.post("/leaveupdate",async(req,res)=>{
    if(!req.body.hasOwnProperty("aid") || !req.body.hasOwnProperty("username")){
        return res.json(formatResponse(false, "Not well formed request.", null));
    }
    var response = await Activity.findOne({"aid" : req.body.aid}).exec()
    if(response == null) {
        return res.json(formatResponse(false, "Activity does not exist.", null));
    }

    var profileupdate;
    try{
        //var url = req.protocol + "://" + req.get('host') + "/profiles/leave";
        var url = req.protocol + "://localhost:3000/profiles/leave";
        profileupdate = await axios.post(url, req.body);
    } catch (err) {
        return res.json(formatResponse(false, "ERROR: " + err, null));
    }

    if(profileupdate.data.success == true){
        response.usernames = response.usernames.filter(e => e !== req.body.username);
        var result = await Activity.replaceOne({"aid" : req.body.aid}, response)
        return res.json(formatResponse(true, "Activity Left successfully.", result));
    } else {
        return res.json(profileupdate.data);
    }
});

//Not in use
router.post('/delete', async (req,res) =>{
    if(!req.body.hasOwnProperty("aid")){
        return res.json(formatResponse(false, "Not well formed request.", null));
    }
    var response = await Activity.deleteOne({"aid" : req.body.aid});
    if(response.n === 0){
        return res.json(formatResponse(false, "Activity does not exist.", null));
    }
    return res.json(formatResponse(true, "Activity deleted successfully.", null));
})

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

var type = 0;
function isGoodSortRequest(func_type, body){
    if(func_type == "with user"){
        if(!body.hasOwnProperty("user")){
            type =1;
            return false;
        }
        if(!profileIsGoodRequest(body.user)){
            type =2;
            return false;
        }
    } else {
        if(!body.hasOwnProperty("username")){
            type =1;
            return false;
        }
    }
    if(!body.hasOwnProperty("userlat")){
        type =3;
        return false;
    }
    if(!body.hasOwnProperty("userlong")){
        type = 4;
        return false;
    }
    if(!body.hasOwnProperty("maxradius")){
        type = 5;
        return false;
    }
    if(!body.hasOwnProperty("locationweight")){
        type = 6;
        return false;
    }
    if(!body.hasOwnProperty("coursesweight")){
        type = 7;
        return false;
    }
    if(!body.hasOwnProperty("majorweight")){
        type = 8;
        return false;
    }
    return true;
}

router.post("/sort", async (req, res) => {
    var cursor = await Activity.find().exec();

    // NECESSARY REQUEST INFORMATION !!!
    if(!isGoodSortRequest("with user", req.body)){
        return res.json(formatResponse(false, "Not well formed request.", null));
    }

    var user = req.body.user; // user json
    return res.json(sort_activities(req, user, cursor))
});

function sort_activities(req, user, cursor){
    var sorted_activities = []
    var activity_matchfactor = {} // maps an activity -> their match factor
    var i=0

    var userlat = req.body.userlat;
    var userlong = req.body.userlong;
    var maxradius = req.body.maxradius;
    var locationweight = req.body.locationweight;
    var coursesweight = req.body.coursesweight;
    var majorweight = req.body.majorweight;

    for(iter=0; iter<cursor.length; iter++) {
        i = sorted_activities.length
        var currentactivity = cursor[iter];

        // Calculate course factor
        var coursefactor = 0;
        for(i_course=0; i_course<currentactivity.course.length; i_course++){
            var activityCourse = currentactivity.course[i_course];
            if(user.CourseRegistered.includes(activityCourse)){
                coursefactor += 1;
            }
        }
        coursefactor = coursesweight * parseFloat(coursefactor / user.CourseRegistered.length);

        // Calculate location factor
        var locationfactor = 0
        var dist = getDistanceFromLatLonInKm(parseFloat(userlat), parseFloat(userlong),
                        parseFloat(currentactivity.lat), parseFloat(currentactivity.long));
        locationfactor = locationweight * parseFloat((maxradius - dist) / maxradius)
        if(locationfactor <= 0){
            // location of activity outside of maxradius
            // dont include activity in sorted list
            continue;
        }

        // Calculate major factor
        var majorfactor = 0
        majorfactor = majorweight * (user.major == currentactivity.major) ? 1 : 0;

        // Store matchfactor with their respective activity
        var matchfactor = 0;
        matchfactor = (coursefactor + locationfactor + majorfactor) / (coursesweight + locationweight + majorweight)
        activity_matchfactor[JSON.stringify(currentactivity)] = matchfactor;
        sorted_activities.push(currentactivity);
    }

    //for(i=0; i<sorted_activities.length; i++){
        //var currentactivity = sorted_activities[i];
        //console.log("activity = %o", currentactivity)
        //console.log("matchfactor = " + activity_matchfactor[JSON.stringify(currentactivity)])
        //console.log("")
        //console.log("")
    //}

    sorted_activities.sort((a, b) => parseFloat(activity_matchfactor[JSON.stringify(b)]) - parseFloat(activity_matchfactor[JSON.stringify(a)]));
    return sorted_activities;
}


// GET USER FROM USERID
router.post("/sortnouser", async (req, res) => {
    var cursor = await Activity.find().exec();

    // NECESSARY REQUEST INFORMATION !!!
    if(!isGoodSortRequest("without user", req.body)){
        return res.json(formatResponse(false, "Not well formed request.", null));
    }

    var r_username = req.body.username;
    var profilereq;
    var userjson = {
        username : r_username
    }

    try{
        var url = req.protocol + "://" + req.get('host') + "/profiles/search";
        profilereq = await axios.post(url,
                        userjson);
    } catch (err) {
        return res.json(formatResponse(false, "ERROR: " + err, null));
    }

    if(profilereq.data.success == false){
        return res.json(profilereq.data);
    }
    var user = profilereq.data.value;
    return res.json(sort_activities(req, user, cursor));
});

module.exports = router;