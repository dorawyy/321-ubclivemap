const request = require("supertest")
const app = require("../app").app;
const axios = require("../app").axios;
var sharedfuncs = require("../__modules__/sharedfunctions");
var models = require("../__models__/models");
var constants = require("../__vars__/constants");
var Activity = models.Activity;

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
        sharedfuncs.axiosPostRequest = jest.fn((arg1,arg2,arg3) => new Promise((resolve,reject) => {
            resolve({data : {success : true}});
        }));

        var response = await request(app)
            .post("/activities/add")
            .type('json')
            .send(constants.a1);

        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity insert successful.");

        var response = await request(app)
            .post("/activities/add")
            .type('json')
            .send(constants.a2);

        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity insert successful.");

        response = await request(app)
            .get("/activities/all");
        expect(response.body).toMatchObject([constants.a1,constants.a2]);
    })

    // test searching an activity
    test("search activity", async () => {
        const response = await request(app)
            .post("/activities/search")
            .type('json')
            .send({
                aid : constants.a1.aid 
            })

        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity found successfully.");
        expect(response.body.value).toMatchObject(constants.a1);
    })

    var new_a1;
    // test updating an activity
    test("update activity", async () => {
        new_a1 = JSON.parse(JSON.stringify(constants.a1));
        new_a1.usernames.push("anotheruser");
        var response = await request(app)
            .post("/activities/update")
            .type('json')
            .send(new_a1);
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity updated successfully.");

        response = await request(app)
            .post("/activities/search")
            .type('json')
            .send({
                aid : constants.a1.aid
            })
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity found successfully.");
        expect(response.body.value).toMatchObject(new_a1);
    })

    test("join and update activity", async () => {
        sharedfuncs.axiosPostRequest = jest.fn((arg1,arg2,arg3) => new Promise((resolve,reject) => {
            resolve({data : {success : true}});
        }));

        var response = await request(app)
            .post("/activities/joinupdate")
            .type('json')
            .send({
                username : "Kyle",
                aid : new_a1.aid
            })
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity Joined successfully.");

        new_a1.usernames.push("Kyle");

        response = await request(app)
            .post("/activities/search")
            .type('json')
            .send({
                aid : new_a1.aid
            })
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity found successfully.");
        expect(response.body.value).toMatchObject(new_a1);
    })

    test("leave and update activity", async () => {
        sharedfuncs.axiosPostRequest = jest.fn((arg1,arg2,arg3) => new Promise((resolve,reject) => {
            resolve({data : {success : true}});
        }));

        var response = await request(app)
            .post("/activities/leaveupdate")
            .type('json')
            .send({
                username : "Kyle",
                aid : new_a1.aid
            })
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity Left successfully.");
        response = await request(app)
            .post("/activities/search")
            .type('json')
            .send({
                aid : new_a1.aid
            })
        new_a1.usernames = new_a1.usernames.filter(e => e !== "Kyle");
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity found successfully.");
        expect(response.body.value).toMatchObject(new_a1);

        //leader leaves
        var response = await request(app)
            .post("/activities/joinupdate")
            .type('json')
            .send({
                username : "Kyle",
                aid : new_a1.aid
            })
        new_a1.usernames.push("Kyle");
        var old_leader = new_a1.leader;
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity Joined successfully.");

        var response = await request(app)
            .post("/activities/leaveupdate")
            .type('json')
            .send({
                username : new_a1.leader,
                aid : new_a1.aid
            })
        new_a1.usernames = new_a1.usernames.filter(e => e !== old_leader);
        new_a1.leader = new_a1.usernames[0];
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity Left successfully.");
        response = await request(app)
            .post("/activities/search")
            .type('json')
            .send({
                aid : new_a1.aid
            })
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity found successfully.");
        expect(response.body.value).toMatchObject(new_a1);

        var response = await request(app)
            .post("/activities/leaveupdate")
            .type('json')
            .send({
                username : "Kyle",
                aid : new_a1.aid
            })
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity Left successfully.");

        var response = await request(app)
            .post("/activities/leaveupdate")
            .type('json')
            .send({
                username : "anotheruser",
                aid : new_a1.aid
            })
        expect(response.body.success).toBe(true)
        expect(response.body.status).toBe("Activity Left successfully.");
        response = await request(app)
            .post("/activities/search")
            .type('json')
            .send({
                aid : new_a1.aid
            })
        
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Activity does not exist.");
    })

    // test deleting an activity
    test("delete activity", async () => {
        const response = await request(app)
            .post("/activities/delete")
            .type('json')
            .send({
                aid : constants.a2.aid
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
        sharedfuncs.axiosPostRequest = jest.fn((arg1,arg2,arg3) => new Promise((resolve,reject) => {
            resolve({data : {
                success : true,
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
            }});
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
    });

    // test adding an activity
    test("add activity", async () => {
        sharedfuncs.axiosPostRequest = jest.fn((arg1,arg2,arg3) => new Promise((resolve,reject) => {
            if(arg3.username == "catch test"){
                reject("exception");
            }
            resolve({data : {success : true, status : "profile error"}});
        }));
        var response = await request(app)
            .post("/activities/add")
            .type('json')
            .send(constants.a1);

        response = await request(app)
            .post("/activities/add")
            .type('json')
            .send(constants.a1);
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Activity Id Taken");

        response = await request(app)
            .post("/activities/add")
            .type('json')
            .send(constants.a6);
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("ERROR: exception");

        sharedfuncs.axiosPostRequest = jest.fn((arg1,arg2,arg3) => new Promise((resolve,reject) => {
            resolve({data : {success : false, status : "profile error"}});
        }));

        var response = await request(app)
            .post("/activities/add")
            .type('json')
            .send(constants.a2);
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("profile error");

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
                user : constants.kyle_p,
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
                user : constants.kyle_p,
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
            user : constants.kyle_p,
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
            user : constants.kyle_p,
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
            user : constants.kyle_p,
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
            user : constants.kyle_p,
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
        sharedfuncs.axiosPostRequest = jest.fn((arg1,arg2,arg3) => new Promise((resolve,reject) => {
            if(arg3.username == "catch test"){
                reject("exception");
            }
            resolve(
                {
                    data : {
                        success : false,
                        status : "search error"
                    }
                });
        }));
        response = await request(app)
            .post("/activities/sortnouser")
            .type('json')
            .send({
                username : "name",
                userlat : "50",
                userlong : "-124",
                maxradius : "2000",
                locationweight : 1,
                coursesweight : 1,
                majorweight : 1
            });
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("search error");

        response = await request(app)
            .post("/activities/sortnouser")
            .type('json')
            .send({
                username : "catch test",
                userlat : "50",
                userlong : "-124",
                maxradius : "2000",
                locationweight : 1,
                coursesweight : 1,
                majorweight : 1
            });
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("ERROR: exception");

        response = await request(app)
            .post("/activities/sortnouser")
            .type('json')
            .send({
                username : "name",
                userlong : "-124",
                maxradius : "2000",
                locationweight : 1,
                coursesweight : 1,
                majorweight : 1
            });
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");
    })

    test("join update test", async () => {
        sharedfuncs.axiosPostRequest = jest.fn((arg1,arg2,arg3) => new Promise((resolve,reject) => {
            if(arg3.username == "catch test"){
                reject("exception");
            }
            resolve(
                {
                    data : {
                        success : false,
                        status : "join error"
                    }
                });
        }));
        response = await request(app)
            .post("/activities/joinupdate")
            .type('json')
            .send({
                username : "user",
                aid : "blahblah"
            });
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Activity does not exist.");

        response = await request(app)
            .post("/activities/joinupdate")
            .type('json')
            .send({
                username : "user",
                aid : constants.a1.aid
            });
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("join error");

        response = await request(app)
            .post("/activities/joinupdate")
            .type('json')
            .send({
                username : "catch test",
                aid : constants.a1.aid
            });
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("ERROR: exception");

        response = await request(app)
            .post("/activities/joinupdate")
            .type('json')
            .send({
                aid : "blahblah"
            });
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");
    });

    test("leave update test", async () => {
        sharedfuncs.axiosPostRequest = jest.fn((arg1,arg2,arg3) => new Promise((resolve,reject) => {
            if(arg3.username == "catch test"){
                reject("exception");
            }
            resolve(
                {
                    data : {
                        success : false,
                        status : "leave error"
                    }
                });
        }));
        response = await request(app)
            .post("/activities/leaveupdate")
            .type('json')
            .send({
                username : "user",
                aid : "blahblah"
            });
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Activity does not exist.");

        response = await request(app)
            .post("/activities/leaveupdate")
            .type('json')
            .send({
                username : "user",
                aid : constants.a1.aid
            });
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("leave error");

        response = await request(app)
            .post("/activities/leaveupdate")
            .type('json')
            .send({
                username : "catch test",
                aid : constants.a1.aid
            });
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("ERROR: exception");

        response = await request(app)
            .post("/activities/leaveupdate")
            .type('json')
            .send({
                aid : "blahblah"
            });
        expect(response.body.success).toBe(false)
        expect(response.body.status).toBe("Not well formed request.");
    });

    afterAll(async () => {
        await Activity.deleteMany({});
        await Activity.insertMany(before2);
        await models.disconnectDb()
    });
});