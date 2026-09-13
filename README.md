# Goodbuys

## 실행 및 검증

JDK 17 이상과 기존 MySQL 데이터베이스가 필요합니다. 저장소에는 전체 테이블 생성 SQL과 실제 DB·메일 자격증명이 포함되어 있지 않습니다.

```powershell
cd Goodsbuy
$env:DB_URL = 'jdbc:mysql://localhost:3306/goodsbuy'
$env:DB_USERNAME = 'goodsbuy'
$env:DB_PASSWORD = '<DB 비밀번호>'
$env:MAIL_USERNAME = '<SMTP 계정>'
$env:MAIL_PASSWORD = '<SMTP 앱 비밀번호>'
.\gradlew.bat test bootWar
java -jar build/libs/Goodsbuy-0.0.1-SNAPSHOT.war
```

실행 전에 기존 DB에 [비밀번호 컬럼 확장 SQL](Goodsbuy/sql/password-hash.sql)을 적용하세요. 신규 비밀번호는 BCrypt로 저장하며, 기존 평문 비밀번호는 로그인 성공 시 BCrypt로 바뀝니다. 로그인하지 않은 기존 계정의 평문 데이터는 자동 일괄 변환되지 않습니다.

이미지는 기본적으로 `Goodsbuy`의 상위 `multipartImg`에 저장합니다. 다른 작업 디렉터리에서 실행한다면 `UPLOAD_PATH`를 절대 경로로 지정하세요. `MAIL_HOST`, `MAIL_PORT`도 환경변수로 변경할 수 있습니다. 기존 `application.properties`가 있다면 `datasource.*`를 `spring.datasource.*`로 옮기고, `email.properties` 대신 `spring.mail.*` 또는 위 환경변수를 사용하세요.

