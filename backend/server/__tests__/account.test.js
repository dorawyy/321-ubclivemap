const request = require("supertest")
const app = require("../app").app;
var models = require("../__models__/models");
var Account = models.Account;
var Profile = models.Profile;
var Activity = models.Activity;

var connected = false

/****************************************************************************
 ********************* SUCCESSFUL RESPONSE TESTS *************************
****************************************************************************/
describe("successful tests", () => {
    beforeAll(async () => {
        await models.connectDb()
    });

    test("register user", async () => {
        await Account.deleteOne({name : "Jack"});
        const response = await request(app)
            .post("/users/register")
            .type('json')
            .send({
                name: "Jack",
                password: "pass123"
            })

        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Account registered successfully.");
    })

    test("login user", async() => {
        const response = await request(app)
            .post("/users/login")
            .type('json')
            .send({
                name: "Jack",
                password: "pass123"
            })

        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Authentication successful.");
    })

    test("delete user", async() => {
        const response = await request(app)
            .post("/users/delete")
            .type('json')
            .send({
                name: "Jack"
            })

        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Account deleted successfully.");
    })

    afterAll(async () => {
        await models.disconnectDb()
    });
});

/****************************************************************************
 ********************* FAILED RESPONSE TESTS *************************
****************************************************************************/
describe("fail tests", () => {
    beforeAll(async () => {
        await models.connectDb()
        const response = await request(app)
            .post("/users/register")
            .type('json')
            .send({
                name: "Jack",
                password: "pass123"
            })
    });

    test("register user", async () => {
        var response = await request(app)
            .post("/users/register")
            .type('json')
            .send({
                name: "Jack",
                password: "pass123"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Account already exists.");

        response = await request(app)
            .post("/users/register")
            .type('json')
            .send({
                password: "pass123"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");
    })

    test("login user", async() => {
        var response = await request(app)
            .post("/users/login")
            .type('json')
            .send({
                name: "JackADKGJGKAD",
                password: "pass123"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Username does not exist.");

        var response = await request(app)
            .post("/users/login")
            .type('json')
            .send({
                name: "Jack",
                password: "pass1234"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Invalid password.");

        var response = await request(app)
            .post("/users/login")
            .type('json')
            .send({
                name: "Jack",
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");
    })

    test("delete user", async() => {
        var response = await request(app)
            .post("/users/delete")
            .type('json')
            .send({
                name: "JackADGJDALKGJ"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Username does not exist.");

        var response = await request(app)
            .post("/users/delete")
            .type('json')
            .send({
                weirdkey: "this is weird"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");
    })

    afterAll(async () => {
        await Account.deleteOne({name : "Jack"});
        await models.disconnectDb()
    });
});