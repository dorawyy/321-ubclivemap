const request = require("supertest")
const app = require("../app").app;
var models = require("../__models__/models");

var connected = false

beforeEach(async () => {
    if(!connected){
        await models.connectDb()
        connected = true
    }
})

afterEach(async () => {
    await models.disconnectDb(); 
    connected = false
})

// accountDB tests 
describe("test root path", () => {
    test("It should response the GET method", async () => {
        const response = await request(app).get("/users");
        console.log(response.body)
    });
});


describe("successful test register", () => {
    test("register user", async () => {
        const response = await request(app)
            .post("/users/register")
            .type('json')
            .send({
                name: "Jack",
                password: "pass123"
            })

        
        expect(response.success == true)
        expect(response.status == "User registed successfully.");
    })
});