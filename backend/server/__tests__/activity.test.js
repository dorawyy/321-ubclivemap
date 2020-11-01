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

    afterAll(async () => {
        await models.disconnectDb()
    });
});
