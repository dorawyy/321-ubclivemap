const mongoose = require('mongoose')
const Schema = mongoose.Schema

const userSchema = new Schema({
  name: String,
  password: {
    type: String, 
    require: true, 
    unique: true
  }
})

module.exports = mongoose.model('Account', userSchema)