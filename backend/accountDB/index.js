var express = require("express");
var mongoose = require("mongoose");
var mongoDB = 'mongodb://127.0.0.1/my_database';
var bcrypt = require("bcryptjs");

var app = express();
var db = mongoose.connection;

// current 'database'
const users = []

app.listen(3000);
app.use(express.json());
db.on("error", console.error.bind(console, "MongoDB connection error"));

// list all users in database
app.get('/users', (req, res) => {
    res.json(users);
});


// register a user 
app.post('/users/register', async (req, res) => {

    // if username exists in database, fail
    const findusr = users.find(user => user.name == req.body.name);
    if (findusr != null) {
        return res.status(300).send("Username already exists.");
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
        users.push(user);
        res.status(201).send();
    } catch (e) {
        res.status(500).send("ERROR");
    }
});

// login a user
app.post('/users/login', async (req, res) => {

    // if username is not in database, user is not registered
    const user = users.find(user => user.name == req.body.name);
    if (user == null){
        return res.status(400).send("Username does not exist");
    }

    // check password (let bcrypt decrypt the password)
    try {
        if(await bcrypt.compare(req.body.password, user.password)) {
            res.send("Authentication Successful");
        } else {
            res.send("Password is incorrect");
        }
    } catch (e) {
        res.status(500).send();
    }
});



