var admin = require("firebase-admin");
var serviceAccount = require("../accountkey.json");
var express = require("express");

var formatResponse = require("./sharedfunctions");

var router = express.Router();
router.use(express.json());

var Token = require("../__models__/models").Token;

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://cpen321projectworking.firebaseio.com"
})

const notification_options = {
    priority: "high",
    timeToLive: 60 * 60 * 24
};

/****************************************************************************
 ********************** PUSH NOTIFICATIONS BASE *****************************
****************************************************************************/

/*
 * All Tokens Route.
 *
 * Returns all stored tokens in tokenDB.
 */
router.get('/alltokens', async (req, res) => {
    var cursor = await Token.find().exec();
    return res.json(cursor)
});

/*
 * Add Tokens Route.
 *
 * Takes in a request with {username, token}
 * Stores request in tokenDB, updates token if the username already exists in
 * tokenDB.
 */
router.post("/addtoken", async (req, res) => {
    // if username is in database, cant add
    if(!req.body.hasOwnProperty("username") || !req.body.hasOwnProperty("token")){
        return res.json(formatResponse(false, "Not well formed request.", null));
    }
    var response = await Token.updateOne({"username" : req.body.username}, req.body, {upsert: true}).exec();
    return res.json(formatResponse(true, "Token insert successfully.", null));
});

/*
 * Send Notification Route.
 *
 * Takes in a request with {username, title, message}
 * Sends a notification to the user with username 'username'.
 * Notification will have title 'title' and body 'message'.
 */
router.post('/send', async (req, res)=>{
    if(!req.body.hasOwnProperty("username") || !req.body.hasOwnProperty("title") || !req.body.hasOwnProperty("message")){
        return res.json(formatResponse(false, "Not well formed request.", null));
    }

    var response = await Token.findOne({"username" : req.body.username}).exec()
    if(response == null) {
        return res.json(formatResponse(false, "Username does not exist.", null));
    }
    var registrationToken = response.token;
    var options = notification_options

    var payload = {
        data : {
            key1 : "This is data"
        },
        notification : {
            title : req.body.title,
            body : req.body.message 
        }
    }

    admin.messaging().sendToDevice(registrationToken, payload, options)
    .then( response => {
        res.json(formatResponse(true, "Notification sent successfully", null));
    })
    .catch( error => {
        console.log(error);
    });
})

module.exports = router;
module.exports.admin = admin