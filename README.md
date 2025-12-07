# Holiday Keeper

> Nager.Date 무인증 API를 활용한 전 세계 공휴일 관리 서비스

## 📋 프로젝트 개요

외부 API 두 개만으로 최근 5년의 전 세계 공휴일 데이터를 저장·조회·관리하는 Mini Service입니다.

## 🛠 기술 스택

- **Language**: Java 21
- **Framework**: Spring Boot 3.4.12
- **ORM**: JPA (Hibernate)
- **Query**: QueryDSL 5
- **Database**: H2 (In-Memory)
- **Documentation**: Swagger/OpenAPI 3
- **Build Tool**: Gradle

## 🚀 빌드 & 실행 방법

### 1. 프로젝트 클론
```bash
git clone <repository-url>
cd holiday-keeper-service
```

### 2. 빌드
```bash
./gradlew clean build
```

### 3. 실행
```bash
./gradlew bootRun
```

애플리케이션이 시작되면 자동으로 최근 5년의 공휴일 데이터가 로딩됩니다.

### 4. 접속 URL
- **웹 UI**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:holidaydb`
  - Username: `sa`
  - Password: (empty)

## 📡 REST API 명세

### 1. 공휴일 조회
**GET** `/holidays`

연도, 국가 코드, 날짜 범위 등의 필터로 공휴일을 페이징 조회합니다.

**Query Parameters:**
| 파라미터 | 타입 | 필수 | 설명 | 예시 |
|---------|------|------|------|------|
| year | Integer | ❌ | 조회할 연도 | 2024 |
| countryCode | String | ❌ | 국가 코드 (ISO 3166-1 alpha-2) | KR |
| fromDate | LocalDate | ❌ | 조회 시작 날짜 | 2024-01-01 |
| toDate | LocalDate | ❌ | 조회 종료 날짜 | 2024-12-31 |
| page | Integer | ❌ | 페이지 번호 (0부터 시작) | 0 |
| size | Integer | ❌ | 페이지 크기 | 10 |

**Response Example:**
```json
{
  "code": 200,
  "status": "OK",
  "message": "OK",
  "data": {
    "content": [
      {
        "date": "2024-01-01",
        "localName": "설날",
        "name": "New Year's Day",
        "countryCode": "KR",
        "global": true,
        "types": ["PUBLIC"],
        "counties": [],
        "launchYear": 1949
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10
  }
}
```

### 2. 공휴일 재동기화 (Refresh)
**POST** `/holidays/refresh`

특정 연도·국가의 공휴일 데이터를 외부 API에서 다시 가져와 Upsert(덮어쓰기) 합니다.

**Request Body:**
```json
{
  "year": 2024,
  "countryCode": "KR"
}
```

**Response:**
```json
{
  "code": 200,
  "status": "OK",
  "message": "OK",
  "data": null
}
```

### 3. 공휴일 삭제
**DELETE** `/holidays`

특정 연도·국가의 모든 공휴일 데이터를 삭제합니다.

**Request Body:**
```json
{
  "year": 2024,
  "countryCode": "KR"
}
```

**Response:**
```json
{
  "code": 204,
  "status": "NO_CONTENT",
  "message": "NO_CONTENT",
  "data": null
}
```

### 4. 지원 국가 조회
**GET** `/countries`

지원하는 모든 국가 목록을 조회합니다.

**Response Example:**
```json
{
  "code": 200,
  "status": "OK",
  "message": "OK",
  "data": {
    "countries": [
      { "countryCode": "KR", "name": "South Korea" },
      { "countryCode": "US", "name": "United States" },
      { "countryCode": "JP", "name": "Japan" }
    ]
  }
}
```

## 🗄 데이터베이스 설계

### ERD
```
Country (국가)
├─ id (PK)
├─ code (Unique) - 국가 코드
└─ name - 국가명

Holiday (공휴일)
├─ id (PK)
├─ country_id (FK → Country)
├─ date - 날짜
├─ local_name - 현지 명칭
├─ name - 공휴일 명
├─ global - 글로벌 여부
└─ launch_year - 출시 년도

HolidayTypeMap (공휴일-타입 매핑)
├─ id (PK)
├─ holiday_id (FK → Holiday)
└─ type (Enum) - PUBLIC, BANK, SCHOOL, AUTHORITIES, OPTIONAL, OBSERVANCE

HolidayCountyMap (공휴일-지역 매핑)
├─ id (PK)
├─ holiday_id (FK → Holiday)
└─ county_id (FK → County)

County (지역)
├─ id (PK)
└─ name (Unique) - 지역명
```



## 🔍 주요 기능

### ✅ 구현 완료
1. **데이터 적재**: 최초 실행 시 최근 5년 전체 국가 공휴일 자동 로딩 (`@PostConstruct`, test 프로파일에서 비활성화)
2. **검색**: 연도별·국가별·날짜 범위 필터 + 페이징 (QueryDSL 활용)
3. **재동기화**: 특정 연도·국가 데이터 Upsert (있으면 Update, 없으면 Insert)
4. **삭제**: 특정 연도·국가의 공휴일 전체 삭제
5. **지원 국가 조회**: 전체 지원 국가 목록 API 제공
6. **웹 UI**: 공휴일 조회/재동기화/삭제를 위한 웹 인터페이스
7. **API 문서화**: Swagger UI로 모든 API 자동 노출
8. **테스트**: Controller, Service, Repository 계층별 단위/통합 테스트 작성

## 🧪 테스트 실행 방법

### 전체 테스트 실행
```bash
./gradlew test
```

### 특정 테스트 클래스 실행
```bash
./gradlew test --tests HolidayControllerTest
./gradlew test --tests CountryControllerTest
./gradlew test --tests HolidayRepositoryTest
./gradlew test --tests NagerHolidayServiceTest
./gradlew test --tests NagerCountryServiceTest
```

### 테스트 구성
- **HolidayControllerTest**: 공휴일 REST API 엔드포인트 테스트 (MockMvc)
- **CountryControllerTest**: 국가 REST API 엔드포인트 테스트 (MockMvc)
- **HolidayRepositoryTest**: QueryDSL 동적 쿼리 및 페이징 테스트
- **NagerHolidayServiceTest**: 공휴일 비즈니스 로직 통합 테스트
- **NagerCountryServiceTest**: 국가 비즈니스 로직 통합 테스트

**참고**: 테스트 실행 시 DataLoader가 자동으로 비활성화됩니다 (`@ActiveProfiles("test")`)

### 테스트 성공 스크린샷

![빌드 성공](/images/build-success.png)



## 🖥 웹 UI

애플리케이션 실행 후 http://localhost:8080 접속 시 웹 UI를 사용할 수 있습니다.

### 주요 기능
- **공휴일 조회**: 연도, 국가, 날짜 범위로 필터링하여 공휴일 검색
- **공휴일 재동기화**: 특정 연도/국가의 데이터를 외부 API에서 다시 가져오기
- **공휴일 삭제**: 특정 연도/국가의 모든 공휴일 삭제
- **국가 선택**: 드롭다운으로 지원 국가 선택 (100+ 국가)

## 📖 Swagger UI 확인 방법

1. 애플리케이션 실행
```bash
./gradlew bootRun
```

2. 브라우저에서 접속
```
http://localhost:8080/swagger-ui/index.html
```

## 🏗 프로젝트 구조

```
src/main/java/com/holidaykeeper/holidaykeeper
├── client
│   └── NagerApiClient.java              # 외부 API 호출
├── controller
│   ├── HolidayController.java           # 공휴일 REST API 엔드포인트
│   └── CountryController.java           # 국가 REST API 엔드포인트
├── domain
│   ├── Country.java                     # 국가 엔티티
│   ├── County.java                      # 지역 엔티티
│   ├── Holiday.java                     # 공휴일 엔티티
│   ├── HolidayCountyMap.java           # 공휴일-지역 매핑
│   ├── HolidayType.java                # 공휴일 타입 Enum
│   └── HolidayTypeMap.java             # 공휴일-타입 매핑
├── dto
│   ├── request
│   │   ├── HolidayGetRequest.java      # 조회 요청
│   │   ├── HolidayRefreshRequest.java  # 재동기화 요청
│   │   └── HolidayDeleteRequest.java   # 삭제 요청
│   └── response
│       ├── ApiResponse.java            # 공통 응답 래퍼
│       ├── PageResponse.java           # 페이징 응답
│       ├── HolidayResponse.java        # 공휴일 응답
│       └── CountryListResponse.java    # 국가 목록 응답
├── repository
│   ├── HolidayRepository.java          # Holiday Repository
│   ├── HolidayRepositoryCustom.java    # QueryDSL 인터페이스
│   ├── HolidayRepositoryCustomImpl.java # QueryDSL 구현체
│   ├── CountryRepository.java
│   ├── CountyRepository.java
│   └── HolidayTypeMapRepository.java
├── service
│   ├── HolidayService.java             # 공휴일 서비스 인터페이스
│   ├── NagerHolidayService.java        # 공휴일 서비스 구현체
│   ├── CountryService.java             # 국가 서비스 인터페이스
│   ├── NagerCountryService.java        # 국가 서비스 구현체
│   └── CountyService.java              # 지역 서비스
└── DataLoader.java                      # 초기 데이터 로딩 (@Profile("!test"))

src/main/resources
├── application.yml                      # 애플리케이션 설정
└── static                               # 웹 UI
    ├── index.html                       # 메인 페이지
    ├── css/style.css                    # 스타일시트
    └── js/app.js                        # JavaScript 애플리케이션

src/test/java/com/holidaykeeper/holidaykeeper
├── controller
│   ├── HolidayControllerTest.java       # 공휴일 Controller 테스트
│   └── CountryControllerTest.java       # 국가 Controller 테스트
├── repository
│   └── HolidayRepositoryTest.java       # Repository 테스트
└── service
    ├── NagerHolidayServiceTest.java     # 공휴일 Service 통합 테스트
    └── NagerCountryServiceTest.java     # 국가 Service 통합 테스트
```

## 🎯 주요 기술적 고려사항

### 1. QueryDSL 활용
- 동적 쿼리 생성으로 유연한 필터링
- Type-safe 쿼리 작성
- 복잡한 조인 처리

### 2. 연관관계 최적화
- Fetch Join으로 N+1 문제 방지
- 페이징 처리 시 ID 기반 조회 후 별도 Fetch Join
- `MultipleBagFetchException` 회피

### 3. Upsert 구현
- 기존 데이터 조회 → Map 변환 → 존재 여부에 따라 Update/Insert

### 4. 성능 고려
- Lazy Loading 기본 전략
- 동적 쿼리로 불필요한 데이터 조회 방지
- 과도한 SQL 방지
  - 연관관계가 매핑된 데이터를 삭제하는 과정에서 N개의 쿼리가 추가로 발생 (N+1 문제)
  - `deleteAllInBatch` 메서드를 활용해 N+1 문제 해결

### 5. 테스트 환경 분리
- `@Profile("!test")` 어노테이션으로 DataLoader를 test 프로파일에서 비활성화
- `@ActiveProfiles("test")`로 통합 테스트 시 초기 데이터 로딩 방지
- 각 테스트는 독립적인 데이터셋으로 실행

## 📝 외부 API

### Nager.Date API
- **국가 목록**: `GET https://date.nager.at/api/v3/AvailableCountries`
- **공휴일 조회**: `GET https://date.nager.at/api/v3/PublicHolidays/{year}/{countryCode}`

별도의 인증·API Key 불필요

