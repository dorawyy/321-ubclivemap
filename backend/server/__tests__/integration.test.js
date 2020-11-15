/**
 * @jest-environment node
 */

const request = require("supertest")
const app = require("../app").app;
const axios = require("../app").axios;
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
        console.log(app);
        var response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send({
                name : "Kyle",
                username : "kyle",
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
            .post("/activities/add")
            .type('json')
            .send({
                aid : "sparklingdangers",
                name : "Cpen321 Project",
                leader : "test",
                usernames : ["test","aplha"],
                info : "Understanding Javascript with TA",
                major : "CPEN",
                course : ["CPEN321", "CPEN331"],
                school : "UBC",
                lat : "49.267941",
                long : "-123.247360",
                status : "1"
            })

        var response = await request(app)
            .post("/activities/joinupdate")
            .type('json')
            .send({
                username : "kyle",
                aid : "sparklingdangers",
            })
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity Joined successfully.");

        console.log("HADH")

        response = await request(app)
            .post("/activities/search")
            .type('json')
            .send({
                aid : "sparklingdangers",
            })

        var val = {
            aid : "sparklingdangers",
            name : "Cpen321 Project",
            leader : "test",
            usernames : ["test","aplha","kyle"],
            info : "Understanding Javascript with TA",
            major : "CPEN",
            course : ["CPEN321", "CPEN331"],
            school : "UBC",
            lat : "49.267941",
            long : "-123.247360",
            status : "1"
        }
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity found successfully.");
        expect(response.body.value).toMatchObject(val);

        response = await request(app)
            .post("/profiles/search")
            .type('json')
            .send({
                username : "kyle",
            })

        val = {
            name : "Kyle",
            username : "kyle",
            major : "CPEN",
            CourseRegistered : [
                "CPEN331", "CPEN321"
            ],
            school : "UBC",
            phone : "4444444444",
            private : false,
            inActivity : true,
            activityID : "sparklingdangers"
        }
        expect(response.body.value).toMatchObject(val);
    });

    test("user leaves an activity", async () => {
        var response = await request(app)
            .post("/activities/leaveupdate")
            .type('json')
            .send({
                username : "kyle",
                aid : "sparklingdangers",
            })
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity Left successfully.");

        response = await request(app)
            .post("/activities/search")
            .type('json')
            .send({
                aid : "sparklingdangers",
            })

        var val = {
            aid : "sparklingdangers",
            name : "Cpen321 Project",
            leader : "test",
            usernames : ["test","aplha"],
            info : "Understanding Javascript with TA",
            major : "CPEN",
            course : ["CPEN321", "CPEN331"],
            school : "UBC",
            lat : "49.267941",
            long : "-123.247360",
            status : "1"
        }
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity found successfully.");
        expect(response.body.value).toMatchObject(val);

        response = await request(app)
            .post("/profiles/search")
            .type('json')
            .send({
                username : "kyle",
            })

        val = {
            name : "Kyle",
            username : "kyle",
            major : "CPEN",
            CourseRegistered : [
                "CPEN331", "CPEN321"
            ],
            school : "UBC",
            phone : "4444444444",
            private : false,
            inActivity : false,
            activityID : "-1"
        }
        expect(response.body.value).toMatchObject(val);
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

        var val = {
            aid : "sparklingdangers",
            name : "Cpen321 Project",
            leader : "test",
            usernames : ["test","aplha"],
            info : "Understanding Javascript with TA",
            major : "CPEN",
            course : ["CPEN321", "CPEN331"],
            school : "UBC",
            lat : "49.267941",
            long : "-123.247360",
            status : "1"
        }

        expectedArr = [val];
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