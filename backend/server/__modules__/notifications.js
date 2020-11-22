var admin = require("firebase-admin");
var serviceAccount = require("../accountkey.json");
var express = require("express");

var router = express.Router();
router.use(express.json());

var Token = require("../__models__/models").Token;

/****************************************************************************
 ********************** PUSH NOTIFICATIONS BASE *****************************
****************************************************************************/

function formatResponse(successVal, status, val) {
    const resp = {
        success : successVal,
        status : status,
        value : val
    }
    return resp;
}

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://cpen321projectworking.firebaseio.com"
})

module.exports.admin = admin

const notification_options = {
    priority: "high",
    timeToLive: 60 * 60 * 24
};

// list all user profile in database
router.get('/alltokens', async (req, res) => {
    var retarr = [];
    var cursor = await Token.find().exec();
    return res.json(cursor)
});

// add user profile to database
router.post("/addtoken", async (req, res) => {
    // if username is in database, cant add
    if(!req.body.hasOwnProperty("username") || !req.body.hasOwnProperty("token")){
        return res.json(formatResponse(false, "Not well formed request.", null));
    }
    var response = await Token.update({"username" : req.body.username}, req.body, {upsert: true}).exec();
    return res.json(formatResponse(true, "Token insert successfully.", null));
});

router.post('/send', async (req, res)=>{
    if(!req.body.hasOwnProperty("username") || !req.body.hasOwnProperty("title") || !req.body.hasOwnProperty("message")){
        return res.json(formatResponse(false, "Not well formed request.", null));
    }

    
    var response = await Token.findOne({"username" : req.body.username}).exec()
    if(response == null) {
        return res.json(formatResponse(false, "Username does not exist.", null));
    }
    const registrationToken = response.token;
    const options = notification_options

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