// Activities
const a1 = {
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

const a2 = {
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

const a3 = {
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

const a4 = {
    aid : "4",
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

const a5 = {
    aid : "5",
    name : "best",
    leader : "test",
    usernames : ["test","aplha"],
    info : "Understanding Javascript with TA",
    major : "CPEN",
    course : ["CPEN321", "CPEN331", "CPEN400"],
    school : "UBC",
    lat : "1",
    long : "-2",
    status : "1"
}

// badly formed activities

const bad_a1 = {
    name : "best",
    leader : "test",
    usernames : ["test","aplha"],
    info : "Understanding Javascript with TA",
    major : "CPEN",
    course : ["CPEN321", "CPEN331", "CPEN400"],
    school : "UBC",
    lat : "1",
    long : "-2",
    status : "1"
}

const bad_a2 = {
    aid : "5",
    leader : "test",
    usernames : ["test","aplha"],
    info : "Understanding Javascript with TA",
    major : "CPEN",
    course : ["CPEN321", "CPEN331", "CPEN400"],
    school : "UBC",
    lat : "1",
    long : "-2",
    status : "1"
}

const bad_a3 = {
    aid : "5",
    name : "best",
    usernames : ["test","aplha"],
    info : "Understanding Javascript with TA",
    major : "CPEN",
    course : ["CPEN321", "CPEN331", "CPEN400"],
    school : "UBC",
    lat : "1",
    long : "-2",
    status : "1"
}

const bad_a4 = {
    aid : "5",
    name : "best",
    leader : "test",
    info : "Understanding Javascript with TA",
    major : "CPEN",
    course : ["CPEN321", "CPEN331", "CPEN400"],
    school : "UBC",
    lat : "1",
    long : "-2",
    status : "1"
}

const bad_a5 = {
    aid : "5",
    name : "best",
    leader : "test",
    usernames : ["test","aplha"],
    major : "CPEN",
    course : ["CPEN321", "CPEN331", "CPEN400"],
    school : "UBC",
    lat : "1",
    long : "-2",
    status : "1"
}

const bad_a6 = {
    aid : "5",
    name : "best",
    leader : "test",
    usernames : ["test","aplha"],
    info : "Understanding Javascript with TA",
    course : ["CPEN321", "CPEN331", "CPEN400"],
    school : "UBC",
    lat : "1",
    long : "-2",
    status : "1"
}

const bad_a7 = {
    aid : "5",
    name : "best",
    leader : "test",
    usernames : ["test","aplha"],
    info : "Understanding Javascript with TA",
    major : "CPEN",
    school : "UBC",
    lat : "1",
    long : "-2",
    status : "1"
}

const bad_a8 = {
    aid : "5",
    name : "best",
    leader : "test",
    usernames : ["test","aplha"],
    info : "Understanding Javascript with TA",
    major : "CPEN",
    course : ["CPEN321", "CPEN331", "CPEN400"],
    lat : "1",
    long : "-2",
    status : "1"
}

const bad_a9 = {
    aid : "5",
    name : "best",
    leader : "test",
    usernames : ["test","aplha"],
    info : "Understanding Javascript with TA",
    major : "CPEN",
    course : ["CPEN321", "CPEN331", "CPEN400"],
    school : "UBC",
    long : "-2",
    status : "1"
}

const bad_a10 = {
    aid : "5",
    name : "best",
    leader : "test",
    usernames : ["test","aplha"],
    info : "Understanding Javascript with TA",
    major : "CPEN",
    course : ["CPEN321", "CPEN331", "CPEN400"],
    school : "UBC",
    lat : "1",
    status : "1"
}

const bad_a11 = {
    aid : "5",
    name : "best",
    leader : "test",
    usernames : ["test","aplha"],
    info : "Understanding Javascript with TA",
    major : "CPEN",
    course : ["CPEN321", "CPEN331", "CPEN400"],
    school : "UBC",
    lat : "1",
    long : "-2",
}

bad_activities = [bad_a1,bad_a2,bad_a3,bad_a4,bad_a5,bad_a6,bad_a7,bad_a8,
                bad_a9,bad_a10,bad_a11];

// badly formed profiles

const bad_p1 = {
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

const bad_p2 = {
    name : "Kyle",
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

const bad_p3 = {
    name : "Kyle",
    username : "kyle",
    CourseRegistered : [
        "CPEN331", "CPEN321"
    ],
    school : "UBC",
    phone : "4444444444",
    private : false,
    inActivity : false,
    activityID : "-1"
}

const bad_p4 = {
    name : "Kyle",
    username : "kyle",
    major : "CPEN",
    school : "UBC",
    phone : "4444444444",
    private : false,
    inActivity : false,
    activityID : "-1"
}

const bad_p5 = {
    name : "Kyle",
    username : "kyle",
    major : "CPEN",
    CourseRegistered : [
        "CPEN331", "CPEN321"
    ],
    phone : "4444444444",
    private : false,
    inActivity : false,
    activityID : "-1"
}

const bad_p6 = {
    name : "Kyle",
    username : "kyle",
    major : "CPEN",
    CourseRegistered : [
        "CPEN331", "CPEN321"
    ],
    school : "UBC",
    private : false,
    inActivity : false,
    activityID : "-1"
}

const bad_p7 = {
    name : "Kyle",
    username : "kyle",
    major : "CPEN",
    CourseRegistered : [
        "CPEN331", "CPEN321"
    ],
    school : "UBC",
    phone : "4444444444",
    inActivity : false,
    activityID : "-1"
}

const bad_p8 = {
    name : "Kyle",
    username : "kyle",
    major : "CPEN",
    CourseRegistered : [
        "CPEN331", "CPEN321"
    ],
    school : "UBC",
    phone : "4444444444",
    private : false,
    activityID : "-1"
}

const bad_p9 = {
    name : "Kyle",
    username : "kyle",
    major : "CPEN",
    CourseRegistered : [
        "CPEN331", "CPEN321"
    ],
    school : "UBC",
    phone : "4444444444",
    private : false,
    inActivity : false
}

bad_profiles = [bad_p1,bad_p2,bad_p3,bad_p4,bad_p5,bad_p6,
            bad_p7,bad_p8,bad_p9];


// profiles
const kyle_p = {
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

const kyle_inact_p = {
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
    activityID : "2"
}

const noact_p1 = {
    name : "Kyle",
    username : "test1",
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


module.exports = {
    a1,
    a2,
    a3,
    a4,
    a5,
    
    bad_activities,
    bad_profiles,

    kyle_p,
    kyle_inact_p,
    noact_p1
}