Java 17·기존 JSP/MyBatis 구조와의 호환성을 유지하도록 [Spring Boot 3.5.16](https://docs.spring.io/spring-boot/3.5/system-requirements.html), [MyBatis 3.0.5](https://github.com/mybatis/spring-boot-starter/releases), [Gradle 8.14.3](https://docs.gradle.org/8.14.3/release-notes.html)으로 갱신했습니다. Boot 전체 최신 메이저 버전으로의 전환은 포함하지 않았습니다. JSP를 실행 파일에 포함하도록 실행 가능한 WAR를 생성합니다.

테스트는 애플리케이션 컨텍스트, 폼 바인딩, 로그인 필터, 비밀번호 해시·기존 계정 전환, 이메일 인증 실패·만료, 상품 소유권, 채팅방 접근, 이미지 파일 검증을 확인합니다. 실제 MySQL 스키마와 SMTP 전송, 브라우저의 전체 거래 흐름은 별도 연동 검증이 필요합니다.

판매 내역을 구매 내역으로 잘못 표시하던 미완성 메뉴와 엔드포인트를 제거했습니다. 구매 내역은 실제 구매 확정 데이터와 규칙이 마련되면 추가해야 합니다.

<br>

<img src="https://github.com/minwoogi/Goodbuys/assets/96968834/d026f54c-ed50-4e16-94b2-5764137364e0">


 ## 프로젝트 정보 🔖

```
Second hand transaction platform(중고 거래 플랫폼)
Full Stack Project

- Java , SpringBoot

- MySQL MyBatis

- HTML , CSS , JavaScript , JQuery , AJax , bootstarp

-git , github , notion
```
<br>

back
<div align="left">
  <img src="https://img.shields.io/badge/java-007396?style=flat-square&logo=java&logoColor=white"> 
  <img src="https://img.shields.io/badge/springboot-6DB33F?style=flat-square&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/apache tomcat-F8DC75?style=flat-square&logo=apachetomcat&logoColor=white">
  <img src="https://img.shields.io/badge/mysql-4479A1?style=flat-square&logo=mysql&logoColor=white"> 
</div>

<br>

front
<div align="left">
  <img src="https://img.shields.io/badge/HTML5-E34F26?style=flat-square&logo=html5&logoColor=white"/>
  <img src="https://img.shields.io/badge/CSS3-1572B6?style=flat-square&logo=css3&logoColor=white"/>
  <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=flat-square&logo=javascript&logoColor=black"/>
  <img src="https://img.shields.io/badge/jquery-0769AD?style=flat-square&logo=jquery&logoColor=white">
  <img src="https://img.shields.io/badge/bootstrap-7952B3?style=flat-square&logo=bootstrap&logoColor=white">
</div>

<br>

management
<div align="left">
  <img src="https://img.shields.io/badge/github-181717?style=flat-square&logo=github&logoColor=white">
  <img src="https://img.shields.io/badge/git-F05032?style=flat-square&logo=git&logoColor=white">
  <img src="https://img.shields.io/badge/notion-000000?style=flat-square&logo=notion&logoColor=white">
</div>
<br><br>

## File Tree 🎄

```
📦Goodsbuy
 ┣ 📂src
 ┃ ┣ 📂main
 ┃ ┃ ┣ 📂generated
 ┃ ┃ ┣ 📂java
 ┃ ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┃ ┗ 📂tu
 ┃ ┃ ┃ ┃ ┃ ┗ 📂goodsbuy
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂advice
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ExceptionAdvice.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂chat
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂list
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂get
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂post
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂mypage
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂get
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂post
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂param
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂product
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂user
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂get
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂post
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜GoodsBuyController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂global
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂exception
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂file
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂product
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂profile
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂user
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂util
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂filter
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂model
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂enumeration
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂param
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜GoodsbuyApplication.java
 ┃ ┃ ┣ 📂resources
 ┃ ┃ ┃ ┣ 📂static
 ┃ ┃ ┃ ┃ ┣ 📂css
 ┃ ┃ ┃ ┃ ┃ ┣ 📜*.css
 ┃ ┃ ┃ ┃ ┣ 📂img
 ┃ ┃ ┃ ┃ ┃ ┣ 📜*.png
 ┃ ┃ ┃ ┃ ┃ ┣ 📜*.gif
 ┃ ┃ ┃ ┃ ┗ 📂js
 ┃ ┃ ┃ ┃ ┃ ┣ 📜*.js
 ┃ ┃ ┃ ┣ 📜application.properties
 ┃ ┃ ┃ ┗ 📜email.properties
 ┃ ┃ ┗ 📂webapp
 ┃ ┃ ┃ ┗ 📂WEB-INF
 ┃ ┃ ┃ ┃ ┗ 📂view
 ┃ ┃ ┃ ┃ ┃ ┣ 📂chat
 ┃ ┃ ┃ ┃ ┃ ┣ 📂common
 ┃ ┃ ┃ ┃ ┃ ┣ 📂product
 ┃ ┃ ┃ ┃ ┃ ┣ 📂profile
 ┃ ┃ ┃ ┃ ┃ ┣ 📂users
 ┃ ┃ ┃ ┃ ┃ ┣ 📜errorPage.jsp
 ┃ ┃ ┃ ┃ ┃ ┗ 📜index.jsp
 ┣ 📜.gitignore
 ┣ 📜build.gradle
```


<br><br>





## 로그인 , 회원가입 , 이메일인증  :cop:
<img width="1000" src="https://github.com/minwoogi/Goodbuys/assets/96968834/534fef4e-f905-4e94-8bc4-eaf91337caf7">

<br><br>

## 프로필 설정 , 찜하기 , 위치설정 :page_with_curl:

<img width="1000" src="https://github.com/minwoogi/Goodbuys/assets/96968834/05fbb40e-b09a-4d0b-ace9-846752f93ecc">

<br><br>

## 판매완료,상품수정  :triangular_flag_on_post:

<img width="1000" src="https://github.com/minwoogi/Goodbuys/assets/96968834/b666f91c-f376-4edf-b211-646af5621ee2">

<br><br>


## 상품등록 삭제 :o: :x:

<img width="1000" src="https://github.com/minwoogi/Goodbuys/assets/96968834/0ca1e2b7-7fed-48d1-9d27-3b0492863453">

<br><br>

## 채팅 :speech_balloon:

<img width="1000" src="https://github.com/minwoogi/Goodbuys/assets/96968834/386f1708-8d09-4fe4-b1c4-0248379fbc45">
