const request = require("supertest")
const app = require("../app").app;
var models = require("../__models__/models");
var constants = require("../__vars__/constants");
var Profile = models.Profile;

var kyle_p = {
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
    activityID : "-1"
}

/****************************************************************************
 ********************* SUCCESSFUL RESPONSE TESTS *************************
****************************************************************************/
var before1;
describe("successful tests", () => {
    beforeAll(async () => {
        await models.connectDb()
        before1 = await Profile.find().exec();
        await Profile.deleteMany({});
    });

    // test adding a profile
    test("add profile", async () => {
        var response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send(kyle_p);

        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("User profile insert successfully.");

        response = await request(app)
            .get("/profiles/all"); 
        
        expect(response.body[0]).toMatchObject(kyle_p);
    })

    // test searching a profile
    test("search profile", async () => {
        const response = await request(app)
            .post("/profiles/search")
            .type('json')
            .send({
                username : "12",
            })

        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("User found successfully.");
        expect(response.body.value).toMatchObject(kyle_p);
    })

    // test updating a profile
    test("update profile", async () => {
        var copy_k = JSON.parse(JSON.stringify(kyle_p));
        copy_k.phone = "5454";

        var response = await request(app)
            .post("/profiles/update")
            .type('json')
            .send(copy_k);
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("User profile updated successfully.");

        response = await request(app)
            .post("/profiles/search")
            .type('json')
            .send({
                username : "12",
            })
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("User found successfully.");
        expect(response.body.value).toMatchObject(copy_k);
    })

    test("join profile", async () => {
        var copy_k = JSON.parse(JSON.stringify(kyle_p));
        copy_k.activityID = "123act";
        copy_k.inActivity = true;
        copy_k.phone = "5454";

        var response = await request(app)
            .post("/profiles/join")
            .type('json')
            .send({username : "12", aid : "123act"});
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("User Joined successfully.");

        response = await request(app)
            .post("/profiles/search")
            .type('json')
            .send({
                username : "12",
            })
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("User found successfully.");
        expect(response.body.value).toMatchObject(copy_k);
    })

    test("leave profile", async () => {
        var copy_k = JSON.parse(JSON.stringify(kyle_p));
        copy_k.phone = "5454";

        var response = await request(app)
            .post("/profiles/leave")
            .type('json')
            .send({username : "12", aid : "123act"});
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("User Left the Activity successfully.");

        response = await request(app)
            .post("/profiles/search")
            .type('json')
            .send({
                username : "12",
            })
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("User found successfully.");
        expect(response.body.value).toMatchObject(copy_k);
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
        await Profile.deleteMany({});
        await Profile.insertMany(before1);
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
        before2 = await Profile.find().exec();
        await Profile.deleteMany({});
    });

    // test adding a profile
    test("add profile", async () => {
        var response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send(kyle_p);

        response = await request(app)
            .post("/profiles/add")
            .type('json')
            .send(kyle_p);
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Username already exists.");

        for(i=0; i<constants.bad_profiles.length; i++){
            response = await request(app)
                .post("/profiles/add")
                .type('json')
                .send(constants.bad_profiles[i]);
            expect(response.body.success).toBe(false)
            expect(response.body.status).toBe("Not well formed request.");
        }
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
                activityID : "-1"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Username does not exist.");

        for(i=0; i<constants.bad_profiles.length; i++){
            response = await request(app)
                .post("/profiles/update")
                .type('json')
                .send(constants.bad_profiles[i]);
            expect(response.body.success).toBe(false)
            expect(response.body.status).toBe("Not well formed request.");
        }
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

    test("join profile", async () => {
        var response = await request(app)
            .post("/profiles/join")
            .type('json')
            .send({
                username : "1239218932189",
                aid : "cool"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("User does not exist.");

        var response = await request(app)
            .post("/profiles/join")
            .type('json')
            .send({
                username : "12",
                aid : "cool"
            })

        var response = await request(app)
            .post("/profiles/join")
            .type('json')
            .send({
                username : "12",
                aid : "cool"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("User is already in an activity.");

        response = await request(app)
            .post("/profiles/join")
            .type('json')
            .send({
                weirdkey : "weird"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");
    })

    test("leave profile", async () => {
        var response = await request(app)
            .post("/profiles/leave")
            .type('json')
            .send({
                username : "1239218932189",
                aid : "cool"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("User does not exist.");

        var response = await request(app)
            .post("/profiles/leave")
            .type('json')
            .send({
                username : "12",
                aid : "blah"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("User is not in this activity.");

        var response = await request(app)
            .post("/profiles/leave")
            .type('json')
            .send({
                username : "12",
                aid : "cool"
            })

        var response = await request(app)
            .post("/profiles/leave")
            .type('json')
            .send({
                username : "12",
                aid : "cool"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("User is not in an activity.");

        response = await request(app)
            .post("/profiles/leave")
            .type('json')
            .send({
                weirdkey : "weird"
            })
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");
    })

    afterAll(async () => {
        await Profile.deleteMany({});
        await Profile.insertMany(before2);
        await models.disconnectDb()
    });
});