const request = require("supertest");
const app = require("../app").app;
var sharedfuncs = require("../__modules__/sharedfunctions");
var models = require("../__models__/models");
var admin = require("../__modules__/notifications").admin;
var Token = models.Token;


/****************************************************************************
 ********************* SUCCESSFUL RESPONSE TESTS *************************
****************************************************************************/
var before1;
describe("successful tests", () => {
    beforeAll(async () => {
        await models.connectDb()
        before1 = await Token.find().exec();
        await Token.deleteMany({});
    });

    test("add token", async () => {
        var token_add = {username: "cool", token: "cooltoken"};
        var response = await request(app)
            .post("/notifications/addtoken")
            .type('json')
            .send(token_add);
        expect(response.body.success).toBe(true);
        expect(response.body.status).toBe("Token insert successfully.");

        var response = await request(app)
            .get("/notifications/alltokens")
        expect(response.body[0]).toMatchObject(token_add);
    })

    test("update token", async () => {
        var token_add = {username: "cool", token: "coolertoken"};
        var response = await request(app)
            .post("/notifications/addtoken")
            .type('json')
            .send(token_add);
        expect(response.body.success).toBe(true);
        expect(response.body.status).toBe("Token insert successfully.");

        var response = await request(app)
            .get("/notifications/alltokens")
        expect(response.body[0]).toMatchObject(token_add);
    })

    test("send notif", async() => {
        admin.messaging = jest.fn();
        admin.messaging().sendToDevice = jest.fn((arg1,arg2,arg3) => new Promise((resolve,reject) => {
            resolve({data : {success : true}});
        }));

        const response = await request(app)
            .post("/notifications/send")
            .type('json')
            .send({username: "cool", title: "cool title", message:"cool message"});
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Notification sent successfully");
    })

    afterAll(async () => {
        await Token.deleteMany({});
        await Token.insertMany(before1);
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
        before2 = await Token.find().exec();
        await Token.deleteMany({});
    });

    test("add token", async () => {
        var response = await request(app)
            .post("/notifications/addtoken")
            .type('json')
            .send({
                token: "cooltoken"
            });
        expect(response.body.success).toBe(false);
        expect(response.body.status).toBe("Not well formed request.");

        var response = await request(app)
            .post("/notifications/addtoken")
            .type('json')
            .send({
                username: "cool"
            });
        expect(response.body.success).toBe(false);
        expect(response.body.status).toBe("Not well formed request.");
    })

    test("send notif", async() => {
        admin.messaging = jest.fn();
        admin.messaging().sendToDevice = jest.fn((arg1,arg2,arg3) => new Promise((resolve,reject) => {
            resolve({data : {success : true}});
        }));

        var response = await request(app)
            .post("/notifications/send")
            .type('json')
            .send({
                    title: "cool title", 
                    message:"cool message"
                });
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

        response = await request(app)
            .post("/notifications/send")
            .type('json')
            .send({
                    username: "cool", 
                    message:"cool message"
                });
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

        response = await request(app)
            .post("/notifications/send")
            .type('json')
            .send({
                    username: "cool", 
                    title: "cool title"
                });
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

        response = await request(app)
            .post("/notifications/send")
            .type('json')
            .send({username: "cool", title: "cool title", message:"cool message"});
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Username does not exist.");

        admin.messaging().sendToDevice = jest.fn((arg1,arg2,arg3) => new Promise((resolve,reject) => {
            reject("exception");
        }));

        var token_add = {username: "cool", token: "coolertoken"};
        response = await request(app)
            .post("/notifications/addtoken")
            .type('json')
            .send(token_add);
        expect(response.body.success).toBe(true);
        expect(response.body.status).toBe("Token insert successfully.");

        response = await request(app)
            .post("/notifications/send")
            .type('json')
            .send({username: "cool", title: "cool title", message:"cool message"});
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("ERROR: exception");
    })

    afterAll(async () => {
        await Token.deleteMany({});
        await Token.insertMany(before2);
        await models.disconnectDb()
    });
});