const axios = require("axios");

// database
const users = [
    {
        name : "Kyle",
        password : "123"
    },

    {
        name : "Jack",
        password : "1234"
    },
]

const website_url = "http://localhost:3000";
axios.get = jest.fn(url => new Promise((resolve, reject) => {
    if(url == (website_url + "/users/all")){
        resolve({data : users});
    }
    else {
        reject(new Error("bad url"));
    }
}));

axios.post = jest.fn((url, jsonObj) => new Promise((resolve, reject) => {
    if(url == (website_url + "/users/register")){
        if(!jsonObj.hasOwnProperty("name") || !jsonObj.hasOwnProperty("name")){
            reject(new Error("bad request"));
        }
        users.push(jsonObj);
        resolve({data : {
            success : true
        }});
    }
    else {
        reject(new Error("bad url"));
    }
}));

describe("mock functions", () =>{
    test("mock", async () =>{
        try{
            var response = await axios.get("http://localhost:3000/users/all");
        } catch (err) {
            fail("its supposed to be good url");
        }
        expect(response.data.length).toBe(2);
    })

    test("badurl", async () => {
        var response;
        try {
            response = await axios.get("http://localhost:3000/users/register");
            fail("bad url");
        } catch (err) {
            expect(err.message).toBe("bad url");
        }
    })

    test("register", async () => {
        try {
            var response = await axios.post("http://localhost:3000/users/register", {name : "Adam", password : "12345"});
        } catch (err) {
            fail("good url");
        }
        expect(users.length).toBe(3);
        expect(response.data.success).toBe(true);
    })
})