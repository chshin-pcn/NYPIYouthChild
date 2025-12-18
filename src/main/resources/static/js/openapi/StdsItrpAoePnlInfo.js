import { showLoadingSpinner, hideLoadingSpinner } from "../common/loading-spinner.js";
import { initializeFilters, addFilterEventListeners, addResetButtonEventListener, addSearchButtonEventListener } from './filter.js';
import { addPageSizeChangeEventListener, addCopyButtonEventListener, performSearch } from './table.js';

const url = {
    filter: "/api/filter?surveyName=학업중단패널조사",
    table: "/api/stdsItrpAoePnlInfo",
    publicApiBaseUrl: "https://apis.data.go.kr/B551923/apiStdsItrpAoePnlInfo/StdsItrpAoePnlInfo",
}

const pageFilterConfig = {
    selects: [
        { key: "year", elementId: "year", data: "yearData", paramName: "srvyYr" },
        { key: "categoryMajor", elementId: "category-major", data: "categoryMajorData", paramName: "large" },
        { key: "categoryMedium", elementId: "category-medium", data: "categoryMediumData", paramName: "mid" },
        { key: "categoryMinor", elementId: "category-minor", data: "categoryMinorData", paramName: "small" },
        { key: "categoryDetailed", elementId: "category-detailed", data: "categoryDetailedData", paramName: "detail" },
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
        { key: 'srvyYr' },
        { key: 'srvyExmnCycl' },
        { key: 'otptCtgryNm', className: 'text-left' },
        { key: 'svbnVrblCn', className: 'text-left' },
        { key: 'svbnClsfCd', className: 'text-left' },
        { key: 'srvyQitemId', className: 'text-left' },
        { key: 'cbookQitemCn', className: 'text-left' },
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
