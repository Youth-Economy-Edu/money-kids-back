-- 참조되는 기본 테이블들을 먼저 생성
CREATE TABLE IF NOT EXISTS USERS (
                        ID	VARCHAR(50)	NOT NULL,
                        password	VARCHAR(255)	NOT NULL,
                        name	VARCHAR(255)	NULL,
                        points	INT	NULL	DEFAULT 0,
                        tendency	VARCHAR(100)	NULL,
                        PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS STOCK (
                         ID	VARCHAR(50)	NOT NULL,
                         name	VARCHAR(255)	NULL,
                         price	INT	NULL	DEFAULT 0,
                         category	VARCHAR(100)	NULL,
                         before_price INT NULL DEFAULT 0,
                         PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS QUIZ (
                        ID	INT	NOT NULL,
                        question	TEXT	NULL,
                        answer	TEXT	NULL,
                        explanation	TEXT	NULL,
                        level	VARCHAR(50)	NOT NULL,
                        PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS WORKSHEET (
                             ID	INT	NOT NULL,
                             difficulty	VARCHAR(50)	NOT NULL,
                             title	VARCHAR(255)	NULL,
                             content	TEXT	NULL,
                             PRIMARY KEY (ID)
);

-- 외래키를 참조하는 테이블들을 생성
CREATE TABLE IF NOT EXISTS FEEDBACK (
                            ID	INT	NOT NULL,
                            user_id	VARCHAR(50)	NOT NULL,
                            date	VARCHAR(20)	NOT NULL,
                            content	TEXT	NULL,
                            PRIMARY KEY (ID, user_id),
                            FOREIGN KEY (user_id) REFERENCES USERS (ID)
);

CREATE TABLE IF NOT EXISTS ARTICLE (
                           ID	INT	NOT NULL,
                           stock_id	VARCHAR(50)	NOT NULL,
                           date	VARCHAR(20)	NOT NULL,
                           title	VARCHAR(255)	NOT NULL,
                           content	TEXT	NULL,
                           effect	VARCHAR(100)	NULL,
                           PRIMARY KEY (ID, stock_id),
                           FOREIGN KEY (stock_id) REFERENCES STOCK (ID)
);

CREATE TABLE IF NOT EXISTS USER_STOCK (
                              ID	INT	NOT NULL,
                              user_id	VARCHAR(50)	NOT NULL,
                              stock_id	VARCHAR(50)	NOT NULL,
                              quantity	INT	NULL	DEFAULT 0,
                              total	INT	NULL	DEFAULT 0,
                              PRIMARY KEY (ID, user_id, stock_id),
                              FOREIGN KEY (user_id) REFERENCES USERS (ID),
                              FOREIGN KEY (stock_id) REFERENCES STOCK (ID)
);

CREATE TABLE IF NOT EXISTS COMPLETION (
                              ID	INT	NOT NULL,
                              user_id	VARCHAR(50)	NOT NULL,
                              worksheet_id	INT	NOT NULL,
                              completion	BOOLEAN	NOT NULL	DEFAULT FALSE,
                              date	VARCHAR(20)	NULL,
                              PRIMARY KEY (ID, user_id, worksheet_id),
                              FOREIGN KEY (user_id) REFERENCES USERS (ID),
                              FOREIGN KEY (worksheet_id) REFERENCES WORKSHEET (ID)
);

CREATE TABLE IF NOT EXISTS STOCKLOG (
                            ID	VARCHAR(255)	NOT NULL,
                            user_id	VARCHAR(50)	NOT NULL,
                            stock_id	VARCHAR(50)	NOT NULL,
                            date	VARCHAR(20)	NULL,
                            quantity	INT	NULL,
                            PRIMARY KEY (ID, user_id, stock_id),
                            FOREIGN KEY (user_id) REFERENCES USERS (ID),
                            FOREIGN KEY (stock_id) REFERENCES STOCK (ID)
);

CREATE TABLE IF NOT EXISTS USER_QUIZ (
                             ID	INT	NOT NULL,
                             user_id	VARCHAR(50)	NOT NULL,
                             quiz_id	INT	NOT NULL,
                             date	VARCHAR(20)	NULL,
                             correct	BOOLEAN	NULL,
                             PRIMARY KEY (ID, user_id, quiz_id),
                             FOREIGN KEY (user_id) REFERENCES USERS (ID),
                             FOREIGN KEY (quiz_id) REFERENCES QUIZ (ID)
);

-- 사용자 성향 분석 결과 저장 테이블
CREATE TABLE IF NOT EXISTS TENDENCY_ANALYSIS (
                                     ID                    INT             NOT NULL,
                                     user_id               VARCHAR(50)     NOT NULL,

    -- 성향별 점수 (개별 성격 특성 강도)
                                     aggressiveness        DOUBLE          NOT NULL,   -- 공격성
                                     assertiveness         DOUBLE          NOT NULL,   -- 자기 주장, 적극성
                                     risk_neutrality       DOUBLE          NOT NULL,   -- 위험을 중립적으로 받아들임
                                     security_oriented     DOUBLE          NOT NULL,   -- 안정 추구 성향
                                     calmness              DOUBLE          NOT NULL,   -- 신중함, 차분함

                                     type                  VARCHAR(100)    NOT NULL,   -- 종합적 성향 분류 (e.g., 공격 투자형)
                                     feedback              TEXT            NULL,       -- AI 피드백 문장
                                     guidance              TEXT            NULL,       -- AI 가이드 문장
                                     created_at            TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,

                                     PRIMARY KEY (ID),
                                     FOREIGN KEY (user_id) REFERENCES USERS (ID)
);

CREATE TABLE IF NOT EXISTS STOCK_PRICE_LOG (
                                 id BIGINT AUTO_INCREMENT NOT NULL,
                                 stock_id VARCHAR(50) NOT NULL,
                                 volatility INT NULL,
                                 price INT NULL,
                                 date TIMESTAMP NULL,
                                 PRIMARY KEY (id),
                                 FOREIGN KEY (stock_id) REFERENCES STOCK (ID)
);

-- 초기 주식 데이터 삽입
INSERT INTO STOCK (ID, name, price, category, before_price) VALUES
('AAPL', 'Apple Inc.', 150000, 'IT', 145000),
('GOOGL', 'Google', 2800000, 'IT', 2750000),
('TSLA', 'Tesla', 800000, 'IT', 820000),
('MSFT', 'Microsoft', 420000, 'IT', 415000),
('AMZN', 'Amazon', 3200000, 'IT', 3180000),
('META', 'Meta Platforms', 320000, 'IT', 315000),
('NVDA', 'NVIDIA', 450000, 'IT', 440000),
('NFLX', 'Netflix', 380000, 'IT', 375000),
('JNJ', 'Johnson & Johnson', 170000, 'Medical', 168000),
('PFE', 'Pfizer', 35000, 'Medical', 34000);

-- 샘플 사용자 데이터
INSERT INTO USERS (ID, password, name, points, tendency) VALUES
('testuser', 'password123', '테스트 유저', 10000, 'conservative');

-- 샘플 퀴즈 데이터
INSERT INTO QUIZ (ID, question, answer, explanation, level) VALUES
(1, '주식의 기본 개념은 무엇인가요?', '회사의 소유권을 나타내는 증서', '주식은 회사의 일부를 소유하는 것을 의미합니다.', 'beginner'),
(2, 'P/E 비율이란 무엇인가요?', '주가수익비율', 'P/E 비율은 주가를 주당순이익으로 나눈 값입니다.', 'intermediate');