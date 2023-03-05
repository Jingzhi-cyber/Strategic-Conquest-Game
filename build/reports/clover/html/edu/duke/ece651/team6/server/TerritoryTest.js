var clover = new Object();

// JSON: {classes : [{name, id, sl, el,  methods : [{sl, el}, ...]}, ...]}
clover.pageData = {"classes":[{"el":20,"id":110,"methods":[{"el":19,"sc":5,"sl":8}],"name":"TerritoryTest","sl":7}]}

// JSON: {test_ID : {"methods": [ID1, ID2, ID3...], "name" : "testXXX() void"}, ...};
clover.testTargets = {"test_3":{"methods":[{"sl":8}],"name":"testBasicFunc","pass":true,"statements":[{"sl":10},{"sl":11},{"sl":12},{"sl":13},{"sl":15},{"sl":16},{"sl":17},{"sl":18}]}}

// JSON: { lines : [{tests : [testid1, testid2, testid3, ...]}, ...]};
clover.srcFileLines = [[], [], [], [], [], [], [], [], [3], [], [3], [3], [3], [3], [], [3], [3], [3], [3], [], []]
