const request = require("supertest")
const app = require("../app").app;
const axios = require("../app").axios;
var models = require("../__models__/models");
var constants = require("../__vars__/constants");
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

    test("update activity", async () => {
        var response = await request(app)
            .post("/activities/update")
            .type('json')
            .send({
                aid : "sparklingdangers",
                name : "Cpen321 Project",
                leader : "test",
                usernames : ["test","aplha","anotheruser","ano"],
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
    })

    test("join and update activity", async () => {
        axios.post = jest.fn((url, jsonObj) => new Promise((resolve, reject) => {
            resolve({
                data : {
                    success : true
                }
            });
        }));

        var response = await request(app)
            .post("/activities/joinupdate")
            .type('json')
            .send({
                username : "Kyle",
                aid : "sparklingdangers",
            })
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity Joined successfully.");

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
            usernames : ["test","aplha","anotheruser","ano","Kyle"],
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

    test("leave and update activity", async () => {
        axios.post = jest.fn((url, jsonObj) => new Promise((resolve, reject) => {
            resolve({
                data : {
                    success : true
                }
            });
        }));

        var response = await request(app)
            .post("/activities/leaveupdate")
            .type('json')
            .send({
                username : "Kyle",
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
            usernames : ["test","aplha","anotheruser","ano"],
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
    test("sort activities with given user information", async () => {
        var response = await request(app)
            .post("/activities/add")
            .type('json')
            .send(constants.a1);

        response = await request(app)
            .post("/activities/add")
            .type('json')
            .send(constants.a4);

        // this activity is very far away
        response = await request(app)
            .post("/activities/add")
            .type('json')
            .send(constants.a5);

        response = await request(app)
            .post("/activities/add")
            .type('json')
            .send(constants.a3);

        response = await request(app)
            .post("/activities/add")
            .type('json')
            .send(constants.a2);

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
                maxradius : "100",
                locationweight : 1,
                coursesweight : 1,
                majorweight : 1
            });
        
        expectedArr = [constants.a4,constants.a3,constants.a2,constants.a1];
        for(i=0; i<expectedArr.length; i++){
            expect(response.body[i]).toMatchObject(expectedArr[i]);
        }
    })

    // test sorting activities
    test("sort activities without user information", async () => {
        axios.post = jest.fn((url, jsonObj) => new Promise((resolve, reject) => {
            if(!jsonObj.hasOwnProperty("username")){
                reject(new Error("bad request"));
            }
            resolve({
                data : {
                    value : {
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
                }
            });
        }));
        
        response = await request(app)
            .post("/activities/sortnouser")
            .type('json')
            .send({
                username : "12",
                userlat : "50",
                userlong : "-124",
                maxradius : "100",
                locationweight : 1,
                coursesweight : 1,
                majorweight : 1
            });
        
        expectedArr = [constants.a4,constants.a3,constants.a2,constants.a1];
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
            .send(constants.a1);
    });

    // test adding an activity
    test("add activity", async () => {
        var response = await request(app)
            .post("/activities/add")
            .type('json')
            .send(constants.a1);
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Activity Id Taken");

        for(i=0; i<constants.bad_activities.length; i++){
            response = await request(app)
                .post("/activities/add")
                .type('json')
                .send(constants.bad_activities[i]);
            expect(response.body.success).toBe(false)
            expect(response.body.status).toBe("Not well formed request.");
        }
    })

    // test searching an activity
    test("search activity", async () => {
        var response = await request(app)
            .post("/activities/search")
            .type('json')
            .send({
                aid : "sparklingdangers",
            });
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

        for(i=0; i<constants.bad_activities.length; i++){
            response = await request(app)
                .post("/activities/update")
                .type('json')
                .send(constants.bad_activities[i]);
            expect(response.body.success).toBe(false)
            expect(response.body.status).toBe("Not well formed request.");
        }
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
                userlat : "50",
                userlong : "-124",
                maxradius : "2000",
                locationweight : 1,
                coursesweight : 1,
                majorweight : 1
            });
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

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

        response = await request(app)
            .post("/activities/sort")
            .type('json')
            .send({
                user : constants.kyle_p[i],
                userlong : "-124",
                maxradius : "2000",
                locationweight : 1,
                coursesweight : 1,
                majorweight : 1
            });
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

        response = await request(app)
            .post("/activities/sort")
            .type('json')
            .send({
                user : constants.kyle_p[i],
                userlat : "50",
                maxradius : "2000",
                locationweight : 1,
                coursesweight : 1,
                majorweight : 1
            });
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

        response = await request(app)
        .post("/activities/sort")
        .type('json')
        .send({
            user : constants.kyle_p[i],
            userlat : "50",
            userlong : "-124",
            locationweight : 1,
            coursesweight : 1,
            majorweight : 1
        });
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

        response = await request(app)
        .post("/activities/sort")
        .type('json')
        .send({
            user : constants.kyle_p[i],
            userlat : "50",
            userlong : "-124",
            maxradius : "2000",
            coursesweight : 1,
            majorweight : 1
        });
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

        response = await request(app)
        .post("/activities/sort")
        .type('json')
        .send({
            user : constants.kyle_p[i],
            userlat : "50",
            userlong : "-124",
            maxradius : "2000",
            locationweight : 1,
            majorweight : 1
        });
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

        response = await request(app)
        .post("/activities/sort")
        .type('json')
        .send({
            user : constants.kyle_p[i],
            userlat : "50",
            userlong : "-124",
            maxradius : "2000",
            locationweight : 1,
            coursesweight : 1,
        });
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");
    })

    // test sorting activities
    test("sort activities without user information", async () => {
        axios.post = jest.fn((url, jsonObj) => new Promise((resolve, reject) => {
            if(!jsonObj.hasOwnProperty("username") || jsonObj.username == "test"){
                reject(new Error("bad request"));
            }
            resolve(
                {
                    val : {
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
                });
        }));
        
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
        
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");

        response = await request(app)
            .post("/activities/sortnouser")
            .type('json')
            .send({
                username : "test",
                userlat : "50",
                userlong : "-124",
                maxradius : "2000",
                locationweight : 1,
                coursesweight : 1,
                majorweight : 1
            });
        
        expect(response.body.success).toBe(false)
    })

    afterAll(async () => {
        await Activity.deleteMany({});
        await Activity.insertMany(before2);
        await models.disconnectDb()
    });
});