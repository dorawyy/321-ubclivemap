var express = require("express");
var admin = require("firebase-admin");


var serviceAccount = require("./accountkey.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://cpen321projectworking.firebaseio.com"
})

module.exports.admin = admin

const app = express();
app.use(express.json());

const port = 6060;
const notification_options = {
    priority: "high",
    timeToLive: 60 * 60 * 24
};

var REG_TOKEN;
app.post('/givetoken', (req,res) => {
    REG_TOKEN = req.body.token; 
});

app.post('/firebase/notification', (req, res)=>{
    const registrationToken = req.body.registrationToken
    const message = req.body.message
    const options =  notification_options

    const payload = {
        data : {
            key1 : req.body.message
        }
    }
    
    admin.messaging().sendToDevice(registrationToken, payload, options)
    .then( response => {
        res.status(200).send("Notification sent successfully")
    })
    .catch( error => {
        console.log(error);
    });
})

app.listen(port, () =>{
    console.log("listening to port "+port)
})
