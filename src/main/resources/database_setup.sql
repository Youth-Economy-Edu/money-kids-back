-- =====================================================
-- Money Kids Back - 데이터베이스 재생성 스크립트
-- 청소년 경제교육 플랫폼 백엔드 데이터베이스
-- =====================================================

-- 기존 데이터베이스 삭제 및 재생성
DROP DATABASE IF EXISTS moneykids;
CREATE DATABASE moneykids CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE moneykids;

-- =====================================================
-- 1. 사용자 관련 테이블
-- =====================================================

-- 사용자 테이블
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

-- 성향 분석 테이블
CREATE TABLE tendency_analysis (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    analysis_date DATE NOT NULL,
    spending_pattern VARCHAR(255),
    saving_pattern VARCHAR(255),
    investment_tendency VARCHAR(255),
    risk_preference VARCHAR(255),
    financial_goal VARCHAR(255),
    recommendations TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES USER(USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 활동 로그 테이블
CREATE TABLE activity_log (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    activity_type VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    completion_id INT,
    quiz_category VARCHAR(255),
    quiz_level VARCHAR(255),
    stock_category VARCHAR(255),
    stock_company_size VARCHAR(255),
    stocklog_id VARCHAR(255),
    user_quiz_id INT,
    worksheet_category VARCHAR(255),
    worksheet_difficulty VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES USER(USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- 2. 퀴즈 관련 테이블
-- =====================================================

-- 퀴즈 테이블
CREATE TABLE QUIZ (
    QUIZ_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    CATEGORY VARCHAR(100) NOT NULL,
    LEVEL VARCHAR(50) NOT NULL,
    QUESTION TEXT NOT NULL,
    OPTION1 VARCHAR(255) NOT NULL,
    OPTION2 VARCHAR(255) NOT NULL,
    OPTION3 VARCHAR(255) NOT NULL,
    OPTION4 VARCHAR(255) NOT NULL,
    CORRECT_ANSWER INT NOT NULL,
    EXPLANATION TEXT,
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 사용자 퀴즈 결과 테이블
CREATE TABLE user_quiz (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    quiz_id INT NOT NULL,
    user_answer INT NOT NULL,
    is_correct BOOLEAN NOT NULL,
    answered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES USER(USER_ID),
    FOREIGN KEY (quiz_id) REFERENCES QUIZ(QUIZ_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 일일 퀘스트 테이블
CREATE TABLE daily_quest (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    quest_date DATE NOT NULL,
    quest_type VARCHAR(255) NOT NULL,
    target_count INT NOT NULL,
    current_count INT DEFAULT 0,
    is_completed BOOLEAN DEFAULT FALSE,
    reward_points INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES USER(USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- 3. 워크시트 관련 테이블
-- =====================================================

-- 워크시트 테이블
CREATE TABLE WORKSHEET (
    WORKSHEET_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    CATEGORY VARCHAR(100) NOT NULL,
    DIFFICULTY VARCHAR(50) NOT NULL,
    TITLE VARCHAR(255) NOT NULL,
    CONTENT TEXT NOT NULL,
    ANSWER_KEY TEXT,
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- 4. 주식 관련 테이블
-- =====================================================

-- 주식 테이블
CREATE TABLE stock (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
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

-- 주식 가격 이력 테이블
CREATE TABLE stock_price_log (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    stock_id BIGINT NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    change_amount DECIMAL(15,2),
    change_percent DECIMAL(5,2),
    volume BIGINT DEFAULT 0,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (stock_id) REFERENCES stock(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 사용자 주식 보유 테이블
CREATE TABLE user_stock (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    stock_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    average_price DECIMAL(15,2) NOT NULL,
    purchased_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES USER(USER_ID),
    FOREIGN KEY (stock_id) REFERENCES stock(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 주식 거래 로그 테이블
CREATE TABLE stock_log (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    stock_id BIGINT NOT NULL,
    transaction_type VARCHAR(10) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES USER(USER_ID),
    FOREIGN KEY (stock_id) REFERENCES stock(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 레거시 주식 로그 테이블 (기존 데이터 호환성)
CREATE TABLE STOCKLOG (
    STOCKLOG_ID VARCHAR(255) NOT NULL PRIMARY KEY,
    USER_ID VARCHAR(50) NOT NULL,
    STOCK_NAME VARCHAR(255) NOT NULL,
    TRANSACTION_TYPE VARCHAR(10) NOT NULL,
    QUANTITY INT NOT NULL,
    PRICE DECIMAL(15,2) NOT NULL,
    TOTAL_AMOUNT DECIMAL(15,2) NOT NULL,
    TRANSACTION_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (USER_ID) REFERENCES USER(USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 위시리스트 테이블
CREATE TABLE wishlist (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    stock_id BIGINT NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES USER(USER_ID),
    FOREIGN KEY (stock_id) REFERENCES stock(id),
    UNIQUE KEY unique_user_stock (user_id, stock_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 주식 알림 테이블
CREATE TABLE user_stock_alert (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    stock_id BIGINT NOT NULL,
    alert_type VARCHAR(50) NOT NULL,
    target_price DECIMAL(15,2),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES USER(USER_ID),
    FOREIGN KEY (stock_id) REFERENCES stock(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 주식 알림 이력 테이블
CREATE TABLE stock_notification (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    stock_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES USER(USER_ID),
    FOREIGN KEY (stock_id) REFERENCES stock(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- 5. 뉴스/기사 관련 테이블
-- =====================================================

-- 기사 테이블
CREATE TABLE article (
    ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    content TEXT NOT NULL,
    stock_name VARCHAR(255) NOT NULL,
    sentiment VARCHAR(50) NOT NULL,
    date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- 6. 피드백 테이블
-- =====================================================

-- 피드백 테이블
CREATE TABLE FEEDBACK (
    FEEDBACK_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    USER_ID VARCHAR(50) NOT NULL,
    FEEDBACK_TYPE VARCHAR(100) NOT NULL,
    RATING INT,
    COMMENTS TEXT,
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (USER_ID) REFERENCES USER(USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- 인덱스 생성
-- =====================================================

-- 사용자 관련 인덱스
CREATE INDEX idx_user_email ON USER(EMAIL);
CREATE INDEX idx_user_login_type ON USER(LOGIN_TYPE);
CREATE INDEX idx_activity_log_user_date ON activity_log(user_id, created_at);

-- 퀴즈 관련 인덱스
CREATE INDEX idx_quiz_category_level ON QUIZ(CATEGORY, LEVEL);
CREATE INDEX idx_user_quiz_user_id ON user_quiz(user_id);
CREATE INDEX idx_daily_quest_user_date ON daily_quest(user_id, quest_date);

-- 주식 관련 인덱스
CREATE INDEX idx_stock_symbol ON stock(symbol);
CREATE INDEX idx_stock_category ON stock(category);
CREATE INDEX idx_stock_price_log_stock_date ON stock_price_log(stock_id, recorded_at);
CREATE INDEX idx_user_stock_user_id ON user_stock(user_id);
CREATE INDEX idx_stock_log_user_date ON stock_log(user_id, created_at);

-- 기사 관련 인덱스
CREATE INDEX idx_article_date ON article(date);
CREATE INDEX idx_article_stock_name ON article(stock_name);
CREATE INDEX idx_article_sentiment ON article(sentiment);

-- =====================================================
-- 완료 메시지
-- =====================================================

SELECT 'Money Kids Back 데이터베이스 스키마가 성공적으로 생성되었습니다!' as status;
SELECT 'AI 뉴스 기반 주식 시뮬레이션과 청소년 경제교육 플랫폼 준비 완료' as description; 