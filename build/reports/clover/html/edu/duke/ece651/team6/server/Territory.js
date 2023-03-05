var clover = new Object();

// JSON: {classes : [{name, id, sl, el,  methods : [{sl, el}, ...]}, ...]}
clover.pageData = {"classes":[{"el":56,"id":36,"methods":[{"el":16,"sc":5,"sl":12},{"el":31,"sc":5,"sl":24},{"el":39,"sc":5,"sl":37},{"el":47,"sc":5,"sl":45},{"el":55,"sc":5,"sl":53}],"name":"Territory","sl":3}]}

// JSON: {test_ID : {"methods": [ID1, ID2, ID3...], "name" : "testXXX() void"}, ...};
clover.testTargets = {"test_1":{"methods":[{"sl":24},{"sl":37}],"name":"testBasicFunc","pass":true,"statements":[{"sl":25},{"sl":26},{"sl":29},{"sl":30},{"sl":38}]},"test_3":{"methods":[{"sl":12},{"sl":24},{"sl":37},{"sl":45},{"sl":53}],"name":"testBasicFunc","pass":true,"statements":[{"sl":13},{"sl":14},{"sl":15},{"sl":25},{"sl":26},{"sl":29},{"sl":30},{"sl":38},{"sl":46},{"sl":54}]},"test_4":{"methods":[{"sl":24},{"sl":37},{"sl":45}],"name":"testCreatePlayerMapInfo","pass":true,"statements":[{"sl":25},{"sl":26},{"sl":29},{"sl":30},{"sl":38},{"sl":46}]}}

// JSON: { lines : [{tests : [testid1, testid2, testid3, ...]}, ...]};
clover.srcFileLines = [[], [], [], [], [], [], [], [], [], [], [], [], [3], [3], [3], [3], [], [], [], [], [], [], [], [], [1, 4, 3], [1, 4, 3], [1, 4, 3], [], [], [1, 4, 3], [1, 4, 3], [], [], [], [], [], [], [1, 4, 3], [1, 4, 3], [], [], [], [], [], [], [4, 3], [4, 3], [], [], [], [], [], [], [3], [3], [], []]
