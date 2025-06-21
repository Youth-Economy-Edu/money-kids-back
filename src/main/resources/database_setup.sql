-- ============================================
-- Money Kids Back - 최종 통합 DB 재생성 스크립트
-- ============================================

DROP DATABASE IF EXISTS moneykids;
CREATE DATABASE moneykids CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE moneykids;

-- ============================================
-- 1. 사용자 테이블
-- ============================================

CREATE TABLE USER (
                      USER_ID VARCHAR(50) NOT NULL PRIMARY KEY,
                      USERNAME VARCHAR(255) NOT NULL,
                      PASSWORD VARCHAR(255),
                      EMAIL VARCHAR(255) NOT NULL,
                      BIRTH_DATE DATE,
                      CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      UPDATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                      PROFILE_IMAGE VARCHAR(255),
                      POINTS INT DEFAULT 0,
                      LEVEL INT DEFAULT 1,
                      EXPERIENCE INT DEFAULT 0,
                      LOGIN_TYPE VARCHAR(50) DEFAULT 'EMAIL',
                      IS_ACTIVE BOOLEAN DEFAULT TRUE,
                      LAST_LOGIN TIMESTAMP,
                      OAUTH_PROVIDER VARCHAR(50),
                      OAUTH_ID VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 2. OX 퀴즈 전용 테이블 (최종 엔티티 설계)
-- ============================================

CREATE TABLE QUIZ (
                      ID INT AUTO_INCREMENT PRIMARY KEY,
                      LEVEL VARCHAR(50) NOT NULL,
                      QUESTION TEXT NOT NULL,
                      ANSWER VARCHAR(1) NOT NULL,  -- 'O' 또는 'X'
                      EXPLANATION TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_quiz (
                           ID INT AUTO_INCREMENT PRIMARY KEY,
                           user_id VARCHAR(50) NOT NULL,
                           quiz_id INT NOT NULL,
                           solve_date DATE NOT NULL,
                           correct BOOLEAN NOT NULL,
                           points INT DEFAULT 0,
                           FOREIGN KEY (user_id) REFERENCES USER(USER_ID),
                           FOREIGN KEY (quiz_id) REFERENCES QUIZ(ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE quiz_reward_log (
                                 ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 user_id VARCHAR(50) NOT NULL,
                                 reward_date DATE NOT NULL,
                                 difficulty VARCHAR(50) NOT NULL,
                                 reward_points INT NOT NULL,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 FOREIGN KEY (user_id) REFERENCES USER(USER_ID),
                                 UNIQUE KEY unique_reward (user_id, reward_date, difficulty)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 3. 워크시트 테이블
-- ============================================

CREATE TABLE WORKSHEET (
                           ID INT AUTO_INCREMENT PRIMARY KEY,
                           DIFFICULTY VARCHAR(50) NOT NULL,
                           TITLE VARCHAR(255) NOT NULL,
                           CONTENT TEXT NOT NULL,
                           ANSWER_KEY TEXT,
                           CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 4. 주식 관련 테이블 (기존 유지)
-- ============================================

CREATE TABLE stock (
                       ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       symbol VARCHAR(50) NOT NULL UNIQUE,
                       category VARCHAR(255),
                       company_size VARCHAR(50),
                       current_price DECIMAL(15,2) NOT NULL,
                       previous_price DECIMAL(15,2),
                       price_change DECIMAL(15,2),
                       price_change_percent DECIMAL(5,2),
                       market_cap BIGINT,
                       volume BIGINT DEFAULT 0,
                       description TEXT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE stock_price_log (
                                 ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 stock_id BIGINT NOT NULL,
                                 price DECIMAL(15,2) NOT NULL,
                                 change_amount DECIMAL(15,2),
                                 change_percent DECIMAL(5,2),
                                 volume BIGINT DEFAULT 0,
                                 recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 FOREIGN KEY (stock_id) REFERENCES stock(ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_stock (
                            ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                            user_id VARCHAR(50) NOT NULL,
                            stock_id BIGINT NOT NULL,
                            quantity INT NOT NULL,
                            average_price DECIMAL(15,2) NOT NULL,
                            purchased_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (user_id) REFERENCES USER(USER_ID),
                            FOREIGN KEY (stock_id) REFERENCES stock(ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE stock_log (
                           ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                           user_id VARCHAR(50) NOT NULL,
                           stock_id BIGINT NOT NULL,
                           transaction_type VARCHAR(10) NOT NULL,
                           quantity INT NOT NULL,
                           price DECIMAL(15,2) NOT NULL,
                           total_amount DECIMAL(15,2) NOT NULL,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (user_id) REFERENCES USER(USER_ID),
                           FOREIGN KEY (stock_id) REFERENCES stock(ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE wishlist (
                          ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                          user_id VARCHAR(50) NOT NULL,
                          stock_id BIGINT NOT NULL,
                          added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (user_id) REFERENCES USER(USER_ID),
                          FOREIGN KEY (stock_id) REFERENCES stock(ID),
                          UNIQUE KEY unique_user_stock (user_id, stock_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_stock_alert (
                                  ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  user_id VARCHAR(50) NOT NULL,
                                  stock_id BIGINT NOT NULL,
                                  alert_type VARCHAR(50) NOT NULL,
                                  target_price DECIMAL(15,2),
                                  is_active BOOLEAN DEFAULT TRUE,
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  FOREIGN KEY (user_id) REFERENCES USER(USER_ID),
                                  FOREIGN KEY (stock_id) REFERENCES stock(ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE stock_notification (
                                    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    user_id VARCHAR(50) NOT NULL,
                                    stock_id BIGINT NOT NULL,
                                    message TEXT NOT NULL,
                                    is_read BOOLEAN DEFAULT FALSE,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    FOREIGN KEY (user_id) REFERENCES USER(USER_ID),
                                    FOREIGN KEY (stock_id) REFERENCES stock(ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 5. 뉴스/기사 관련 테이블
-- ============================================

CREATE TABLE article (
                         ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                         title VARCHAR(500) NOT NULL,
                         content TEXT NOT NULL,
                         stock_name VARCHAR(255) NOT NULL,
                         sentiment VARCHAR(50) NOT NULL,
                         date DATE NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 6. 피드백 테이블
-- ============================================

CREATE TABLE FEEDBACK (
                          ID INT AUTO_INCREMENT PRIMARY KEY,
                          USER_ID VARCHAR(50) NOT NULL,
                          FEEDBACK_TYPE VARCHAR(100) NOT NULL,
                          RATING INT,
                          COMMENTS TEXT,
                          CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (USER_ID) REFERENCES USER(USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 최종 완료 메시지
-- ============================================

SELECT 'Money Kids Back 최종 DB 스키마 생성 완료!' AS status;
