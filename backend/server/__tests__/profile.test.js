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

    // test adding a profile
    test("add profile", async () => {
        await Profile.deleteOne({name : "Kyle"});
        const response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send({
                name : "Kyle",
                username : "12",
                major : "CPEN",
                CourseRegistered : [
                    "CPEN331", "CPEN321"
                ],
                school : "UBC",
                phone : "4444444444",
                private : false,
                inActivity : false,
                activityID : -1
            })

        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("User profile insert successfully.");
    })

    // test searching a profile
    test("search profile", async () => {
        const response = await request(app)
            .post("/profiles/search")
            .type('json')
            .send({
                username : "12",
            })

        var val = {
            name : "Kyle",
            username : "12",
            major : "CPEN",
            CourseRegistered : [
                "CPEN331", "CPEN321"
            ],
            school : "UBC",
            phone : "4444444444",
            private : false,
            inActivity : false,
            activityID : -1
        }

        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("User found successfully.");
        expect(response.body.value).toMatchObject(val);
    })

    // test updating a profile
    test("update profile", async () => {
        var response = await request(app)
            .post("/profiles/update")
            .type('json')
            .send({
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
            })
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("User profile updated successfully.");

        response = await request(app)
            .post("/profiles/search")
            .type('json')
            .send({
                username : "12",
            })

        var val = {
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
        }

        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("User found successfully.");
        expect(response.body.value).toMatchObject(val);
    })

    // test deleting a profile
    test("delete profile", async () => {
        const response = await request(app)
            .post("/profiles/delete")
            .type('json')
            .send({
                username : "12",
            })
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("User profile deleted successfully.");
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
            .post("/profiles/add")
            .type('json')
            .send({
                name : "Kyle",
                username : "12",
                major : "CPEN",
                CourseRegistered : [
                    "CPEN331", "CPEN321"
                ],
                school : "UBC",
                phone : "4444444444",
                private : false,
                inActivity : false,
                activityID : -1
            })
    });

    // test adding a profile
    test("add profile", async () => {
        var response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send({
                name : "Kyle",
                username : "12",
                major : "CPEN",
                CourseRegistered : [
                    "CPEN331", "CPEN321"
                ],
                school : "UBC",
                phone : "4444444444",
                private : false,
                inActivity : false,
                activityID : -1
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Username already exists.");

        // no courseregistered
        response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send({
                name : "Kyle",
                username : "12",
                major : "CPEN",
                school : "UBC",
                phone : "4444444444",
                private : false,
                inActivity : false,
                activityID : -1
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

        // no name
        response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send({
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
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

        // no username
        response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send({
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
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

        // no major
        response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send({
                name : "Kyle",
                username : "12",
                CourseRegistered : [
                    "CPEN331", "CPEN321", "CPEN400"
                ],
                school : "UBC",
                phone : "4444445555",
                private : false,
                inActivity : false,
                activityID : -1
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

        // no school
        response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send({
                name : "Kyle",
                username : "12",
                major : "CPEN",
                CourseRegistered : [
                    "CPEN331", "CPEN321", "CPEN400"
                ],
                phone : "4444445555",
                private : false,
                inActivity : false,
                activityID : -1
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

        // no phone
        response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send({
                name : "Kyle",
                username : "12",
                major : "CPEN",
                CourseRegistered : [
                    "CPEN331", "CPEN321", "CPEN400"
                ],
                school : "UBC",
                private : false,
                inActivity : false,
                activityID : -1
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

        // no private
        response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send({
                name : "Kyle",
                username : "12",
                major : "CPEN",
                CourseRegistered : [
                    "CPEN331", "CPEN321", "CPEN400"
                ],
                school : "UBC",
                phone : "4444445555",
                inActivity : false,
                activityID : -1
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

        // no inactivity
        response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send({
                name : "Kyle",
                username : "12",
                major : "CPEN",
                CourseRegistered : [
                    "CPEN331", "CPEN321", "CPEN400"
                ],
                school : "UBC",
                phone : "4444445555",
                private : false,
                activityID : -1
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

        // no activityid
        response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send({
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
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");
    })

    // test searching a profile
    test("search profile", async () => {
        var response = await request(app)
            .post("/profiles/search")
            .type('json')
            .send({
                username : "2103920139210",
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Username does not exist.");

        response = await request(app)
            .post("/profiles/search")
            .type('json')
            .send({
                weirdkey : "weird"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");
    })

    // test updating a profile
    test("update profile", async () => {
        var response = await request(app)
            .post("/profiles/update")
            .type('json')
            .send({
                name : "Kyle",
                username : "12039210392109",
                major : "CPEN",
                CourseRegistered : [
                    "CPEN331", "CPEN321", "CPEN400"
                ],
                school : "UBC",
                phone : "4444445555",
                private : false,
                inActivity : false,
                activityID : -1
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Username does not exist.");

        response = await request(app)
            .post("/profiles/update")
            .type('json')
            .send({
                name : "Kyle",
                username : "12",
                major : "CPEN",
                school : "UBC",
                phone : "4444445555",
                private : false,
                inActivity : false,
                activityID : -1
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");
    })

    // test deleting a profile
    test("delete profile", async () => {
        var response = await request(app)
            .post("/profiles/delete")
            .type('json')
            .send({
                username : "1239218932189",
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Username does not exist.");
        
        response = await request(app)
            .post("/profiles/delete")
            .type('json')
            .send({
                weirdkey : "weird"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");
    })

    afterAll(async () => {
        await models.disconnectDb()
    });
});