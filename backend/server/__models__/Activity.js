const mongoose = require('mongoose') 
const Schema = mongoose.Schema 
const userSchema = new Schema({
    aid : String,
    name : String,
    leader : String,
    usernames : [String],
    info : String,
    major : String,
    course : [String],
    school : String,
    lat : String,
    long : String,
    status : String
})

module.exports = mongoose.model('Activity', userSchema)