import { showLoadingSpinner, hideLoadingSpinner } from "../common/loading-spinner.js";
import { initializeFilters, addFilterEventListeners, addResetButtonEventListener, addSearchButtonEventListener } from './filter.js';
import { addPageSizeChangeEventListener, addCopyButtonEventListener, performSearch } from './table.js';

const url = {
    filter: "/api/filter?surveyName=한국아동·청소년패널조사",
    table: "/api/korKidAoePnlInfo",
    publicApiBaseUrl: "https://data.nypi.re.kr/openapi/service/api/KorKidAoePnlInfo",
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
        { key: 'id', className: 'text-right' },
        { key: 'cohortNm' },
        { key: 'srvyYr' },
        { key: 'srvyExmnCycl' },
        { key: 'rspnsMnbdNm' },
        { key: 'otptCtgryNm' },
        { key: 'svbnVrblCn', className: 'text-left' },
        { key: 'svbnClsfCd', className: 'text-left' },
        { key: 'srvyQitemId' },
        { key: 'cbookQitemCn' },
        { key: 'rspvl', className: 'text-right' },
        { key: 'rspnsNm', className: 'text-left' },
        { key: 'aiCrtYn' },
        { key: 'caseCnt', className: 'text-right' },
        { key: 'freqRt', className: 'text-right' }
    ]
};

showLoadingSpinner();
initializeFilters(pageFilterConfig, url).then(() => {
    addFilterEventListeners();
    addResetButtonEventListener();
    addSearchButtonEventListener(performSearch, pageFilterConfig, tableConfig, url);
    hideLoadingSpinner();
});

addPageSizeChangeEventListener(performSearch, tableConfig, url);
addCopyButtonEventListener();
