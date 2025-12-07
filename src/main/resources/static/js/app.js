const API_BASE_URL = '/holidays';

// 국가 목록 로드
async function loadCountries() {
    try {
        const response = await fetch('/countries');
        const data = await response.json();

        if (response.ok && data.data && data.data.countries) {
            const countries = data.data.countries;
            const selects = ['searchCountry', 'refreshCountry', 'deleteCountry'];

            selects.forEach(selectId => {
                const select = document.getElementById(selectId);
                countries.forEach(country => {
                    const option = document.createElement('option');
                    option.value = country.countryCode;
                    option.textContent = `${country.name} (${country.countryCode})`;
                    select.appendChild(option);
                });
            });

            document.getElementById('countryCount').textContent = countries.length;
            document.getElementById('searchCountry').value = 'KR';
        }
    } catch (error) {
        console.error('국가 목록 로드 실패:', error);
        document.getElementById('countryCount').textContent = '로드 실패';
    }
}

// 조회 필터 설정 후 검색 수행
function searchWithFilter(year, countryCode) {
    document.getElementById('searchYear').value = year;
    document.getElementById('searchCountry').value = countryCode;
    document.getElementById('searchFromDate').value = '';
    document.getElementById('searchToDate').value = '';
    document.getElementById('searchPage').value = 1;
    searchHolidays();
}

// 새 검색 (페이지 초기화)
function resetAndSearch() {
    document.getElementById('searchPage').value = 1;
    searchHolidays();
}

// 공휴일 조회
async function searchHolidays() {
    const year = document.getElementById('searchYear').value;
    const countryCode = document.getElementById('searchCountry').value.trim().toUpperCase();
    const fromDate = document.getElementById('searchFromDate').value;
    const toDate = document.getElementById('searchToDate').value;
    const page = document.getElementById('searchPage').value;
    const size = document.getElementById('searchSize').value;

    const params = new URLSearchParams();
    if (year) params.append('year', year);
    if (countryCode) params.append('countryCode', countryCode);
    if (fromDate) params.append('fromDate', fromDate);
    if (toDate) params.append('toDate', toDate);
    if (page) params.append('page', parseInt(page) - 1);
    if (size) params.append('size', size);

    const resultsDiv = document.getElementById('results');
    resultsDiv.innerHTML = '<div class="loading">검색 중...</div>';

    try {
        const response = await fetch(`${API_BASE_URL}?${params.toString()}`);
        const data = await response.json();

        if (response.ok) {
            displayResults(data.data);
        } else {
            resultsDiv.innerHTML = `<div class="error">오류: ${data.message || '조회 실패'}</div>`;
        }
    } catch (error) {
        resultsDiv.innerHTML = `<div class="error">네트워크 오류: ${error.message}</div>`;
    }
}

// 검색 결과 표시
function displayResults(pageData) {
    const resultsDiv = document.getElementById('results');

    if (!pageData || !pageData.content || pageData.content.length === 0) {
        resultsDiv.innerHTML = '<div class="info-box">검색 결과가 없습니다.</div>';
        return;
    }

    let html = `<div style="margin-bottom: 15px; font-weight: 600;">
        총 ${pageData.totalElements}개 (${pageData.page + 1}/${pageData.totalPages} 페이지)
    </div>`;

    pageData.content.forEach(holiday => {
        const types = holiday.types ? holiday.types.map(t => `<span class="badge">${t}</span>`).join('') : '';
        const counties = holiday.counties && holiday.counties.length > 0
            ? `지역: ${holiday.counties.join(', ')}`
            : '전국';

        html += `
            <div class="holiday-item">
                <div class="holiday-date">${holiday.date}</div>
                <div class="holiday-name">${holiday.name} (${holiday.localName})</div>
                <div class="holiday-details">
                    ${types}<br>
                    국가: ${holiday.countryCode} | ${counties} |
                    ${holiday.global ? '글로벌' : '로컬'} |
                    시행년도: ${holiday.launchYear || 'N/A'}
                </div>
            </div>
        `;
    });

    // Pagination
    if (pageData.totalPages > 1) {
        html += '<div class="pagination">';
        if (pageData.page > 0) {
            html += `<button onclick="goToPage(${pageData.page - 1})">이전</button>`;
        }
        html += `<button disabled>페이지 ${pageData.page + 1}</button>`;
        if (pageData.page < pageData.totalPages - 1) {
            html += `<button onclick="goToPage(${pageData.page + 1})">다음</button>`;
        }
        html += '</div>';
    }

    resultsDiv.innerHTML = html;
}

// 페이지 이동
function goToPage(page) {
    document.getElementById('searchPage').value = page + 1;
    searchHolidays();
}

// 공휴일 재동기화
async function refreshHolidays() {
    const year = document.getElementById('refreshYear').value;
    const countryCode = document.getElementById('refreshCountry').value.trim().toUpperCase();

    if (!year || !countryCode) {
        alert('연도와 국가 코드를 모두 입력해주세요.');
        return;
    }

    const resultsDiv = document.getElementById('results');
    resultsDiv.innerHTML = '<div class="loading">재동기화 중...</div>';

    try {
        const response = await fetch(`${API_BASE_URL}/refresh`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                year: parseInt(year),
                countryCode: countryCode
            })
        });

        const data = await response.json();

        if (response.ok) {
            resultsDiv.innerHTML = `<div class="success">재동기화 완료! (${year}년 ${countryCode})</div>`;
            searchWithFilter(year, countryCode);
        } else {
            resultsDiv.innerHTML = `<div class="error">오류: ${data.message || '재동기화 실패'}</div>`;
        }
    } catch (error) {
        resultsDiv.innerHTML = `<div class="error">네트워크 오류: ${error.message}</div>`;
    }
}

// 공휴일 삭제
async function deleteHolidays() {
    const year = document.getElementById('deleteYear').value;
    const countryCode = document.getElementById('deleteCountry').value.trim().toUpperCase();

    if (!year || !countryCode) {
        alert('연도와 국가 코드를 모두 입력해주세요.');
        return;
    }

    if (!confirm(`정말로 ${year}년 ${countryCode}의 모든 공휴일을 삭제하시겠습니까?`)) {
        return;
    }

    const resultsDiv = document.getElementById('results');
    resultsDiv.innerHTML = '<div class="loading">삭제 중...</div>';

    try {
        const response = await fetch(`${API_BASE_URL}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                year: parseInt(year),
                countryCode: countryCode
            })
        });

        if (response.status === 204 || response.ok) {
            resultsDiv.innerHTML = `<div class="success">삭제 완료! (${year}년 ${countryCode})</div>`;
            searchWithFilter(year, countryCode);
        } else {
            const data = await response.json();
            resultsDiv.innerHTML = `<div class="error">오류: ${data.message || '삭제 실패'}</div>`;
        }
    } catch (error) {
        resultsDiv.innerHTML = `<div class="error">네트워크 오류: ${error.message}</div>`;
    }
}

// 페이지 로드 시 초기화
window.addEventListener('DOMContentLoaded', async () => {
    const currentYear = new Date().getFullYear();
    document.getElementById('searchYear').value = currentYear;

    await loadCountries();
    searchHolidays();
});
