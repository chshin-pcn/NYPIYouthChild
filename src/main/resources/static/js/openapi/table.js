import { showLoadingSpinner, hideLoadingSpinner } from "../common/loading-spinner.js";

/**
 * 현재 선택된 검색 파라미터를 저장하는 객체입니다.
 * @type {object}
 */
let selectedParams = {};

/**
 * 지정된 URL과 쿼리 문자열을 사용하여 테이블 데이터를 비동기적으로 가져옵니다.
 * @param {string} url - 데이터를 가져올 API 엔드포인트 URL.
 * @param {string} queryString - URL에 추가할 쿼리 문자열.
 * @returns {Promise<Object>} - 테이블 데이터를 포함하는 JSON 객체를 반환하는 Promise.
 * @throws {Error} - 데이터 조회 실패 시 오류를 발생시킵니다.
 */
export async function fetchTable(url, queryString) {
    showLoadingSpinner();
    const response = await fetch(`${url}?${queryString}`);
    if (!response.ok) {
        hideLoadingSpinner();
        throw new Error("테이블 데이터 조회에 실패했습니다.\n잠시 후 다시 시도해주세요.");
    }
    hideLoadingSpinner();
    return response.json();
}

/**
 * 숫자에 1,000 단위 콤마를 추가합니다.
 * @param {string|number} numStr - 콤마를 추가할 숫자 또는 숫자 문자열.
 * @returns {string} - 콤마가 추가된 문자열 또는 빈 문자열.
 */
