var express = require("express");
var mongoclient = require("mongodb").MongoClient;
var bcrypt = require("bcryptjs");

var app = express();
const APP_PORT = 3000;
const MONGO_URL = 'mongodb://localhost:27018';

app.use(express.json());

// init database
mongoclient.connect(MONGO_URL,
{useUnifiedTopology: true},
(err, client) => {
    if(err) return console.log("error");
    db = client.db("user-data");
    var server = app.listen(APP_PORT, function () {
        var port = server.address().port;
        console.log("Listening at %s", port);
    });
});

// list all users in database
app.get('/users', (req, res) => {
    var retarr = [];
    var cursor = db.collection("user-data").find();
    cursor.forEach((doc,err) => {
        if(err) return console.log("error");
        retarr.push(doc);
    }, () => {
        return res.json(retarr);
    });
});


function formatResponse(successVal, status) {
    const resp = {
        success : successVal,
        status : status
    }
    return resp;
}

// register a user 
app.post('/users/register', async (req, res) => {

    // if username exists in database, fail
    findarr = [];
    var response = await db.collection("user-data").findOne({"name" : req.body.name});
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

    // insert 'user' into db
    var response2 = await db.collection("user-data").insertOne(user);
    if(response2.result.ok == 1){
        return res.json(formatResponse(true, "User registered successfully.", null));
    }
    return res.json(formatResponse(false, "User register failed.", null));
});

// login a user
app.post('/users/login', async (req, res) => {
    const response = await db.collection("user-data").findOne({"name" : req.body.name});
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



