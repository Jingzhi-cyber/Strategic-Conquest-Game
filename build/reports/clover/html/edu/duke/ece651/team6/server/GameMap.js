var clover = new Object();

// JSON: {classes : [{name, id, sl, el,  methods : [{sl, el}, ...]}, ...]}
clover.pageData = {"classes":[{"el":44,"id":25,"methods":[{"el":20,"sc":5,"sl":18},{"el":28,"sc":5,"sl":26},{"el":42,"sc":5,"sl":35}],"name":"GameMap","sl":7}]}

// JSON: {test_ID : {"methods": [ID1, ID2, ID3...], "name" : "testXXX() void"}, ...};
clover.testTargets = {"test_1":{"methods":[{"sl":18},{"sl":26},{"sl":35}],"name":"testBasicFunc","pass":true,"statements":[{"sl":19},{"sl":27},{"sl":36},{"sl":37},{"sl":38},{"sl":41}]},"test_4":{"methods":[{"sl":18},{"sl":26},{"sl":35}],"name":"testCreatePlayerMapInfo","pass":true,"statements":[{"sl":19},{"sl":27},{"sl":36},{"sl":37},{"sl":38}]}}

// JSON: { lines : [{tests : [testid1, testid2, testid3, ...]}, ...]};
clover.srcFileLines = [[], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [1, 4], [1, 4], [], [], [], [], [], [], [1, 4], [1, 4], [], [], [], [], [], [], [], [1, 4], [1, 4], [1, 4], [1, 4], [], [], [1], [], [], []]
