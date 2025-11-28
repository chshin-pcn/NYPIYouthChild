import { initializeFilters, addFilterEventListeners, addResetButtonEventListener, addSearchButtonEventListener } from './filter.js';
import { addPageSizeChangeEventListener, addCopyButtonEventListener, performSearch } from './table.js';

const url = {
    filter: "/api/filter?surveyName=다문화청소년패널조사",
    table: "/api/mcltAoePnlInfo",
    publicApiBaseUrl: "https://data.nypi.re.kr/openapi/service/api/McltAoePnlInfo",
}

const pageFilterConfig = {
    selects: [
        { key: "year", elementId: "year", data: "yearData", paramName: "srvyYr" },
        { key: "respondent", elementId: "respondent", data: "respondentData", paramName: "rspnsMnbdNm" },
        { key: "categoryMajor", elementId: "category-major", data: "categoryMajorData" },
        { key: "categoryMedium", elementId: "category-medium", data: "categoryMediumData" },
        { key: "categoryMinor", elementId: "category-minor", data: "categoryMinorData" },
        { key: "categoryDetailed", elementId: "category-detailed", data: "categoryDetailedData" },
        { key: "questionId", elementId: "question", data: "questionData", paramName: "srvyQitemId" }
    ],
    bannerVariables: [
        { elementId: 'gender', paramName: 'svbnClsfCd01', paramValue: 'BANN020200' },
        { elementId: 'age', paramName: 'svbnClsfCd02', paramValue: 'BANN020300' },
        { elementId: 'region', paramName: 'svbnClsfCd03', paramValue: 'BANN020400' }
    ]
};

const tableConfig = {
    columns: [
        { key: 'id' },
        { key: 'ornuNm' },
        { key: 'srvyYr' },
        { key: 'srvyExmnCycl' },
        { key: 'rspnsMnbdNm' },
        { key: 'otptCtgryNm' },
        { key: 'svbnVrblCn' },
        { key: 'svbnClsfCd' },
        { key: 'srvyQitemId' },
        { key: 'cbookQitemCn' },
        { key: 'rspvl' },
        { key: 'rspnsNm' },
        { key: 'aiCrtYn' },
        { key: 'caseCnt' },
        { key: 'freqRt' }
    ]
};

initializeFilters(pageFilterConfig, url).then(() => {
    addFilterEventListeners();
    addResetButtonEventListener();
    addSearchButtonEventListener(performSearch, pageFilterConfig, tableConfig, url);
});

addPageSizeChangeEventListener(performSearch, tableConfig, url);
addCopyButtonEventListener();
