# 설명
공통적인 이벤트 등록 클래스

# 기능
- `classgraph` 라이브러리를 사용해서 Event의 모든 서브 클래스에 Event Handler 등록
- 모든 Event Handler는 MiniGameManager의 `processEvent()` 메소드를 호출해서 event를 넘김

# 사용법


# 주의사항


# 개선할 것
## 모든 서브 이벤트 클래스에 Event Handler를 등록하므로 시간이 오래걸림(대략 5초)
- MiniGameManager에서 처리가능한 이벤트 목록만 Event Handler 등록하기(간단하게 시도했지만 실패)