function addCommas(numStr) {
    if (!numStr) return '';
    return numStr.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

/**
 * 간단한 <td> 엘리먼트를 생성합니다.
 * @param {string} text - 셀에 들어갈 텍스트
 * @param {string} [className] - 셀에 추가할 CSS 클래스 이름 (선택 사항).
 * @returns {HTMLTableCellElement} - 생성된 <td> 엘리먼트.
 */
function createCell(text, className) {
    const cell = document.createElement('td');
    cell.textContent = text || ''; // 텍스트가 없으면 빈 문자열 할당
    if (className) {
        cell.classList.add(className);
    }
    return cell;
}

/**
 * 설정과 데이터 아이템을 기반으로 <tr> 엘리먼트를 생성합니다.
 * @param {object} item - 테이블에 표시할 데이터 아이템 객체.
 * @param {object} config - 테이블 설정 객체. 컬럼 정보를 포함합니다.
 * @param {boolean} showAiCol - AI 컬럼의 표시 여부. true이면 표시, false이면 숨김.
 * @returns {HTMLTableRowElement} - 생성된 <tr> 엘리먼트.
 */
function buildRow(item, config, showAiCol) {
    const row = document.createElement('tr');
    config.columns.forEach(column => {
        // AI 컬럼을 표시하지 않아야 하는 경우 해당 컬럼은 건너뜁니다.
        if (column.key === 'aiCrtYn' && !showAiCol) return;

        // 'caseCnt' 컬럼인 경우 콤마를 추가하여 표시합니다.
        if (column.key === 'caseCnt') {
            row.appendChild(createCell(addCommas(item[column.key]), column.className));
        } else {
            row.appendChild(createCell(item[column.key], column.className));
        }
    });
    return row;
}

/**
 * 설정(config)과 데이터 배열을 기반으로 테이블 전체를 렌더링합니다.
 * @param {Array<object>|null} items - 테이블에 표시할 데이터 아이템 배열. null 또는 빈 배열일 경우 "검색 결과가 없습니다" 메시지를 표시합니다.
 * @param {object} config - 테이블 설정 객체. 컬럼 정보를 포함합니다.
 */
function renderTable(items, config) {
    const tableBody = document.querySelector("#results-table tbody");
    if (!tableBody) return;

    // 테이블 Body 비우기
    while (tableBody.firstChild) {
        tableBody.removeChild(tableBody.firstChild);
    }

    // AI 컬럼 표시 여부 판단 및 헤더 제어
    // 아이템 중 하나라도 "aiCrtYn"이 'Y'인 경우 AI 컬럼을 표시합니다.
    const showAiCol = items && items.some(item => item["aiCrtYn"] === 'Y');
    const aiCrtTh = document.querySelector(".aiCrtYn");
    if (aiCrtTh) {
        aiCrtTh.style.display = showAiCol ? "table-cell" : "none"; // AI 컬럼 헤더 표시/숨김
    }
    
    // AI 컬럼을 표시하지 않을 경우 colSpan을 1 감소시킵니다.
    const finalColSpan = showAiCol ? config.columns.length : config.columns.length - 1;

    // 검색 결과가 없는 경우 처리
    if (!items || items.length === 0) {
        const noResultsRow = document.createElement('tr');
        const noResultsCell = document.createElement('td');
        noResultsCell.colSpan = finalColSpan; // 컬럼 병합
        noResultsCell.style.textAlign = 'center'; // 중앙 정렬
        noResultsCell.textContent = '검색 결과가 없습니다.'; // 메시지
        noResultsRow.appendChild(noResultsCell);
        tableBody.appendChild(noResultsRow);
        return;
    }

    // 각 데이터 아이템에 대해 행을 생성하고 테이블에 추가
    items.forEach(item => {
        const row = buildRow(item, config, showAiCol); // 행 생성
        tableBody.appendChild(row); // 테이블에 행 추가
    });
}

/**
 * 검색 결과 총 건수를 화면에 업데이트합니다.
 * @param {number} totalCount - 전체 결과 수.
 */
export function updateTotalCount(totalCount) {
    const countElement = document.getElementById('total-count');
    if (countElement) {
        countElement.textContent = addCommas(totalCount) || 0; // 총 건수 업데이트
    }
}

/**
 * 페이지네이션 버튼을 생성하는 헬퍼 함수.
 * @param {string|number} text - 버튼에 표시될 텍스트.
 * @param {Array<string>} classNames - 버튼에 추가할 CSS 클래스 배열.
 * @param {boolean} disabled - 버튼 비활성화 여부.
 * @param {number|null} dataPage - 버튼 클릭 시 이동할 페이지 번호 (data-page 속성).
 * @returns {HTMLButtonElement} - 생성된 버튼 엘리먼트.
 */
function createButton(text, classNames = [], disabled = false, dataPage = null) {
    const button = document.createElement("button");
    button.classList.add("page-btn", ...classNames); // 기본 클래스 및 추가 클래스 적용
    if (disabled) button.disabled = true; // 비활성화 설정

    const anchor = document.createElement("a");
    anchor.textContent = text; // 텍스트 설정
    button.appendChild(anchor); // 앵커 태그를 버튼에 추가

    if (dataPage) button.dataset.page = dataPage; // data-page 속성 설정

    return button;
};

/**
 * 페이지네이션 UI를 생성하고 페이지 버튼에 이벤트 리스너를 추가합니다.
 * @param {number} totalCount - 전체 데이터 항목 수.
 * @param {number} numOfRows - 페이지 당 표시할 행 수.
 * @param {number} pageNo - 현재 페이지 번호.
 * @param {Function} performSearch - 페이지 변경 시 검색을 수행할 콜백 함수.
 * @param {object} tableConfig - 테이블 설정 객체.
 * @param {string} url - API URL 정보 객체.
 */
function displayPagination(totalCount, numOfRows, pageNo, performSearch, tableConfig, url) {
    const totalPages = Math.ceil(totalCount / numOfRows); // 전체 페이지 수 계산
    const paginationContainer = document.querySelector(".pagination"); // 페이지네이션 컨테이너 요소
    if (!paginationContainer) return;
    while (paginationContainer.firstChild) {
        paginationContainer.removeChild(paginationContainer.firstChild);
    }

    if (totalPages === 0) return; // 페이지가 없으면 아무것도 표시하지 않음

    const pageGroupSize = 10; // 페이지 그룹 크기
    // 현재 페이지 그룹의 시작 페이지 번호 계산
    const startPage = Math.floor((pageNo - 1) / pageGroupSize) * pageGroupSize + 1;
    // 현재 페이지 그룹의 끝 페이지 번호 계산
    const endPage = Math.min(startPage + pageGroupSize - 1, totalPages);

    // "처음" 버튼 생성 및 추가
    const firstButton = createButton("처음", ["arrow", "first"], pageNo === 1);
    paginationContainer.appendChild(firstButton);

    // "이전" 버튼 생성 및 추가
    const prevButton = createButton("이전", ["arrow", "prev"], pageNo === 1);
    paginationContainer.appendChild(prevButton);

    // 페이지 번호 버튼 생성 및 추가
    for (let i = startPage; i <= endPage; i++) {
        const pageButton = createButton(i, [], false, i);
        if (i === pageNo) pageButton.classList.add("active"); // 현재 페이지에 'active' 클래스 추가
        paginationContainer.appendChild(pageButton);
    }

    // "다음" 버튼 생성 및 추가
    const nextButton = createButton("다음", ["arrow", "next"], pageNo === totalPages);
    paginationContainer.appendChild(nextButton);

    // "마지막" 버튼 생성 및 추가
    const lastButton = createButton("마지막", ["arrow", "last"], pageNo === totalPages);
    paginationContainer.appendChild(lastButton);

    // 페이지 버튼 클릭 이벤트 리스너 추가
    const buttons = paginationContainer.querySelectorAll(".page-btn");
    buttons.forEach(button => {
        button.addEventListener("click", () => {
            const clickedPage = parseInt(button.dataset.page, 10);
            if (!isNaN(clickedPage)) {
                pageNo = clickedPage; // 숫자 페이지 버튼 클릭 시
            } else if (button.classList.contains("first")) {
                pageNo = 1; // "처음" 버튼 클릭 시
            } else if (button.classList.contains("prev")) {
                pageNo = Math.max(1, pageNo - pageGroupSize); // "이전" 버튼 클릭 시
            } else if (button.classList.contains("next")) {
                pageNo = Math.min(totalPages, pageNo + pageGroupSize); // "다음" 버튼 클릭 시
            } else if (button.classList.contains("last")) {
                pageNo = totalPages; // "마지막" 버튼 클릭 시
            }
            numOfRows = document.getElementById('page-size-select').value; // 페이지 당 행 수 다시 가져오기
            performSearch(selectedParams, pageNo, numOfRows, tableConfig, url); // 검색 수행
        });
    });
}


/**
 * 페이지 당 결과 수 변경 select 요소에 이벤트 리스너를 추가합니다.
 * 값이 변경되면 새로운 페이지 크기로 검색을 다시 수행합니다.
 * @param {Function} performSearch - 검색을 실제로 수행하는 함수.
 * @param {object} tableConfig - 테이블 설정 객체.
 * @param {object} url - API URL 정보 객체.
 * @returns {void}
 * @export
 */
export function addPageSizeChangeEventListener(performSearch, tableConfig, url) {
    const pageSizeSelect = document.getElementById('page-size-select');
    if (!pageSizeSelect) return;

    pageSizeSelect.addEventListener('change', () => {
        // selectedParams가 비어있지 않은 경우에만, 즉 첫 검색이 이루어진 후에만 동작합니다.
        if (selectedParams && Object.keys(selectedParams).length > 0) {
            const numOfRows = pageSizeSelect.value; // 변경된 페이지 당 행 수
            performSearch(selectedParams, 1, numOfRows, tableConfig, url); // 첫 페이지부터 검색 다시 수행
        }
    });
}

/**
 * API 호출 URL을 화면에 표시합니다.
 * @param {string} publicApiBaseUrl - 공공 API의 기본 URL.
 * @param {string} queryString - API 호출에 사용된 쿼리 문자열.
 */
function displayPublicApiUrl(publicApiBaseUrl, queryString) {
    const publicApiUrl = `${publicApiBaseUrl}?_type=json&${queryString}`; // 최종 API URL 생성

    const apiUrlContainer = document.getElementById('api-url-container'); // URL 컨테이너
    const apiUrlDisplay = document.getElementById('api-url-display'); // URL을 표시할 input 요소

    if (apiUrlDisplay && apiUrlContainer) {
        apiUrlDisplay.value = publicApiUrl; // input 필드에 URL 설정
        apiUrlContainer.style.display = 'block'; // 컨테이너 표시
    }
}

/**
 * API URL 복사 버튼에 이벤트 리스너를 추가합니다.
 * 클릭 시 표시된 API URL을 클립보드에 복사하고, 사용자에게 피드백을 제공합니다.
 * @returns {void}
 * @export
 */
export function addCopyButtonEventListener() {
    const copyButton = document.getElementById('copy-btn'); // 복사 버튼
    const apiUrlDisplay = document.getElementById('api-url-display'); // API URL 표시 input
    const copyFeedback = document.getElementById('copy-feedback'); // 복사 피드백 메시지

    if (copyButton && apiUrlDisplay && copyFeedback) {
        copyButton.addEventListener('click', async () => {
            try {
                await navigator.clipboard.writeText(apiUrlDisplay.value); // 클립보드에 URL 복사
                copyFeedback.classList.add('show'); // 피드백 메시지 표시
                setTimeout(() => {
                    copyFeedback.classList.remove('show'); // 2초 후 피드백 메시지 숨김
                }, 2000);
            } catch (err) {
                console.error('복사 실패: ', err); // 복사 실패 시 에러 로깅
            }
        })
    }
}

/**
 * 검색 파라미터를 기반으로 데이터를 검색하고 테이블을 렌더링하며 페이지네이션을 업데이트합니다.
 * 검색 전에 필수 필드 유효성 검사를 수행합니다.
 * @param {object} searchParams - 검색에 사용될 파라미터 객체.
 * @param {number} [pageNo=1] - 요청할 페이지 번호. 기본값은 1.
 * @param {number} [numOfRows=10] - 페이지 당 표시할 행 수. 기본값은 10.
 * @param {object} tableConfig - 테이블 설정 객체.
 * @param {object} url - API URL 정보 객체.
 * @returns {Promise<void>}
 * @export
 */
export async function performSearch(searchParams, pageNo = 1, numOfRows = 10, tableConfig, url) {
    // 필터 요소들을 가져옵니다.
    const year = document.getElementById('year');

    // 필수 필드 유효성 검사
    if (year && !searchParams.srvyYr) {
        alert("연도가 선택되지 않았습니다.");
        year.focus();
        return;
    }

    // 쿼리 문자열 생성
    const queryString = new URLSearchParams({ ...searchParams, pageNo, numOfRows }).toString();
    try {
        // 테이블 데이터 가져오기
        const data = await fetchTable(url.table, queryString);
        const { items, totalCount, numOfRows, pageNo } = data; // 데이터와 총 건수 추출

        selectedParams = { ...searchParams }; // 현재 검색 파라미터 저장
        renderTable(items, tableConfig); // 테이블 렌더링
        updateTotalCount(totalCount); // 총 건수 업데이트
        displayPagination(totalCount, numOfRows, pageNo, performSearch, tableConfig, url); // 페이지네이션 표시
        displayPublicApiUrl(url.publicApiBaseUrl, queryString); // 공공 API URL 표시
    } catch (error) {
        console.error("검색 중 오류 발생:", error); // 에러 로깅
        renderTable(null, tableConfig); // 에러 발생 시 빈 테이블 렌더링
        updateTotalCount(0); // 총 건수 0으로 업데이트
    }
}
