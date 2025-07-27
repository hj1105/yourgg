# Your.GG - 소환사 최근 매치 조회 서비스

리그 오브 레전드(League of Legends) 소환사의 최근 게임 전적을 상세하게 보여주는 웹 애플리케이션입니다.

## 📝 프로젝트 설명

이 프로젝트는 주어진 과제 요구사항에 따라 특정 소환사의 가장 최근 '소환사의 협곡' 매치(일반, 솔로 랭크, 자유 랭크) 상세 정보를 조회하는 기능을 구현한 Spring Boot 기반 웹 서비스입니다. 사용자는 지역을 선택하고 Riot ID(게임 이름#태그라인)를 입력하여 마지막 게임의 상세 정보를 확인할 수 있습니다.

## ✨ 주요 기능

### Riot ID 기반 검색
- 게임 이름#태그라인 형식으로 소환사를 검색합니다.

### 지역 선택 기능
- 한국, 북미, 유럽 등 주요 지역을 선택하여 검색할 수 있습니다.

### 최근 매치 상세 정보
소환사의 협곡에서 진행된 가장 최근 게임의 상세 정보를 표시합니다.
- 게임 모드, 시간 등 기본 정보
- 블루팀/레드팀 정보 (승리/패배 여부)
- 각 팀의 참가자 목록 (인게임 이름, 챔피언, K/D/A)

### 안정적인 API 호출
- Resilience4J의 RateLimiter를 적용하여 Riot API 요청 제한을 준수합니다.

### 성능 최적화
- Spring Cache를 이용해 반복적인 API 호출을 줄이고 응답 속도를 개선합니다.

### 비동기 처리
- 외부 API 호출을 별도 스레드에서 처리하도록 구성하여 향후 비동기 처리 확장이 용이하도록 설계했습니다.

## 🛠️ 기술 스택

### Backend
- **Kotlin** 1.9.x
- **Spring Boot** 3.x
- **Gradle**
- **Retrofit2**: 타입-세이프(Type-safe) HTTP 클라이언트 (Riot API 연동)
- **Resilience4J**: Rate Limiter (API 요청 제한 처리)
- **Spring Cache** (Caffeine): 캐싱을 통한 성능 최적화

### Frontend
- **Thymeleaf**: Java 템플릿 엔진
- **HTML / CSS**

### Testing
- **JUnit 5**: 단위 테스트 프레임워크
- **MockK**: 코틀린 Mocking 라이브러리

## 🚀 시작하기

### 사전 준비
- Java 17 이상
- **Riot Games API Key**: [Riot Developer Portal](https://developer.riotgames.com/)에서 발급받아야 합니다.

### 실행 방법

1. **프로젝트 클론**
   ```bash
   git clone [저장소_URL]
   cd [프로젝트_폴더]
   ```

2. **API 키 설정**

   `src/main/resources/application.yml` 파일을 열어 API 키를 입력합니다.
   ```yaml
   riot:
     api:
       key: "YOUR_RIOT_API_KEY" # 여기에 발급받은 API 키를 입력하세요.
   ```

3. **프로젝트 빌드 및 실행**

   프로젝트 루트 디렉토리에서 아래의 Gradle 명령어를 실행합니다.
   ```bash
   ./gradlew bootRun
   ```

4. **서비스 접속**

   웹 브라우저에서 [http://localhost:8080](http://localhost:8080)으로 접속합니다.

## 📁 프로젝트 구조

```
src
├── main
│   ├── kotlin/org/geng/yourgg
│   │   ├── YourGgApplication.kt         # 메인 애플리케이션
│   │   ├── config
│   │   │   ├── RegionRouting.kt         # 지역별 API 호스트 관리
│   │   │   └── RetrofitConfig.kt        # Retrofit 동적 설정
│   │   ├── controller
│   │   │   └── MatchController.kt       # 웹 요청 처리
│   │   ├── dto                          # 데이터 전송 객체 (API 응답 등)
│   │   │   ├── AccountDTO.kt
│   │   │   ├── MatchDTO.kt
│   │   │   └── MatchDetailViewDTO.kt    # 화면 표시용 가공 DTO
│   │   ├── retrofit
│   │   │   └── RiotApiService.kt        # Retrofit API 인터페이스
│   │   └── service
│   │       └── MatchService.kt          # 핵심 비즈니스 로직
│   └── resources
│       ├── application.yml              # 설정 파일 (API 키 등)
│       └── templates
│           ├── index.html               # 메인 페이지 (검색 폼)
│           └── match-details.html       # 매치 상세 정보 페이지
└── test
    └── kotlin/org/geng/yourgg/service
        └── MatchServiceTest.kt          # 서비스 로직 Mock 테스트
```

## 📖 사용 방법

1. 웹 브라우저에서 서비스에 접속합니다.
2. 지역을 선택합니다 (예: 한국, 북미, 유럽 등).
3. Riot ID를 입력합니다 (형식: `게임이름#태그라인`).
4. 검색 버튼을 클릭하여 최근 매치 정보를 확인합니다.

## 🔧 개발 참고사항

### API 제한사항
- Riot Games API는 개발용 키의 경우 분당 100회, 2분당 1000회의 요청 제한이 있습니다.
- 본 프로젝트는 Resilience4J RateLimiter를 통해 이러한 제한사항을 준수합니다.

### 캐싱 정책
- 동일한 소환사에 대한 반복 검색 시 캐시된 결과를 반환하여 API 호출을 최소화합니다.
- 캐시 TTL(Time To Live)은 설정 파일에서 조정 가능합니다.
