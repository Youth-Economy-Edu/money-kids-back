-- 참조되는 기본 테이블들을 먼저 생성
CREATE TABLE `USER` (
                        `ID`	VARCHAR(50)	NOT NULL,
                        `password`	VARCHAR(255)	NOT NULL,
                        `name`	VARCHAR(255)	NULL,
                        `points`	INT	NULL	DEFAULT 0,
                        `tendency`	VARCHAR(100)	NULL,
                        PRIMARY KEY (`ID`)
) ENGINE=InnoDB;

CREATE TABLE `STOCK` (
                         `ID`	VARCHAR(50)	NOT NULL,
                         `name`	VARCHAR(255)	NULL,
                         `price`	INT	NULL	DEFAULT 0,
                         `category`	VARCHAR(100)	NULL,
                         PRIMARY KEY (`ID`)
) ENGINE=InnoDB;

CREATE TABLE `QUIZ` (
                        `ID`	INT	NOT NULL,
                        `question`	TEXT	NULL,
                        `answer`	TEXT	NULL,
                        `explanation`	TEXT	NULL,
                        `level`	VARCHAR(50)	NOT NULL,
                        PRIMARY KEY (`ID`)
) ENGINE=InnoDB;

CREATE TABLE `WORKSHEET` (
                             `ID`	INT	NOT NULL,
                             `difficulty`	VARCHAR(50)	NOT NULL,
                             `title`	VARCHAR(255)	NULL,
                             `content`	TEXT	NULL	COMMENT 'url을 저장',
                             PRIMARY KEY (`ID`)
) ENGINE=InnoDB;

-- 외래키를 참조하는 테이블들을 생성
CREATE TABLE `FEEDBACK` (
                            `ID`	INT	NOT NULL,
                            `user_id`	VARCHAR(50)	NOT NULL,
                            `date`	VARCHAR(20)	NOT NULL,
                            `content`	TEXT	NULL,
                            PRIMARY KEY (`ID`, `user_id`),
                            FOREIGN KEY (`user_id`) REFERENCES `USER` (`ID`)
) ENGINE=InnoDB;

CREATE TABLE `ARTICLE` (
                           `ID`	INT	NOT NULL,
                           `stock_id`	VARCHAR(50)	NOT NULL,
                           `date`	VARCHAR(20)	NOT NULL,
                           `title`	VARCHAR(255)	NOT NULL,
                           `content`	TEXT	NULL,
                           `effect`	VARCHAR(100)	NULL,
                           PRIMARY KEY (`ID`, `stock_id`),
                           FOREIGN KEY (`stock_id`) REFERENCES `STOCK` (`ID`)
) ENGINE=InnoDB;

CREATE TABLE `USER-STOCK` (
                              `ID`	INT	NOT NULL,
                              `user_id`	VARCHAR(50)	NOT NULL,
                              `stock_id`	VARCHAR(50)	NOT NULL,
                              `quantity`	INT	NULL	DEFAULT 0,
                              `total`	INT	NULL	DEFAULT 0,
                              PRIMARY KEY (`ID`, `user_id`, `stock_id`),
                              FOREIGN KEY (`user_id`) REFERENCES `USER` (`ID`),
                              FOREIGN KEY (`stock_id`) REFERENCES `STOCK` (`ID`)
) ENGINE=InnoDB;

CREATE TABLE `COMPLETION` (
                              `ID`	INT	NOT NULL,
                              `user_id`	VARCHAR(50)	NOT NULL,
                              `worksheet_id`	INT	NOT NULL,
                              `completion`	BOOLEAN	NOT NULL	DEFAULT FALSE,
                              `date`	VARCHAR(20)	NULL,
                              PRIMARY KEY (`ID`, `user_id`, `worksheet_id`),
                              FOREIGN KEY (`user_id`) REFERENCES `USER` (`ID`),
                              FOREIGN KEY (`worksheet_id`) REFERENCES `WORKSHEET` (`ID`)
) ENGINE=InnoDB;

CREATE TABLE `STOCKLOG` (
                            `ID`	VARCHAR(255)	NOT NULL,
                            `user_id`	VARCHAR(50)	NOT NULL,
                            `stock_id`	VARCHAR(50)	NOT NULL,
                            `date`	VARCHAR(20)	NULL,
                            `quantity`	INT	NULL,
                            PRIMARY KEY (`ID`, `user_id`, `stock_id`),
                            FOREIGN KEY (`user_id`) REFERENCES `USER` (`ID`),
                            FOREIGN KEY (`stock_id`) REFERENCES `STOCK` (`ID`)
) ENGINE=InnoDB;

CREATE TABLE `USER-QUIZ` (
                             `ID`	INT	NOT NULL,
                             `user_id`	VARCHAR(50)	NOT NULL,
                             `quiz_id`	INT	NOT NULL,
                             `date`	VARCHAR(20)	NULL,
                             `correct`	BOOLEAN	NULL	COMMENT '풀이일자가 있을 시에만',
                             PRIMARY KEY (`ID`, `user_id`, `quiz_id`),
                             FOREIGN KEY (`user_id`) REFERENCES `USER` (`ID`),
                             FOREIGN KEY (`quiz_id`) REFERENCES `QUIZ` (`ID`)
) ENGINE=InnoDB;

-- 사용자 성향 분석 결과 저장 테이블
CREATE TABLE `TENDENCY_ANALYSIS` (
                                     `ID`                    INT             NOT NULL,
                                     `user_id`               VARCHAR(50)     NOT NULL,

    -- 성향별 점수 (개별 성격 특성 강도)
                                     `aggressiveness`        DOUBLE          NOT NULL,   -- 공격성
                                     `assertiveness`         DOUBLE          NOT NULL,   -- 자기 주장, 적극성
                                     `risk_neutrality`       DOUBLE          NOT NULL,   -- 위험을 중립적으로 받아들임
                                     `security_oriented`     DOUBLE          NOT NULL,   -- 안정 추구 성향
                                     `calmness`              DOUBLE          NOT NULL,   -- 신중함, 차분함

                                     `type`                  VARCHAR(100)    NOT NULL,   -- 종합적 성향 분류 (e.g., 공격 투자형)
                                     `feedback`              TEXT            NULL,       -- AI 피드백 문장
                                     `guidance`                    TEXT            NULL,       -- AI 가이드 문장
                                     `created_at`            TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,

                                     PRIMARY KEY (`ID`),
                                     FOREIGN KEY (`user_id`) REFERENCES `USER` (`ID`)
) ENGINE=InnoDB;

CREATE TABLE STOCKPRICELOG (
                                 ID   INT NOT NULL,
                                 stock_id VARCHAR(50) NOT NULL,
                                 volatility INT NULL,
                                 date VARCHAR(20) NULL,
                                 PRIMARY KEY (ID),
                                 FOREIGN KEY (stock_id) REFERENCES STOCK (ID)
) ENGINE=InnoDB