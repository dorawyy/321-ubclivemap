var express = require("express");
var axios = require("axios");

var router = express.Router();
router.use(express.json());

var formatResponse = require("./sharedfunctions");
var Profile = require("../__models__/models").Profile;

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

/****************************************************************************
 ************************* USER DATA BASE API CALLS *************************
****************************************************************************/

/*
 * All Profile Route.
 *
 * Returns all profiles in profileDB.
 */
router.get('/all', async (req, res) => {
    var retarr = [];
    var cursor = await Profile.find().exec();
    return res.json(cursor)
});

/*
 * Search Profile Route.
 *
 * Takes in a request with the username of some profile.
 * Returns the profile with username 'username' if it exists in profileDB,
 * returns null otherwise.
 */
router.post("/search", async (req, res) => {
    if(!req.body.hasOwnProperty("username")){
        return res.json(formatResponse(false, "Not well formed request.", null));
    }
    var response = await Profile.findOne({"username" : req.body.username}).exec()
    if(response == null) {
        return res.json(formatResponse(false, "Username does not exist.", null));
    }
    return res.json(formatResponse(true, "User found successfully.", response));
});

/*
 * Update Profile Route.
 *
 * Takes in a request with the full information of a profile.
 * Updates the profile with the username of the request username.
 */
router.post("/update", async (req, res) => {
    if(!profileIsGoodRequest(req.body)){
        return res.json(formatResponse(false, "Not well formed request.", null));
    }
    var response = await Profile.replaceOne({"username" : req.body.username}, req.body)
    if(response.n == 0){
        return res.json(formatResponse(false, "Username does not exist.", null));
    }
    return res.json(formatResponse(true, "User profile updated successfully.", null));
});

/*
 * Delete Profile Route.
 *
 * Takes in a request with the username of a profile.
 * Removes a profile with username 'username' from profileDB.
 */
router.post('/delete', async (req,res) =>{
    if(!req.body.hasOwnProperty("username")){
        return res.json(formatResponse(false, "Not well formed request.", null));
    }

    var response = await Profile.deleteOne({"username" : req.body.username});
    if(response.n === 0){
        return res.json(formatResponse(false, "Username does not exist.", null));
    }
    return res.json(formatResponse(true, "User profile deleted successfully.", null));
})

/*
 * Add Profile Route.
 *
 * Takes in a request with the full information of a profile.
 * Adds the profile given in the request to profileDB if the username is unique.
 */
router.post("/add", async (req, res) => {
    // if username is in database, cant add
    if(!profileIsGoodRequest(req.body)){
        return res.json(formatResponse(false, "Not well formed request.", null));
    }
    var response = await Profile.findOne({"username" : req.body.username}).exec()
    if(response != null) {
        return res.json(formatResponse(false, "Username already exists.", null));
    }

    await Profile.create(req.body);
    return res.json(formatResponse(true, "User profile insert successfully.", null));
});

/*
 * Join Profile Route.
 *
 * Takes in a request with {username, activity_id}.
 * Updates a profile with username 'username' to be added to activity with id 'activity_id'.
 */
router.post("/join",async(req,res)=>{
    if(!req.body.hasOwnProperty("username") || !req.body.hasOwnProperty("aid")){
        return res.json(formatResponse(false, "Not well formed request.", null));
    }
    var response = await Profile.findOne({"username" : req.body.username}).exec()
    if(response == null) {
        return res.json(formatResponse(false, "User does not exist.", null));
    }
    if(response.inActivity == true){
        return res.json(formatResponse(false, "User is already in an activity.", null));
    }
    response.inActivity = true;
    response.activityID = req.body.aid;
    var result = await Profile.replaceOne({"username" : req.body.username}, response);
    return res.json(formatResponse(true, "User Joined successfully.", result));
});

/*
 * Leave Profile Route.
 *
 * Takes in a request with {username, activity_id}.
 * Updates a profile with username 'username' to leave the activity with id 'activity_id'
 * if the user is in the activity.
 */
router.post("/leave",async(req,res)=>{
    if(!req.body.hasOwnProperty("username") || !req.body.hasOwnProperty("aid")){
        return res.json(formatResponse(false, "Not well formed request.", null));
    }
    var response = await Profile.findOne({"username" : req.body.username}).exec()
    if(response == null) {
        return res.json(formatResponse(false, "User does not exist.", null));
    }

    if(response.inActivity == false){
        return res.json(formatResponse(false, "User is not in an activity.", null));
    }

    if(response.activityID != req.body.aid){
        return res.json(formatResponse(false, "User is not in this activity.", null));
    }

    response.inActivity = false;
    response.activityID = "-1";
    var result = await Profile.replaceOne({"username" : req.body.username}, response)
    return res.json(formatResponse(true, "User Left the Activity successfully.", result));
});

module.exports = router;