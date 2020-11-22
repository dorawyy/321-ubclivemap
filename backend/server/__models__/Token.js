const mongoose = require('mongoose')
const Schema = mongoose.Schema

const tokenSchema = new Schema({
  username: String,
  token: String
})

module.exports = mongoose.model('Token', tokenSchema)