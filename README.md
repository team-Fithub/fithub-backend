<img src="https://github.com/team-Fithub/fithub-backend/assets/68698007/171d1320-a023-413e-a833-6ca9ed57b97c" style="display: block; margin: 0 auto; height: 100px; width: 100px;" />

# 핏헙 (fithub)
- 사람들간의 운동 관련 대화를 나누고 질문할 수 있는 커뮤니티
- 나에게 맞는 트레이닝을 예약해 원하는 트레이닝을 손쉽게 체험
- 사용자 위치 주변에 있는 트레이닝, 트레이너 검색

- 트레이너 인증 시, 트레이닝 생성과 예약 관리

|기능명세서|디자인|ERD|
|---|---|---|
|[![노션](https://github.com/team-Fithub/fithub-backend/assets/68698007/61847bc3-133e-4aa8-88b7-2e08e50d5213)](https://ludicrous-nymphea-84f.notion.site/11ee8f551dec4f5cbee4ad2747b508c2?v=15d4ea2ea2e7438aa25b43ca9435a223&pvs=4)|[![피그마](https://github.com/team-Fithub/fithub-backend/assets/68698007/765064d9-20d7-4418-95d9-c2a42b748088)](https://www.figma.com/file/zxBCG5b4thUB794rg94SkP/Fithub?type=design&node-id=0-1&mode=design&t=CTbCcK3s0fDCrymb-0)|[![erd (1)](https://github.com/team-Fithub/fithub-backend/assets/68698007/8794f608-302f-4653-87ff-d2147b452761)](https://www.erdcloud.com/d/8RSKbMLuBKGmRrCNk)|

![핏헙erd (1)](https://github.com/team-Fithub/fithub-backend/assets/68698007/0893b00d-431a-4553-9629-d71fb39d15c2)


### 핏헙 - 프론트
https://github.com/team-Fithub/fithub-front

# fithub-backend
사이드프로젝트-핏헙 백엔드


## 주요 기능
### 사용자
> - 조회    
> - 트레이너 검색    
> - 현재 위치로 트레이닝 검색    

### 회원
> - 게시글, 댓글 북마크, 좋아요
> - 트레이닝 예약, 리뷰
> - 관심사,위치 기반 트레이너 추천
> - 1:1 채팅
> - 트레이너 인증 요철

### 트레이너
> - 트레이닝 설정
> - 트레이닝 예약 관리


## 문제 해결
### 트레이닝 예약 동시성
> 하나의 트레이닝에는 여러 날짜와 하나의 날짜에 여러 시간이 있다.    
> 하나의 시간 예약에 다수가 접근할 경우 동시성 문제를 생각해야 했다.
> - [포트원 사용 기록](https://darkened-jar-dc8.notion.site/Fithub-PortOne-API-429cac94bd834f33aea0843dc00b614d?pvs=4)
> - [동시성, @Lock 적용](https://darkened-jar-dc8.notion.site/Fithub-Lock-e474e9c6cdcc4b84a368551b45031fd4?pvs=4)
