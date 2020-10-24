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
    var cursor = db.collection("user-data").find({"name" : req.body.name});
    cursor.forEach((doc,err) => {
        findarr.push(doc);
    }, async () => {
        if(findarr.length != 0){
            return res.json(formatResponse(false, "Username already exists."));
        }

        // enter user into database, encrypt password
        try{
            const salt = await bcrypt.genSalt(); 
            const hashedPassword = await bcrypt.hash(req.body.password, salt);
            console.log(salt);

            const user = { 
                name : req.body.name,
                password: hashedPassword
            };

            // insert 'user' into db
            db.collection("user-data").insertOne(
                user,
                (err, request) => {
                    if (err) {
                        return res.json(formatResponse(false, "Database error."))
                    }
                    console.log("mongodb saved");
                    return res.json(formatResponse(true, "User registered."))
                }
            );
        } catch (e) {
            res.status(500).send("ERROR");
        }
    });

});

// login a user
app.post('/users/login', async (req, res) => {
    findarr = [];
    var cursor = db.collection("user-data").find({"name" : req.body.name});

    cursor.forEach((doc,err) => {
        findarr.push(doc);
    }, async () => {
        // if username is not in database, user is not registered
        if(findarr.length == 0){
            return res.json(formatResponse(false, "Username does not exist."));
        }

        // check password (let bcrypt decrypt the password)
        try {
            if(await bcrypt.compare(req.body.password, findarr[0].password)) {
                return res.json(formatResponse(true, "Authentication successful."));
            } else {
                return res.json(formatResponse(false, "Invalid password."));
            }
        } catch (e) {
            return res.json(formatResponse(false, "I dunno"));
        }
    });

});



