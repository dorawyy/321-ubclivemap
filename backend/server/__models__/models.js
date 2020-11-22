const mongoose = require('mongoose') 
var Account = require("./Account")
var Profile = require("./Profile")
var Activity = require("./Activity")
var Token = require("./Token")

const MONGO_URL = 'mongodb://localhost:27020';
const connectDb = () => {
    return mongoose.connect(MONGO_URL, {useNewUrlParser : true, useUnifiedTopology : true});
}
const disconnectDb = () => {
    return mongoose.disconnect();
}

module.exports = {
    connectDb,
    disconnectDb,
    Account,
    Profile,
    Activity,
    Token
}