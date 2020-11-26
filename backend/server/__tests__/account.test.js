const request = require("supertest");
const app = require("../app").app;
var sharedfuncs = require("../__modules__/sharedfunctions");
var models = require("../__models__/models");
var Account = models.Account;

var jack_a1 = {
    name : "Jack",
    password: "pass123"
}
var jack_at1 = {
    name : "Jack",
    password: "pass123",
    token: "blah"
}
var jack_a2 = {
    name : "Jack",
    password: "pass234"
}
var jack_at2 = {
    name : "Jack",
    password: "pass234",
    token : "blahblah"
}

var name_only = {
    name : "Jack"
}
var pass_only = {
    password : "pass234"
}

/****************************************************************************
 ********************* SUCCESSFUL RESPONSE TESTS *************************
****************************************************************************/
var before1;
describe("successful tests", () => {
    beforeAll(async () => {
        await models.connectDb()
        before1 = await Account.find().exec();
        await Account.deleteMany({});
    });

    test("register user", async () => {
        sharedfuncs.axiosPostRequest = jest.fn((arg1,arg2,arg3) => new Promise((resolve,reject) => {
            resolve({data : {success : true}});
        }));

        var response = await request(app)
            .post("/users/register")
            .type('json')
            .send(jack_at1);

        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Account registered successfully.");

        response = await request(app)
            .get("/users/all");
        
        expect(response.body[0].name).toBe("Jack");
    })

    test("update user", async () => {
        const response = await request(app)
            .post("/users/update")
            .type('json')
            .send(jack_a2);

        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Account updated successfully.");
    })

    test("login user", async() => {
        sharedfuncs.axiosPostRequest = jest.fn((arg1,arg2,arg3) => new Promise((resolve,reject) => {
            resolve({data : {success : true}});
        }));

        const response = await request(app)
            .post("/users/login")
            .type('json')
            .send(jack_at2);

        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Authentication successful.");
    })

    test("delete user", async() => {
        const response = await request(app)
            .post("/users/delete")
            .type('json')
            .send(name_only);

        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Account deleted successfully.");
    })

    afterAll(async () => {
        await Account.deleteMany({});
        await Account.insertMany(before1);
        await models.disconnectDb()
    });
});

/****************************************************************************
 ********************* FAILED RESPONSE TESTS *************************
****************************************************************************/
var before2;
describe("fail tests", () => {
    beforeAll(async () => {
        await models.connectDb()
        before2 = await Account.find().exec();
        await Account.deleteMany({});
    });

    test("register user", async () => {
        var response = await request(app)
            .post("/users/register")
            .type('json')
            .send(jack_a1);

        response = await request(app)
            .post("/users/register")
            .type('json')
            .send(jack_a1);

        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Account already exists.");

        response = await request(app)
            .post("/users/register")
            .type('json')
            .send(pass_only);
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

        response = await request(app)
            .post("/users/register")
            .type('json')
            .send(name_only);
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");
    })

    test("update user", async () => {
        var response = await request(app)
            .post("/users/update")
            .type('json')
            .send({
                name: "badbj",
                password: "pass234"
            })

        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Username does not exist.");

        response = await request(app)
            .post("/users/update")
            .type('json')
            .send(name_only);
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

        response = await request(app)
            .post("/users/update")
            .type('json')
            .send(pass_only);
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");
    })

    test("login user", async() => {
        sharedfuncs.axiosPostRequest = jest.fn((arg1,arg2,arg3) => new Promise((resolve,reject) => {
            resolve({data : {success : false, status : "no profile"}});
        }));

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
            .send(jack_a1);
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("no profile");

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
            .send(name_only);
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

        var response = await request(app)
            .post("/users/login")
            .type('json')
            .send(pass_only);
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
        await Account.deleteMany({});
        await Account.insertMany(before2);
        await models.disconnectDb()
    });
});