## 깃허브
- [programmers-lecture/spring-order-management](https://github.com/programmers-lecture/spring-order-management)

## 미션 요구사항

주문관리 API 서버를 개발해야 합니다.
Spring Boot Web을 이용해 로그인 및 회원가입, 상품 관련 API, 주문 관련 API를 구현해야 합니다.

기술 스택 : Java, Gradle, Spring Boot Web, Spring JPA, H2, Spring Security
(기타 의존성은 자유롭게 추가해주시면 됩니다.)

### 모델링
### users
```
CREATE TABLE users  
(  
    seq           bigint      NOT NULL AUTO_INCREMENT, --사용자 PK    name          varchar(10) NOT NULL,                --사용자명  
    email         varchar(50) NOT NULL,                --로그인 이메일  
    passwd        varchar(80) NOT NULL,                --로그인 비밀번호  
    login_count   int         NOT NULL DEFAULT 0,      --로그인 횟수. 로그인시 마다 1 증가  
    last_login_at datetime             DEFAULT NULL,   --최종 로그인 일자  
    create_at     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP(),  
    PRIMARY KEY (seq),  
    CONSTRAINT unq_user_email UNIQUE (email)  
);
```
### products
```
CREATE TABLE products  
(  
    seq          bigint      NOT NULL AUTO_INCREMENT, --상품 PK    name         varchar(50) NOT NULL,                --상품명  
    details      varchar(1000)        DEFAULT NULL,   --상품설명  
    review_count int         NOT NULL DEFAULT 0,      --리뷰 갯수. 리뷰가 새로 작성되면 1 증가  
    create_at    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP(),  
    PRIMARY KEY (seq)  
);
```

### reviews
```
CREATE TABLE reviews  
(  
    seq         bigint        NOT NULL AUTO_INCREMENT, --리뷰 PK    user_seq    bigint        NOT NULL,                --리뷰 작성자 PK (users 테이블 참조)  
    product_seq bigint        NOT NULL,                --리뷰 상품 PK (products 테이블 참조)  
    content     varchar(1000) NOT NULL,                --리뷰 내용  
    create_at   datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP(),  
    PRIMARY KEY (seq),  
    CONSTRAINT fk_reviews_to_users FOREIGN KEY (user_seq) REFERENCES users (seq) ON DELETE RESTRICT ON UPDATE RESTRICT,  
    CONSTRAINT fk_reviews_to_products FOREIGN KEY (product_seq) REFERENCES products (seq) ON DELETE RESTRICT ON UPDATE RESTRICT  
);
```

### orders

```
CREATE TABLE orders  
(  
    seq          bigint   NOT NULL AUTO_INCREMENT, --주문 PK    user_seq     bigint   NOT NULL,                --주문자 PK (users 테이블 참조)  
    product_seq  bigint   NOT NULL,                --주문상품 PK (products 테이블 참조)  
    review_seq   bigint            DEFAULT NULL,   --주문에 대한 리뷰 PK (reviews 테이블 참조)  
    state        enum('REQUESTED','ACCEPTED','SHIPPING','COMPLETED','REJECTED') DEFAULT 'REQUESTED' NOT NULL,  
    --주문상태  
    request_msg  varchar(1000)     DEFAULT NULL,   --주문 요청 메시지  
    reject_msg   varchar(1000)     DEFAULT NULL,   --주문 거절 메시지  
    completed_at datetime          DEFAULT NULL,   --주문 완료 처리 일자  
    rejected_at  datetime          DEFAULT NULL,   -- 주문 거절일자  
    create_at    datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(),  
    PRIMARY KEY (seq),  
    CONSTRAINT unq_review_seq UNIQUE (review_seq),  
    CONSTRAINT fk_orders_to_users FOREIGN KEY (user_seq) REFERENCES users (seq) ON DELETE RESTRICT ON UPDATE RESTRICT,  
    CONSTRAINT fk_orders_to_products FOREIGN KEY (product_seq) REFERENCES products (seq) ON DELETE RESTRICT ON UPDATE RESTRICT,  
    CONSTRAINT fk_orders_to_reviews FOREIGN KEY (review_seq) REFERENCES reviews (seq) ON DELETE RESTRICT ON UPDATE RESTRICT  
);
```

- 샘플 데이터와 스키마는 아래 파일에 있습니다.
```
resources/schema-h2.sql
resources/data-h2.sql
```

- 경량 RDBMS H2를 사용하면 되고, MODE는 MYSQL로 진행해주세요.
```
spring:
  h2:  
    console:  
      enabled: true  
      path: /h2-console  
  datasource:  
    platform: h2  
    driver-class-name: org.h2.Driver  
    url: "jdbc:h2:mem:spring_assignments;MODE=MYSQL;DB_CLOSE_DELAY=-1"  
    username: sa  
    password:  
    hikari:  
      minimum-idle: 1  
      maximum-pool-size: 5  
      pool-name: H2_DB
```

## 1단계 요구사항

### 요건1. API 응답 포맷
정상처리 및 오류처리에 대한 API 서버 공통 응답 포맷을 아래와 같이 정의 합니다.

-   정상처리 및 오류처리 모두 success 필드를 포함합니다.
    -   정상처리라면 true, 오류처리라면 false 값을 출력합니다.
-   정상처리는 response 필드를 포함하고 error 필드는 null 입니다.
    -   응답 데이터가 `단일 객체`라면, response 필드는 `JSON Object`로 표현됩니다.
    -   응답 데이터가 `스칼라 타입(string, int, boolean)`이라면, response 필드는 `string, int, boolean로 표현`됩니다.
    -   응답 데이터가 `Collection`이라면, response 필드는 `JSON Array`로 표협됩니다.
-   오류처리는 error 필드를 포함하고 response 필드는 null 입니다. error 필드는 status, message 필드를 포함합니다.
    -   status : HTTP Response status code 값과 동일한 값을 출력해야 합니다.
    -   message : 오류 메시지가 출력 됩니다.

### 요건2. 로그인 및 회원가입 API 구현
- JWT가 아닌 세션 기반으로 로그인을 구현해주세요.
- 로그인: /api/users/login
    - 로그인 성공 응답 예시
```
{
    "success": true,
    "response": {
        "user": {
            "name": "tester",
            "email": {
                "address": "tester@gmail.com"
            },
            "loginCount": 1,
            "lastLoginAt": "2021-01-20 20:13:51",
            "createAt": "2021-01-20 20:13:36"
        }
    },
    "error": null
}
```
- 로그인 실패 응답 예시
    -  로그인 아이디 누락 (HTTP STATUS 400)
```
{
  "success": false,
  "response": null,
  "error": {
    "message": "principal must be provided",
    "status": 400
  }
}
```
- 로그인 아이디/비밀번호 미일치 (HTTP STATUS 401)
```
{
  "success": false,
  "response": null,
  "error": {
    "message": "Bad credential",
    "status": 401
  }
}
```
- 회원가입: /api/users/signup
- Request

```
{
	name:
	email:
	password:
}
```
- Response
```
201 Created
```
- 전체 공개 API
    - 단일 상품 조회: /api/products/{id}
    -   상품 목록 조회: /api/products
-
## 2단계 요구사항
인증 사용자용 API
-   내 정보 조회: /api/users/me
-   주문 리뷰작성: /api/orders/{id}/review
-   주문 접수처리: /api/orders/{id}/accept
-   주문 배송처리: /api/orders/{id}/shipping
-   주문 완료처리: /api/orders/{id}/complete
-   주문 거절처리: /api/orders/{id}/reject
-   단일 주문조회: /api/orders/{id}
-   주문 목록조회: /api/orders

#### 3.1. 주문 리뷰 작성

인증된 사용자 본인의 주문에 대해 리뷰를 작성한다.

주문 상태 `state`가 `COMPLETED`라면 리뷰를 작성할 수 있다. 단, 동일한 주문에 대해 중복 리뷰를 작성할 수 없다. 정상적으로 리뷰가 작성되면 리뷰 대상 `Product`의 `reviewCount` 값이 1 증가한다.

리뷰 작성이 불가능하다면 `400 오류`를 응답한다.

-   구분: 인증 사용자용 API
-   구현 컨트롤러: com.github.prgrms.orders.ReviewRestController
-   컨트롤러 메소드명: review
-   URL: POST /api/orders/{id}/review
    -   {id}: 리뷰를 남기려는 주문의 PK
-   Request Body: 리뷰 내용

```
{
  "content": "review test"
}
```

-   Response Body: 작성된 리뷰 내용

```
{
  "success": true,
  "response": {
    "seq": 2,
    "productId": 3,
    "content": "review test",
    "createAt": "2021-01-20 20:16:47"
  },
  "error": null
}
```

-   중복 리뷰 작성 오류 응답 예시

```
{
  "success": false,
  "response": null,
  "error": {
    "message": "Could not write review for order 4 because have already written",
    "status": 400
  }
}
```

-   주문 상태 `state`가 `COMPLETED`가 아닌 경우 오류 응답 예시

```
{
  "success": false,
  "response": null,
  "error": {
    "message": "Could not write review for order 1 because state(REQUESTED) is not allowed",
    "status": 400
  }
}
```

#### 3.2. 주문 목록 조회

인증된 사용자 본인의 주문 목록을 출력한다.

주문은 `Review`를 포함할 수 있다. (샘플 데이터에서 `seq=4`인 주문은 리뷰를 포함한다.)

-   구분: 인증 사용자용 API
-   구현 컨트롤러: com.github.prgrms.orders.OrderRestController
-   컨트롤러 메소드명: findAll
-   URL: GET /api/orders?offset=0&size=5
    -   offset: offset 기반 페이징 처리 파리미터 (최소값: 0, 최대값: Long.MAX_VALUE, 기본값: 0)
    -   size: 출력할 아이템의 갯수 (최소값 1, 최대값: 5, 기본값: 5)
    -   offset, size 값이 최소값~최대값 범위 밖이거나 주어지지 않는다면 기본값으로 대체
-   Response Body: 주문 내용 목록 (아래 출력 예시는 offset=0, size=5 인 경우)
    -   offset=2 라면, `seq=5`인 주문부터 출력되야함

> 힌트: 페이징 파라미터 (offset, size) 처리를 위해 com.github.prgrms.configures.web.SimplePageRequestHandlerMethodArgumentResolver 클래스 완성이 필요합니다.  
> RestController에서는 Pageable 타입으로 페이징 파라미터 접근이 가능합니다.

```
{
  "success": true,
  "response": [
    {
      "seq": 7,
      "productId": 3,
      "review": null,
      "state": "REQUESTED",
      "requestMessage": null,
      "rejectMessage": null,
      "completedAt": null,
      "rejectedAt": null,
      "createAt": "2021-01-20 20:13:36"
    },
    {
      "seq": 6,
      "productId": 3,
      "review": null,
      "state": "REJECTED",
      "requestMessage": null,
      "rejectMessage": "No stock",
      "completedAt": null,
      "rejectedAt": "2021-01-24 18:30:00",
      "createAt": "2021-01-20 20:13:36"
    },
    {
      "seq": 5,
      "productId": 3,
      "review": {
        "seq": 2,
        "productId": 3,
        "content": "review test",
        "createAt": "2021-01-20 20:16:47"
      },
      "state": "COMPLETED",
      "requestMessage": null,
      "rejectMessage": null,
      "completedAt": "2021-01-24 10:30:10",
      "rejectedAt": null,
      "createAt": "2021-01-20 20:13:36"
    },
    {
      "seq": 4,
      "productId": 2,
      "review": {
        "seq": 1,
        "productId": 2,
        "content": "I like it!",
        "createAt": "2021-01-20 20:13:36"
      },
      "state": "COMPLETED",
      "requestMessage": "plz send it quickly!",
      "rejectMessage": null,
      "completedAt": "2021-01-24 12:10:30",
      "rejectedAt": null,
      "createAt": "2021-01-20 20:13:36"
    },
    {
      "seq": 3,
      "productId": 2,
      "review": null,
      "state": "SHIPPING",
      "requestMessage": null,
      "rejectMessage": null,
      "completedAt": null,
      "rejectedAt": null,
      "createAt": "2021-01-20 20:13:36"
    }
  ],
  "error": null
}
```

#### 3.3. 개별 주문 조회

인증된 사용자 본인의 개별 주문을 출력한다.

주문은 `Review`를 포함할 수 있다. (샘플 데이터에서 `seq=4`인 주문은 리뷰를 포함한다.)

-   구분: 인증 사용자용 API
-   구현 컨트롤러: com.github.prgrms.orders.OrderRestController
-   컨트롤러 메소드명: findById
-   URL: GET /api/orders/{id}
    -   {id}: 조회 대상 주문의 PK
-   Response Body: 주문 내용

```
{
  "success": true,
  "response": {
    "seq": 4,
    "productId": 2,
    "review": {
      "seq": 1,
      "productId": 2,
      "content": "I like it!",
      "createAt": "2021-01-20 20:13:36"
    },
    "state": "COMPLETED",
    "requestMessage": "plz send it quickly!",
    "rejectMessage": null,
    "completedAt": "2021-01-24 12:10:30",
    "rejectedAt": null,
    "createAt": "2021-01-20 20:13:36"
  },
  "error": null
}
```

#### 3.4. 주문 접수 처리

인증된 사용자 본인의 주문에 대해 상태를 변경한다.

주문이 최초 생성될 때 주문 상태 `state`는 `REQUESTED`이다. 주문 상태가 `REQUESTED`라면 접수 처리를 할 수 있다. 정상적으로 접수 처리 되면 주문 상태는 `ACCEPTED`로 변경된다.

상태 변경이 불가능하다면 예외를 발생시키지 말고 `false`를 정상 반환한다.

-   구분: 인증 사용자용 API
-   구현 컨트롤러: com.github.prgrms.orders.OrderRestController
-   컨트롤러 메소드명: accept
-   URL: PATCH /api/orders/{id}/accept
    -   {id}: 상태를 변경할 주문의 PK
-   Response Body: true 라면, 상태변경 성공

```
{
  "success": true,
  "response": true,
  "error": null
}
```

-   주문 상태 `state`가 `REQUESTED`가 아닌 경우 응답 예시

```
{
  "success": true,
  "response": false,
  "error": null
}
```

#### 3.5. 주문 거절 처리

인증된 사용자 본인의 주문에 대해 상태를 변경한다.

주문이 최초 생성될 때 주문 상태 `state`는 `REQUESTED`이다. 주문 상태가 `REQUESTED`라면 거절 처리를 할 수 있다. 정상적으로 거절 처리 되면 주문 상태는 `REJECTED`로 변경된다. 그리고 주문 거절 시각 `rejectedAt`은 현재 시각으로 설정된다.

상태 변경이 불가능하다면 예외를 발생시키지 말고 `false`를 정상 반환한다.

-   구분: 인증 사용자용 API
-   구현 컨트롤러: com.github.prgrms.orders.OrderRestController
-   컨트롤러 메소드명: reject
-   URL: PATCH /api/orders/{id}/reject
    -   {id}: 상태를 변경할 주문의 PK
-   Request Body: 거절 메세지

```
{
  "message": "reject message"
}
```

-   Response Body: true 라면, 상태변경 성공

```
{
  "success": true,
  "response": true,
  "error": null
}
```

-   주문 상태 `state`가 `REQUESTED`가 아닌 경우 응답 예시

```
{
  "success": true,
  "response": false,
  "error": null
}
```

#### 3.6. 주문 배송 처리

인증된 사용자 본인의 주문에 대해 상태를 변경한다.

주문 상태 `state`가 `ACCEPTED`라면 배송 처리를 할 수 있다. 정상적으로 배송 처리 되면 주문 상태는 `SHIPPING`으로 변경된다.

상태 변경이 불가능하다면 예외를 발생시키지 말고 `false`를 정상 반환한다.

-   구분: 인증 사용자용 API
-   구현 컨트롤러: com.github.prgrms.orders.OrderRestController
-   컨트롤러 메소드명: shipping
-   URL: PATCH /api/orders/{id}/shipping
    -   {id}: 상태를 변경할 주문의 PK
-   Response Body: true 라면, 상태변경 성공

```
{
  "success": true,
  "response": true,
  "error": null
}
```

-   주문 상태 `state`가 `ACCEPTED`가 아닌 경우 응답 예시

```
{
  "success": true,
  "response": false,
  "error": null
}
```

#### 3.7. 주문 완료 처리

인증된 사용자 본인의 주문에 대해 상태를 변경한다.

주문 상태 `state`가 `SHIPPING`이라면 완료 처리를 할 수 있다. 정상적으로 완료 처리 되면 주문 상태는 `COMPLETED`로 변경된다. 그리고 주문 완료 시각 `completedAt`은 현재 시각으로 설정된다.

상태 변경이 불가능하다면 예외를 발생시키지 말고 `false`를 정상 반환한다.

-   구분: 인증 사용자용 API
-   구현 컨트롤러: com.github.prgrms.orders.OrderRestController
-   컨트롤러 메소드명: complete
-   URL: PATCH /api/orders/{id}/complete
    -   {id}: 상태를 변경할 주문의 PK
-   Response Body: true 라면, 상태변경 성공

```
{
  "success": true,
  "response": true,
  "error": null
}
```

-   주문 상태 `state`가 `SHIPPING`이 아닌 경우 응답 예시

```
{
  "success": true,
  "response": false,
  "error": null
}
```
