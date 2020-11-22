var admin = require("firebase-admin");
var serviceAccount = require("../accountkey.json");
var express = require("express");

var router = express.Router();
router.use(express.json());

/****************************************************************************
 ********************** PUSH NOTIFICATIONS BASE *****************************
****************************************************************************/

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://cpen321projectworking.firebaseio.com"
})

module.exports.admin = admin

const notification_options = {
    priority: "high",
    timeToLive: 60 * 60 * 24
};

var REG_TOKEN;
router.post('/givetoken', (req,res) => {
    REG_TOKEN = req.body.token; 
});

router.post('/send', (req, res)=>{
    const registrationToken = req.body.registrationToken
    const message = req.body.message
    const options =  notification_options

    
    admin.messaging().sendToDevice(registrationToken, req.body.payload, options)
    .then( response => {
        res.status(200).send("Notification sent successfully")
    })
    .catch( error => {
        console.log(error);
    });
})

module.exports = router;