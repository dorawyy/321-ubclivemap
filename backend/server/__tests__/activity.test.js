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
var before; 
describe("successful tests", () => {
    beforeAll(async () => {
        await models.connectDb()
        before = await Activity.find().exec();
        await Activity.deleteMany({})
    });

    // test adding an activity
    test("add activity", async () => {
        await Activity.deleteOne({aid : "sparklingdangers"});
        const response = await request(app)
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

        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity insert successful.");
    })

    // test searching an activity
    test("search activity", async () => {
        const response = await request(app)
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
    })

    // test updating an activity
    test("update activity", async () => {
        var response = await request(app)
            .post("/activities/update")
            .type('json')
            .send({
                aid : "sparklingdangers",
                name : "Cpen321 Project",
                leader : "test",
                usernames : ["test","aplha","anotheruser"],
                info : "Understanding Javascript with TA",
                major : "CPEN",
                course : ["CPEN321", "CPEN331", "CPEN400"],
                school : "UBC",
                lat : "50.2321039",
                long : "-123.247360",
                status : "1"
            })
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity updated successfully.");

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
            usernames : ["test","aplha","anotheruser"],
            info : "Understanding Javascript with TA",
            major : "CPEN",
            course : ["CPEN321", "CPEN331", "CPEN400"],
            school : "UBC",
            lat : "50.2321039",
            long : "-123.247360",
            status : "1"
        }

        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity found successfully.");
        expect(response.body.value).toMatchObject(val);
    })

    // test deleting an activity
    test("delete activity", async () => {
        const response = await request(app)
            .post("/activities/delete")
            .type('json')
            .send({
                aid : "sparklingdangers",
            })
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity deleted successfully.");
    })

    // test sorting activities
    test("sort activities", async () => {
        var a1 = {
            aid : "1",
            name : "bad",
            leader : "test",
            usernames : ["test","aplha"],
            info : "Understanding Javascript with TA",
            major : "CPEN",
            course : ["CPEN321"],
            school : "UBC",
            lat : "49.267941",
            long : "-123.247360",
            status : "1"
        }

        var response = await request(app)
            .post("/activities/add")
            .type('json')
            .send(a1);

        var a4 = {
            aid : "3",
            name : "best",
            leader : "test",
            usernames : ["test","aplha"],
            info : "Understanding Javascript with TA",
            major : "CPEN",
            course : ["CPEN321", "CPEN331", "CPEN400"],
            school : "UBC",
            lat : "50",
            long : "-124",
            status : "1"
        }

        response = await request(app)
            .post("/activities/add")
            .type('json')
            .send(a4);

        var a3 = {
            aid : "3",
            name : "best",
            leader : "test",
            usernames : ["test","aplha"],
            info : "Understanding Javascript with TA",
            major : "CPEN",
            course : ["CPEN321", "CPEN331", "CPEN400"],
            school : "UBC",
            lat : "49.9",
            long : "-123.9",
            status : "1"
        }

        response = await request(app)
            .post("/activities/add")
            .type('json')
            .send(a3);

        var a2 = {
            aid : "2",
            name : "better",
            leader : "test",
            usernames : ["test","aplha"],
            info : "Understanding Javascript with TA",
            major : "CPEN",
            course : ["CPEN321", "CPEN331"],
            school : "UBC",
            lat : "49.9",
            long : "-123.9",
            status : "1"
        }

        response = await request(app)
            .post("/activities/add")
            .type('json')
            .send(a2);


        response = await request(app)
            .post("/activities/sort")
            .type('json')
            .send({
                user : {
                    name : "Kyle",
                    username : "12",
                    major : "CPEN",
                    CourseRegistered : [
                        "CPEN331", "CPEN321", "CPEN400"
                    ],
                    school : "UBC",
                    phone : "4444445555",
                    private : false,
                    inActivity : false,
                    activityID : -1
                },
                userlat : "50",
                userlong : "-124",
                maxradius : "2000",
                locationweight : 1,
                coursesweight : 1,
                majorweight : 1
            });
        
        expectedArr = [a4,a3,a2,a1];
        for(i=0; i<expectedArr.length; i++){
            expect(response.body[i]).toMatchObject(expectedArr[i]);
        }
    })

    afterAll(async () => {
        await Activity.deleteMany({});
        await Activity.insertMany(before);
        await models.disconnectDb()
    });
});

var before2;
describe("fail tests", () => {
    beforeAll(async () => {
        await models.connectDb()
        before2 = await Activity.find().exec();
        await Activity.deleteMany({})
        const response = await request(app)
            .post("/activities/add")
            .type('json')
            .send({
                aid : "1",
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
    });

    // test adding an activity
    test("add activity", async () => {
        var response = await request(app)
            .post("/activities/add")
            .type('json')
            .send({
                aid : "1",
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

        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Activity Id Taken");

        response = await request(app)
            .post("/activities/add")
            .type('json')
            .send({
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
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");
    })

    // test searching an activity
    test("search activity", async () => {
        var response = await request(app)
            .post("/activities/search")
            .type('json')
            .send({
                aid : "sparklingdangers",
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Activity does not exist.");

        response = await request(app)
            .post("/activities/search")
            .type('json')
            .send({
                weirdkey : "sparklingdangers",
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");
    })

    // test updating an activity
    test("update activity", async () => {
        var response = await request(app)
            .post("/activities/update")
            .type('json')
            .send({
                aid : "sparklingdangers",
                name : "Cpen321 Project",
                leader : "test",
                usernames : ["test","aplha","anotheruser"],
                info : "Understanding Javascript with TA",
                major : "CPEN",
                course : ["CPEN321", "CPEN331", "CPEN400"],
                school : "UBC",
                lat : "50.2321039",
                long : "-123.247360",
                status : "1"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Activity does not exist.");

        response = await request(app)
            .post("/activities/update")
            .type('json')
            .send({
                aid : "1",
                leader : "test",
                usernames : ["test","aplha","anotheruser"],
                info : "Understanding Javascript with TA",
                major : "CPEN",
                course : ["CPEN321", "CPEN331", "CPEN400"],
                school : "UBC",
                lat : "50.2321039",
                long : "-123.247360",
                status : "1"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");
    })

    // test deleting an activity
    test("delete activity", async () => {
        var response = await request(app)
            .post("/activities/delete")
            .type('json')
            .send({
                aid : "sparklingdangers",
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Activity does not exist.");

        response = await request(app)
            .post("/activities/delete")
            .type('json')
            .send({
                weirdkey : "sparklingdangers",
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");
    })

    // test sorting activities
    test("sort activities", async () => {
        response = await request(app)
            .post("/activities/sort")
            .type('json')
            .send({
                user : {
                    name : "Kyle",
                    major : "CPEN",
                    CourseRegistered : [
                        "CPEN331", "CPEN321", "CPEN400"
                    ],
                    school : "UBC",
                    phone : "4444445555",
                    private : false,
                    inActivity : false,
                    activityID : -1
                },
                userlat : "50",
                userlong : "-124",
                maxradius : "2000",
                locationweight : 1,
                coursesweight : 1,
                majorweight : 1
            });
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.2");
    })

    afterAll(async () => {
        await Activity.deleteMany({});
        await Activity.insertMany(before2);
        await models.disconnectDb()
    });
});