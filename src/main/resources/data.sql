CREATE DATABASE IF NOT EXISTS money_kids_back;
USE money_kids_back;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS user_quiz;
DROP TABLE IF EXISTS user_stock;
DROP TABLE IF EXISTS stock_log;
DROP TABLE IF EXISTS completion;
DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS article;
DROP TABLE IF EXISTS worksheet;
DROP TABLE IF EXISTS quiz;
DROP TABLE IF EXISTS stock;
DROP TABLE IF EXISTS user;

-- 외래키 체크 다시 켜기
SET FOREIGN_KEY_CHECKS = 1;a
-- 사용자 테이블
CREATE TABLE user (
                      id VARCHAR(50) NOT NULL,
                      password VARCHAR(255) NOT NULL,
                      name VARCHAR(255),
                      points INT DEFAULT 0,
                      tendency VARCHAR(100),
                      PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 주식 테이블
CREATE TABLE stock (
                       id VARCHAR(50) NOT NULL,
                       name VARCHAR(255),
                       price INT DEFAULT 0,
                       category VARCHAR(100),
                       PRIMARY KEY (id)
) ENGINE=InnoDB;

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

-- 피드백 테이블
CREATE TABLE feedback (
                          id INT NOT NULL,
                          user_id VARCHAR(50) NOT NULL,
                          feedback_date VARCHAR(20) NOT NULL,
                          content TEXT,
                          PRIMARY KEY (id, user_id),
                          FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB;

-- 기사 테이블
CREATE TABLE article (
                         id INT NOT NULL,
                         stock_id VARCHAR(50) NOT NULL,
                         article_date VARCHAR(20) NOT NULL,
                         title VARCHAR(255) NOT NULL,
                         content TEXT,
                         effect VARCHAR(100),
                         PRIMARY KEY (id, stock_id),
                         FOREIGN KEY (stock_id) REFERENCES stock(id)
) ENGINE=InnoDB;

-- 유저-주식 보유 테이블
CREATE TABLE user_stock (
                            id INT NOT NULL AUTO_INCREMENT,
                            user_id VARCHAR(50) NOT NULL,
                            stock_id VARCHAR(50) NOT NULL,
                            quantity INT DEFAULT 0,
                            total INT DEFAULT 0,
                            PRIMARY KEY (id, user_id, stock_id),
                            FOREIGN KEY (user_id) REFERENCES user(id),
                            FOREIGN KEY (stock_id) REFERENCES stock(id)
) ENGINE=InnoDB;

-- 워크시트 풀이 여부
CREATE TABLE completion (
                            id INT NOT NULL AUTO_INCREMENT,
                            user_id VARCHAR(50) NOT NULL,
                            worksheet_id INT NOT NULL,
                            completion BOOLEAN NOT NULL DEFAULT FALSE,
                            completion_date VARCHAR(20),
                            PRIMARY KEY (id, user_id, worksheet_id),
                            FOREIGN KEY (user_id) REFERENCES user(id),
                            FOREIGN KEY (worksheet_id) REFERENCES worksheet(id)
) ENGINE=InnoDB;

-- 주식 거래 로그
CREATE TABLE stock_log (
                          id VARCHAR(255) NOT NULL,
                          user_id VARCHAR(50) NOT NULL,
                          stock_id VARCHAR(50) NOT NULL,
                          log_date VARCHAR(20),
                          quantity INT,
                          PRIMARY KEY (id, user_id, stock_id),
                          FOREIGN KEY (user_id) REFERENCES user(id),
                          FOREIGN KEY (stock_id) REFERENCES stock(id)
) ENGINE=InnoDB;

-- 퀴즈 풀이 이력
CREATE TABLE user_quiz (
                           id INT NOT NULL AUTO_INCREMENT,
                           user_id VARCHAR(50) NOT NULL,
                           quiz_id INT NOT NULL,
                           solve_date VARCHAR(20),
                           correct BOOLEAN COMMENT '풀이일자 있을 시 정답 여부',
                           PRIMARY KEY (id, user_id, quiz_id),
                           FOREIGN KEY (user_id) REFERENCES user(id),
                           FOREIGN KEY (quiz_id) REFERENCES quiz(id)
) ENGINE=InnoDB;
