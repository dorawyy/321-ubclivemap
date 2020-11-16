/**
 * @jest-environment node
 */

const request = require("supertest")
const app = require("../app").app;
const axios = require("../app").axios;
var constants = require("../__vars__/constants")
var models = require("../__models__/models");
var Account = models.Account;
var Profile = models.Profile;
var Activity = models.Activity;


var beforeAct;
var beforePro;
describe("successful tests", () => {
    beforeAll(async () => {
        await models.connectDb()
        beforeAct = await Activity.find().exec();
        await Activity.deleteMany({})

        beforePro = await Profile.find().exec();
        await Profile.deleteMany({})
    });

    test("user joins an activity", async () => {
        var response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send(constants.kyle_p);
        
        response = await request(app)
            .post("/activities/add")
            .type('json')
            .send(constants.a1);

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

    test("add activity with leader", async () => {
        var response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send({
                name : "Jack",
                username : "leadertest",
                major : "CPEN",
                CourseRegistered : [
                    "CPEN331", "CPEN321"
                ],
                school : "UBC",
                phone : "4444444444",
                private : false,
                inActivity : false,
                activityID : "-1"
            })

        response = await request(app)
            .post("/activities/addleader")
            .type('json')
            .send({
                aid : "leadertestact",
                name : "Cpen321 Project",
                leader : "leadertest",
                usernames : ["test","aplha"],
                info : "Understanding Javascript with TA",
                major : "CPEN",
                course : ["CPEN321", "CPEN331"],
                school : "UBC",
                lat : "1",
                long : "-2",
                status : "1"
            })

        expect(response.body.status).toBe("Activity insert successful.");
        expect(response.body.success).toBe(true)
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
        await Activity.deleteMany({});
        await Activity.insertMany(beforeAct);

        await Profile.deleteMany({});
        await Profile.insertMany(beforePro);
        await models.disconnectDb()
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

        var response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send(constants.kyle_inact_p);
        response = await request(app)
            .post("/activities/add")
            .type('json')
            .send(constants.a1);
        response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send(constants.noact_p1);
    });

    test("user joins an activity", async () => {
        var response = await request(app)
            .post("/activities/joinupdate")
            .type('json')
            .send({
                username : "jack",
                aid : "1"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("User does not exist.");

        var response = await request(app)
            .post("/activities/joinupdate")
            .type('json')
            .send({
                username : "kyle",
                aid : "1"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("User is already in an activity.");

        var response = await request(app)
            .post("/activities/joinupdate")
            .type('json')
            .send({
                aid : "1"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

        var response = await request(app)
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
            .post("/activities/leaveupdate")
            .type('json')
            .send({
                username : "test1",
                aid : "1"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("User is not in an activity.");

        var response = await request(app)
            .post("/activities/leaveupdate")
            .type('json')
            .send({
                username : "kyle",
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
                aid : "1"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("User is not in an activity.");

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

    test("add activity with leader", async () => {
        response = await request(app)
            .post("/activities/addleader")
            .type('json')
            .send({
                aid : "leadertestact",
                name : "Cpen321 Project",
                leader : "kyle",
                usernames : ["test","aplha"],
                info : "Understanding Javascript with TA",
                major : "CPEN",
                course : ["CPEN321", "CPEN331"],
                school : "UBC",
                lat : "1",
                long : "-2",
                status : "1"
            })

        expect(response.body.status).toBe("Leader is in an activity.");
        expect(response.body.success).toBe(false)

        response = await request(app)
            .post("/activities/addleader")
            .type('json')
            .send({
                aid : "leadertestact",
                name : "Cpen321 Project",
                leader : "oadgdakjg",
                usernames : ["test","aplha"],
                info : "Understanding Javascript with TA",
                major : "CPEN",
                course : ["CPEN321", "CPEN331"],
                school : "UBC",
                lat : "1",
                long : "-2",
                status : "1"
            })

        expect(response.body.status).toBe("Leader does not exist.");
        expect(response.body.success).toBe(false)

        response = await request(app)
            .post("/activities/addleader")
            .type('json')
            .send({
                aid : "1",
                name : "Cpen321 Project",
                leader : "oadgdakjg",
                usernames : ["test","aplha"],
                info : "Understanding Javascript with TA",
                major : "CPEN",
                course : ["CPEN321", "CPEN331"],
                school : "UBC",
                lat : "1",
                long : "-2",
                status : "1"
            })

        expect(response.body.status).toBe("Activity Id Taken");
        expect(response.body.success).toBe(false)
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
    });

    afterAll(async () => {
        await Activity.deleteMany({});
        await Activity.insertMany(beforeAct2);

        await Profile.deleteMany({});
        await Profile.insertMany(beforePro2);
        await models.disconnectDb()
    });
})