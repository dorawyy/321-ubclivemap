
/**
 * @jest-environment node
 */

const request = require("supertest")
const app = require("../app").app;
var constants = require("../__vars__/constants")
var models = require("../__models__/models");
var admin = require("../__modules__/notifications").admin;
var Account = models.Account;
var Profile = models.Profile;
var Activity = models.Activity;


admin.messaging = jest.fn();
admin.messaging().sendToDevice = jest.fn((arg1,arg2,arg3) => new Promise((resolve,reject) => {
    resolve({data : {success : true}});
}));

var beforeUsr;
var beforeAct;
var beforePro;
describe("successful tests", () => {
    beforeAll(async () => {
        await models.connectDb()
        beforeUsr = await Account.find().exec();
        await Account.deleteMany({})

        beforeAct = await Activity.find().exec();
        await Activity.deleteMany({})

        beforePro = await Profile.find().exec();
        await Profile.deleteMany({})
    });


    test("user makes an account and profile", async () =>{
        var testacc = {
            name : "test",
            password : "pass1234",
            token : "cooltoken"
        }
        var response = await request(app)
            .post("/users/register")
            .type('json')
            .send(testacc);
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Account registered successfully.");

        response = await request(app)
            .get("/users/all");
        expect(response.body[0].name).toBe("test");

        response = await request(app)
            .get("/notifications/alltokens")
        expect(response.body[0].username).toBe("test");
        expect(response.body[0].token).toBe("cooltoken");
    })

    test("user logs in", async () => {
        var leader = JSON.parse(JSON.stringify(constants.kyle_p));
        leader.username = "test";
        var response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send(leader);
        response = await request(app)
            .post("/users/login")
            .type('json')
            .send({
                name : "test",
                password : "pass1234",
                token : "coolertoken"
            });

        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Authentication successful.");
        expect(response.body.value).toMatchObject(leader)

        response = await request(app)
            .get("/notifications/alltokens")
        expect(response.body[0].username).toBe("test");
        expect(response.body[0].token).toBe("coolertoken");
    })

    test("make user leader of activity", async () => {
        var leader = JSON.parse(JSON.stringify(constants.kyle_p));
        leader.username = "test";
        var response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send(leader);
        response = await request(app)
            .post("/activities/add")
            .type('json')
            .send(constants.a1);

        response = await request(app)
            .post("/activities/search")
            .type('json')
            .send({
                aid : "1",
            })

        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity found successfully.");
        expect(response.body.value.leader).toBe("test");

        response = await request(app)
            .post("/profiles/search")
            .type('json')
            .send({
                username : "test",
            })
        expect(response.body.value.inActivity).toBe(true);
    })

    test("user joins an activity", async () => {
        var response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send(constants.kyle_p);

        var response = await request(app)
            .post("/activities/joinupdate")
            .type('json')
            .send({
                username : "kyle",
                aid : "1",
            })
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity Joined successfully.");

        response = await request(app)
            .post("/activities/search")
            .type('json')
            .send({
                aid : "1",
            })

        var correct_val = JSON.parse(JSON.stringify(constants.a1));
        correct_val.usernames.push("kyle");
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity found successfully.");
        expect(response.body.value).toMatchObject(correct_val);

        response = await request(app)
            .post("/profiles/search")
            .type('json')
            .send({
                username : "kyle",
            })
        correct_val = JSON.parse(JSON.stringify(constants.kyle_p));
        correct_val.inActivity = true;
        correct_val.activityID = "1";
        expect(response.body.success).toBe(true);
        expect(response.body.value).toMatchObject(correct_val);
    });

    test("user leaves an activity", async () => {
        var response = await request(app)
            .post("/activities/leaveupdate")
            .type('json')
            .send({
                username : "kyle",
                aid : "1",
            })
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity Left successfully.");

        response = await request(app)
            .post("/activities/search")
            .type('json')
            .send({
                aid : "1",
            })
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity found successfully.");
        expect(response.body.value).toMatchObject(constants.a1);

        response = await request(app)
            .post("/profiles/search")
            .type('json')
            .send({
                username : "kyle",
            })

        expect(response.body.value).toMatchObject(constants.kyle_p);
    });

    test("sort activity find user", async () => {
        response = await request(app)
            .post("/activities/sortnouser")
            .type('json')
            .send({
                username : "kyle",
                userlat : "50",
                userlong : "-124",
                maxradius : "2000",
                locationweight : 1,
                coursesweight : 1,
                majorweight : 1
            });

        expectedArr = [constants.a1];
        for(i=0; i<expectedArr.length; i++){
            expect(response.body[i]).toMatchObject(expectedArr[i]);
        }
    });

    afterAll(async () => {
        await Account.deleteMany({});
        await Account.insertMany(beforeUsr);

        await Activity.deleteMany({});
        await Activity.insertMany(beforeAct);

        await Profile.deleteMany({});
        await Profile.insertMany(beforePro);
        await models.disconnectDb();
        done();
    });
})

