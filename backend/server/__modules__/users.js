var express = require("express");
var bcrypt = require("bcryptjs");

var router = express.Router();
router.use(express.json());

var Account = require("../__models__/models").Account;

/****************************************************************************
 ********************** ACCOUNT DATA BASE API CALLS *************************
****************************************************************************/

function formatResponse(successVal, status, val) {
    const resp = {
        success : successVal,
        status : status,
        value : val
    }
    return resp;
}

// list all users in database
router.get('/all', async (req, res) => {
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
router.post('/register', async (req, res) => {
    // if username exists in database, fail
    if(!userIsGoodRequest(req.body)){
        return res.status(401).json(formatResponse(false, "Not well formed request.", null));
    }

    findarr = [];
    var response = await Account.findOne({"name" : req.body.name}).exec();
    if(response != null){
        return res.status(402).json(formatResponse(false, "Account already exists.", null));
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
    return res.status(200).json(formatResponse(true, "Account registered successfully.", null));
});


//Not in Use
router.post("/update", async (req, res) => {
    if(!userIsGoodRequest(req.body)){
        return res.status(401).json(formatResponse(false, "Not well formed request.", null));
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

    if(response.n == 0){
        return res.status(402).json(formatResponse(false, "Username does not exist.", null));
    }
    return res.status(200).json(formatResponse(true, "Account updated successfully.", null));
});

//Not in Use
router.post('/delete', async (req,res) =>{
    if(!req.body.hasOwnProperty("name")){
        return res.status(401).json(formatResponse(false, "Not well formed request.", null));
    }
    var response = await Account.deleteOne({"name" : req.body.name});
    if(response.n === 0){
        return res.status(402).json(formatResponse(false, "Username does not exist.", null));
    }
    return res.status(200).json(formatResponse(true, "Account deleted successfully.", null));
})

// login a user
router.post('/login', async (req, res) => {
    if(!userIsGoodRequest(req.body)){
        return res.status(401).json(formatResponse(false, "Not well formed request.", null));
    }

    var response = await Account.findOne({"name" : req.body.name}).exec();
    if(response == null){
        return res.status(402).json(formatResponse(false, "Username does not exist.", null));
    }
    try {
        if(await bcrypt.compare(req.body.password, response.password)) {
            return res.status(200).json(formatResponse(true, "Authentication successful.", null));
        } else {
            return res.status(403).json(formatResponse(false, "Invalid password.", null));
        }
    } catch (e) {
        return res.json(formatResponse(false, "I dunno", null));
    }
});

module.exports = router;