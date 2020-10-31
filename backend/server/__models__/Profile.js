const mongoose = require('mongoose') 
const Schema = mongoose.Schema 
const userSchema = new Schema({
    name : String,
    username : String,
    major : String,
    CourseRegistered : [String],
    school : String,
    phone : String,
    private : Boolean,
    inActivity : Boolean, 
    activityID : Number
})

module.exports = mongoose.model('Profile', userSchema)