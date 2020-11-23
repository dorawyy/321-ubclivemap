var axios = require("axios");

function formatResponse (successVal, status, val) {
    const resp = {
        success : successVal,
        status : status,
        value : val
    }
    return resp;
}

function axiosPostRequest(req, endpoint, data) {
    var host_str = req.get("host");
    if(host_str == "10.0.2.2:3000"){
        host_str = "localhost:3000";
    }
    var url = req.protocol + "://" + host_str + endpoint;
    return axios.post(url, data);
}

module.exports = {
    formatResponse,
    axiosPostRequest
}