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
CREATE DATABASE IF NOT EXISTS money_kids_back;
USE money_kids_back;
SET FOREIGN_KEY_CHECKS = 0;

# DROP TABLE IF EXISTS user_quiz;
# DROP TABLE IF EXISTS user_stock;
# DROP TABLE IF EXISTS stock_log;
# DROP TABLE IF EXISTS completion;
# DROP TABLE IF EXISTS feedback;
# DROP TABLE IF EXISTS article;
# DROP TABLE IF EXISTS worksheet;
# DROP TABLE IF EXISTS quiz;
# DROP TABLE IF EXISTS stock;
# DROP TABLE IF EXISTS user;

-- 외래키 체크 다시 켜기
SET FOREIGN_KEY_CHECKS = 1;
-- 사용자 테이블
CREATE TABLE user (
                      id VARCHAR(50) NOT NULL,
                      password VARCHAR(255) NOT NULL,
                      name VARCHAR(255),
                      points INT DEFAULT 0,
                      tendency VARCHAR(100),
                      PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE `QUIZ` (
                        `ID`	INT	NOT NULL,
                        `question`	TEXT	NULL,
                        `answer`	TEXT	NULL,
                        `explanation`	TEXT	NULL,
                        `level`	VARCHAR(50)	NOT NULL,
                        PRIMARY KEY (`ID`)
-- 주식 테이블
CREATE TABLE stock (
                       id VARCHAR(50) NOT NULL,
                       name VARCHAR(255),
                       price INT DEFAULT 0,
                       category VARCHAR(100),
                       PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE `WORKSHEET` (
                             `ID`	INT	NOT NULL,
                             `difficulty`	VARCHAR(50)	NOT NULL,
                             `title`	VARCHAR(255)	NULL,
                             `content`	TEXT	NULL	COMMENT 'url을 저장',
                             PRIMARY KEY (`ID`)
-- 퀴즈 테이블
CREATE TABLE quiz (
                      id INT NOT NULL AUTO_INCREMENT,
                      question TEXT,
                      answer TEXT,
                      explanation TEXT,
                      level VARCHAR(50) NOT NULL,
                      PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 워크시트 테이블
CREATE TABLE worksheet (
                           id INT NOT NULL AUTO_INCREMENT,
                           difficulty VARCHAR(50) NOT NULL,
                           title VARCHAR(255),
                           content TEXT COMMENT 'url 저장용',
                           PRIMARY KEY (id)
) ENGINE=InnoDB;

) ENGINE=InnoDB;

) ENGINE=InnoDB;

) ENGINE=InnoDB;

) ENGINE=InnoDB;

                           stock_id VARCHAR(50) NOT NULL,
) ENGINE=InnoDB;

) ENGINE=InnoDB;