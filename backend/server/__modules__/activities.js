var express = require("express");

var router = express.Router();
router.use(express.json());

var sharedfuncs = require("./sharedfunctions");
var Activity = require("../__models__/models").Activity;

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

/****************************************************************************
 ********************* ACTIVITY DATA BASE API CALLS *************************
****************************************************************************/

/*
 * All Activity Route.
 *
 * Returns all activities in activityDB.
 */
router.get('/all', async (req, res) => {
    var cursor = await Activity.find().exec();
    return res.json(cursor)
});

/*
 * Search Activity Route.
 *
 * Takes in a request with {activity_id}
 * Returns the activity with activity id 'activity_id' if it exists in activityDB,
 * returns a null response otherwise.
 */
router.post("/search", async (req, res) => {
    if(!req.body.hasOwnProperty("aid")){
        return res.json(sharedfuncs.formatResponse(false, "Not well formed request.", null));
    }
    var response = await Activity.findOne({"aid" : req.body.aid}).exec()
    if(response == null) {
        return res.json(sharedfuncs.formatResponse(false, "Activity does not exist.", null));
    }
    return res.json(sharedfuncs.formatResponse(true, "Activity found successfully.", response));
});

/*
 * Add Activity Route.
 *
 * Takes in a request with the full information for an activity.
 * Adds the request activity to the activityDB, and then updates the activity leader's profile
 * to now be in the newly added activity.
 */
router.post("/add", async (req, res) => {
    if(!activityIsGoodRequest(req.body)){
        return res.json(sharedfuncs.formatResponse(false, "Not well formed request.", null));
    }
    var response = await Activity.findOne({"aid" : req.body.aid}).exec()
    if(response != null) {
        return res.json(sharedfuncs.formatResponse(false, "Activity Id Taken", null));
    }
    
    var userJoin = {
        username : req.body.leader,
        aid : req.body.aid
    }
    var profileupdate;
    try{
        // UPDATE ACTIVITY LEADER'S PROFILE
        profileupdate = await sharedfuncs.axiosPostRequest(req, "/profiles/join", userJoin);
    } catch (err) {
        return res.json(sharedfuncs.formatResponse(false, "ERROR: " + err, null));
    }
    if(profileupdate.data.success === true){
        // ADD ACTIVITY
        await Activity.create(req.body);
        return res.json(sharedfuncs.formatResponse(true, "Activity insert successful.", null));
    } else {
        // 'PROFILE JOIN' ERROR
        return res.json(profileupdate.data);
    }
});

/*
 * Join Activity Route.
 *
 * Takes in a request with {username, activity_id}. 
 * Updates the users profile to be in an activity with id 'activity_id', and updates activity
 * to include the user.
 */
router.post("/joinupdate",async(req,res)=>{
    if(!req.body.hasOwnProperty("aid") || !req.body.hasOwnProperty("username")){
        return res.json(sharedfuncs.formatResponse(false, "Not well formed request.", null));
    }
    var response = await Activity.findOne({"aid" : req.body.aid}).exec()
    if(response == null) {
        return res.json(sharedfuncs.formatResponse(false, "Activity does not exist.", null));
    }

    var profileupdate;
    var sendnotif;
    try{
        // UPDATE USER PROFILE TO BE IN ACTIVITY
        profileupdate = await sharedfuncs.axiosPostRequest(req, "/profiles/join", req.body);

        // SEND NOTIFICATION TO ACTIVITY LEADER
        var leader_notif = {
            username : response.leader,
            title : "Activity Locator",
            message : req.body.username + " joined your activity!"
        } 
        sendnotif = await sharedfuncs.axiosPostRequest(req, "/notifications/send", leader_notif);
    } catch (err) {
        // AXIOS ERROR
        return res.json(sharedfuncs.formatResponse(false, "ERROR: " + err, null));
    }

    if(profileupdate.data.success === true){
        // UPDATE ACTIVITY TO NOW INCLUDE USER
        response.usernames.push(req.body.username);
        var result = await Activity.replaceOne({"aid" : req.body.aid}, response)
        return res.json(sharedfuncs.formatResponse(true, "Activity Joined successfully.", result));
    } else {
        return res.json(profileupdate.data);
    }
});

/*
 * Leave Activity Route.
 *
 * Takes in a request with {username, activity_id}. 
 * Updates the users profile to no longer be in an activity, and updates activity
 * to no longer include user.
 */
