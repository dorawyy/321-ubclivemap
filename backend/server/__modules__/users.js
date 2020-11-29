var express = require("express");
var bcrypt = require("bcryptjs");

var router = express.Router();
router.use(express.json());

var sharedfuncs = require("./sharedfunctions");
var Account = require("../__models__/models").Account;

function userIsGoodRequest(body){
    if(!body.hasOwnProperty('name')){
        return false;
    }
    if(!body.hasOwnProperty('password')){
        return false;
    }
    return true;
}

/****************************************************************************
 ********************** ACCOUNT DATA BASE API CALLS *************************
****************************************************************************/

/*
 * All Accounts Route.
 *
 * Returns all accounts in accountDB.
 */
router.get('/all', async (req, res) => {
    var cursor = await Account.find().exec();
    return res.json(cursor)
});

/*
 * Register Account Route.
 *
 * Takes in a request with {username, password}.
 * Stores an account with information in request to accountDB, encrypts password
 * before storage.
 */
router.post('/register', async (req, res) => {
    // if username exists in database, fail
    if(!userIsGoodRequest(req.body)){
        return res.status(401).json(sharedfuncs.formatResponse(false, "Not well formed request.", null));
    }

    findarr = [];
    var response = await Account.findOne({"name" : req.body.name}).exec();
    if(response != null){
        return res.status(402).json(sharedfuncs.formatResponse(false, "Account already exists.", null));
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
        return res.json(sharedfuncs.formatResponse(false, err, null));
    }

    var tokenreq;
    if(req.body.hasOwnProperty("token")){
        // Store device token of user
        token_data = {
            username : req.body.name,
            token : req.body.token
        }
        tokenreq = await sharedfuncs.axiosPostRequest(req, "/notifications/addtoken", token_data);
    }
    return res.status(200).json(sharedfuncs.formatResponse(true, "Account registered successfully.", null));
});

/*
 * Update Account Route.
 *
 * Takes in a request with {username, password}.
 * Updates account with username 'username' to the information in request.
 */
router.post("/update", async (req, res) => {
    if(!userIsGoodRequest(req.body)){
        return res.status(401).json(sharedfuncs.formatResponse(false, "Not well formed request.", null));
    }

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

    var response = await Account.replaceOne({"name" : req.body.name}, user);

    if(response.n === 0){
        return res.status(402).json(sharedfuncs.formatResponse(false, "Username does not exist.", null));
    }
    return res.status(200).json(sharedfuncs.formatResponse(true, "Account updated successfully.", null));
});

/*
 * Delete Account Route.
 *
 * Takes in a request with {name}.
 * Removes account with name 'name' from accountDB.
 */
router.post('/delete', async (req,res) =>{
    if(!req.body.hasOwnProperty("name")){
        return res.status(401).json(sharedfuncs.formatResponse(false, "Not well formed request.", null));
    }
    var response = await Account.deleteOne({"name" : req.body.name});
    if(response.n === 0){
        return res.status(402).json(sharedfuncs.formatResponse(false, "Username does not exist.", null));
    }
    return res.status(200).json(sharedfuncs.formatResponse(true, "Account deleted successfully.", null));
})

/*
 * Login Account Route.
 *
 * Takes in a request with {name, password, token}.
 * Returns the user profile with username 'name' after authenticating password, 
 * stores the token 'token' in tokenDB.
 */
router.post('/login', async (req, res) => {
    if(!userIsGoodRequest(req.body)){
        return res.json(sharedfuncs.formatResponse(false, "Not well formed request.", null));
    }
    var response = await Account.findOne({"name" : req.body.name}).exec();
    if(response == null){
        // Username doesnt exist
        return res.json(sharedfuncs.formatResponse(false, "Username does not exist.", null));
    }

    var profilereq;
    var tokenreq;
    try {
        if(await bcrypt.compare(req.body.password, response.password)) {
            // PASSWORD AUTHENTICATED
            // GET PROFILE OF LOGGED IN USER
            profilereq = await sharedfuncs.axiosPostRequest(req, "/profiles/search", {username: req.body.name});
            if(req.body.hasOwnProperty("token")){
                // STORE DEVICE TOKEN OF USER
                var token_data = {
                    username : req.body.name,
                    token : req.body.token
                }
                tokenreq = await sharedfuncs.axiosPostRequest(req, "/notifications/addtoken", token_data);
            }
                
            if(profilereq.data.success == true){
                // Logged in, got profile.
                return res.json(sharedfuncs.formatResponse(true, "Authentication successful.", profilereq.data.value));
            } else {
                // profile search error
                return res.json(profilereq.data);
            }
        } else {
            // Wrong password
            return res.json(sharedfuncs.formatResponse(false, "Invalid password.", null));
        }
    } catch (err) {
        // Bcrypt error
        return res.json(sharedfuncs.formatResponse(false, "UHOH: " + err, null));
    }
});

module.exports = router;