var beforeAct2;
var beforePro2;
describe("fail tests", () => {
    beforeAll(async () => {
        await models.connectDb()
        beforeAct2 = await Activity.find().exec();
        await Activity.deleteMany({})

        beforePro2 = await Profile.find().exec();
        await Profile.deleteMany({})
    });

    var leader = JSON.parse(JSON.stringify(constants.kyle_p));
    leader.username = "test";
    leader.inActivity = true;
    leader.activityID = "2";

    test("make user leader of activity", async () => {
        var response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send(leader);

        response = await request(app)
            .post("/activities/add")
            .type('json')
            .send(constants.a1);
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("User is already in an activity.");

        leader.inActivity = false;
        var response = await request(app)
            .post("/profiles/update")
            .type('json')
            .send(leader);

        response = await request(app)
            .post("/activities/add")
            .type('json')
            .send(constants.a1);
        expect(response.body.success).toBe(true);

        var otheractivity = JSON.parse(JSON.stringify(constants.a1));
        otheractivity.leader = "doesntexist";
        otheractivity.aid = "2";
        response = await request(app)
            .post("/activities/add")
            .type('json')
            .send(otheractivity);
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("User does not exist.");

        for(i=0; i<constants.bad_activities.length; i++){
            response = await request(app)
                .post("/activities/add")
                .type('json')
                .send(constants.bad_activities[i]);
            expect(response.body.success).toBe(false)
            expect(response.body.status).toBe("Not well formed request.");
        }
    })

    test("user joins an activity", async () => {
        var response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send(constants.kyle_inact_p);
        expect(response.body.success).toBe(true)

        response = await request(app)
            .post("/activities/joinupdate")
            .type('json')
            .send({
                username : "jack",
                aid : "1"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("User does not exist.");

        response = await request(app)
            .post("/activities/joinupdate")
            .type('json')
            .send({
                username : "kyle",
                aid : "1"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("User is already in an activity.");

        response = await request(app)
            .post("/activities/joinupdate")
            .type('json')
            .send({
                aid : "1"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

        response = await request(app)
            .post("/activities/joinupdate")
            .type('json')
            .send({
                username : "heyo"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");
    });

    test("user leaves an activity", async () => {
        var response = await request(app)
            .post("/profiles/delete")
            .type('json')
            .send({username : "kyle"});
        var response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send(constants.kyle_p);

        var response = await request(app)
            .post("/activities/leaveupdate")
            .type('json')
            .send({
                username : "kyle",
                aid : "1"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("User is not in an activity.");

        leader.inActivity = true;
        leader.activityID = "2";
        var response = await request(app)
            .post("/profiles/update")
            .type('json')
            .send(leader);
        var response = await request(app)
            .post("/activities/leaveupdate")
            .type('json')
            .send({
                username : "test",
                aid : "1"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("User is not in this activity.");

        var response = await request(app)
            .post("/activities/leaveupdate")
            .type('json')
            .send({
                username : "blahalba",
                aid : "1"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("User does not exist.");

        var response = await request(app)
            .post("/activities/leaveupdate")
            .type('json')
            .send({
                username : "test1",
                aid : "bjasgk"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Activity does not exist.");

        var response = await request(app)
            .post("/activities/leaveupdate")
            .type('json')
            .send({
                aid : "bjasgk"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

        var response = await request(app)
            .post("/activities/leaveupdate")
            .type('json')
            .send({
                username : "test1"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");
    });

    test("sort activity find user", async () => {
        response = await request(app)
            .post("/activities/sortnouser")
            .type('json')
            .send({
                username : "oajsfg",
                userlat : "50",
                userlong : "-124",
                maxradius : "2000",
                locationweight : 1,
                coursesweight : 1,
                majorweight : 1
            });

        expect(response.body.status).toBe("Username does not exist.");
        expect(response.body.success).toBe(false)

        response = await request(app)
            .post("/activities/sortnouser")
            .type('json')
            .send({
                userlat : "50",
                userlong : "-124",
                maxradius : "2000",
                locationweight : 1,
                coursesweight : 1,
                majorweight : 1
            });

        expect(response.body.status).toBe("Not well formed request.");
        expect(response.body.success).toBe(false)

        for(i=0; i<constants.bad_profiles.length; i++){
            response = await request(app)
                .post("/activities/sort")
                .type('json')
                .send({
                    user : constants.bad_profiles[i],
                    userlat : "50",
                    userlong : "-124",
                    maxradius : "2000",
                    locationweight : 1,
                    coursesweight : 1,
                    majorweight : 1
                });
            expect(response.body.success).toBe(false)
            expect(response.body.status).toBe("Not well formed request.");
        }
    });

    afterAll(async () => {
        await Activity.deleteMany({});
        await Activity.insertMany(beforeAct2);

        await Profile.deleteMany({});
        await Profile.insertMany(beforePro2);
        await models.disconnectDb()
        done();
    });
})