router.post("/leaveupdate", async (req,res) => {
    if(!req.body.hasOwnProperty("aid") || !req.body.hasOwnProperty("username")){
        return res.json(sharedfuncs.formatResponse(false, "Not well formed request.", null));
    }

    // FIND THE ACTIVITY
    var response = await Activity.findOne({"aid" : req.body.aid}).exec()
    if(response == null) {
        return res.json(sharedfuncs.formatResponse(false, "Activity does not exist.", null));
    }

    var profileupdate;
    var sendnotif;
    try{
        // UPDATE USER PROFILE TO NO LONGER BE IN ACTIVITY
        profileupdate = await sharedfuncs.axiosPostRequest(req, "/profiles/leave", req.body);

        // SEND NOTIFICATION TO LEADER OF ACTIVITY
        if(req.body.username != response.leader){
            var leader_notif = {
                username : response.leader,
                title : "Activity Locator",
                message : req.body.username + " left your activity."
            };
            sendnotif = await sharedfuncs.axiosPostRequest(req, "/notifications/send", leader_notif);
        }
    } catch (err) {
        // AXIOS ERROR
        return res.json(sharedfuncs.formatResponse(false, "ERROR: " + err, null));
    }

    if(profileupdate.data.success == true){
        // REMOVE USER FROM USERARRAY IN ACTIVITY
        response.usernames = response.usernames.filter(e => e !== req.body.username);
        await Activity.replaceOne({"aid" : req.body.aid}, response);
        if(response.usernames.length === 0){
            // IF THERE ARE NO USERS LEFT IN ACTIVITY, DELETE THE ACTIVITY
            await Activity.deleteOne({"aid" : req.body.aid});
        } else {
            if(req.body.username == response.leader){
                // IF LEADER LEFT ACTIVITY, UPDATE ACTIVITY LEADER
                response.leader = response.usernames[0];
            }
            var result = await Activity.replaceOne({"aid" : req.body.aid}, response)
        } 
        return res.json(sharedfuncs.formatResponse(true, "Activity Left successfully.", result));
    } else {
        // 'LEAVE PROFILE' ERROR
        return res.json(profileupdate.data);
    }
});

/*
 * Delete Activity Route.
 *
 * Takes in a request with the full information of an activity.
 * Updates activity with activity id 'activity_id' to the request activity.
 */
router.post("/update", async (req, res) => {
    if(!activityIsGoodRequest(req.body)){
        return res.json(sharedfuncs.formatResponse(false, "Not well formed request.", null));
    }
    var response = await Activity.replaceOne({"aid" : req.body.aid}, req.body)
    if(response.n == 0){
        return res.json(sharedfuncs.formatResponse(false, "Activity does not exist.", null));
    }
    return res.json(sharedfuncs.formatResponse(true, "Activity updated successfully.", null));
});

/*
 * Delete Activity Route.
 *
 * Takes in a request with {activity_id}
 * Removes activity with activity id 'activity_id' from activityDB.
 */
router.post('/delete', async (req,res) =>{
    if(!req.body.hasOwnProperty("aid")){
        return res.json(sharedfuncs.formatResponse(false, "Not well formed request.", null));
    }
    var response = await Activity.deleteOne({"aid" : req.body.aid});
    if(response.n === 0){
        return res.json(sharedfuncs.formatResponse(false, "Activity does not exist.", null));
    }
    return res.json(sharedfuncs.formatResponse(true, "Activity deleted successfully.", null));
})

/*
 * Sort Activity Complex Logic.
 *
 * Sorts all activities in activityDB within a given maxradius.
 * Takes into account the weights in sort_options during the sorting.
 */
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

    sorted_activities.sort((a, b) => parseFloat(activity_matchfactor[JSON.stringify(b)]) - parseFloat(activity_matchfactor[JSON.stringify(a)]));
    return sorted_activities;
}

/*
 * Sort Activity Route (with given user profile information).
 *
 * Takes in a request with {{userprofile}, {userlocation}, {sort_options}}
 * Returns a sorted list of all activities in activityDB within a given max_radius in sort_options.
 */
router.post("/sort", async (req, res) => {
    var cursor = await Activity.find().exec();

    // NECESSARY REQUEST INFORMATION !!!
    if(!isGoodSortRequest("with user", req.body)){
        return res.json(sharedfuncs.formatResponse(false, "Not well formed request.", null));
    }

    var user = req.body.user; // user json
    return res.json(sort_activities(req, user, cursor))
});

/*
 * Sort Activity Route (with only user's username).
 *
 * Takes in a request with {{username}, {userlocation}, {sort_options}}
 * Searches for the profile with username='username'.
 * Returns a sorted list of all activities in activityDB within a given max_radius in sort_options.
 */
router.post("/sortnouser", async (req, res) => {
    var cursor = await Activity.find().exec();

    // NECESSARY REQUEST INFORMATION !!!
    if(!isGoodSortRequest("without user", req.body)){
        return res.json(sharedfuncs.formatResponse(false, "Not well formed request.", null));
    }

    var r_username = req.body.username;
    var profilereq;
    var userjson = {
        username : r_username
    }

    try{
        // SEARCH FOR USER PROFILE
        profilereq = await await sharedfuncs.axiosPostRequest(req, "/profiles/search", userjson);
    } catch (err) {
        // AXIOS ERROR
        return res.json(sharedfuncs.formatResponse(false, "ERROR: " + err, null));
    }

    if(profilereq.data.success === false){
        // 'PROFILE SEARCH' ERROR
        return res.json(profilereq.data);
    }
    var user = profilereq.data.value;
    return res.json(sort_activities(req, user, cursor));
});

module.exports = router;