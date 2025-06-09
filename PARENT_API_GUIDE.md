# 🏠👨‍👩‍👧‍👦 학부모 전용 API 가이드

Money Kids Back 플랫폼의 **학부모 전용 대시보드 API**를 위한 완전한 가이드입니다.

## 📋 목차
1. [개요](#개요)
2. [API 엔드포인트](#api-엔드포인트)
3. [상세 사용법](#상세-사용법)
4. [응답 형식](#응답-형식)
5. [활용 예시](#활용-예시)

## 🎯 개요

학부모가 자녀의 경제 교육 현황을 모니터링하고 분석할 수 있는 전용 API입니다.

### 주요 기능
- 👤 **자녀 프로필 조회**: 기본 정보, 레벨, 포인트 현황
- 📊 **경제 성향 분석**: 5가지 성향 그래프 및 추이 분석  
- 📈 **학습 성과 추적**: 퀴즈 정답률, 트렌드 분석
- 💰 **투자 포트폴리오**: 보유 주식, 분산도 분석
- 🏃‍♂️ **활동 모니터링**: 일별/주별 활동 현황
- 💡 **맞춤형 추천**: 성향 기반 교육 가이드

---

## 🔗 API 엔드포인트

### 1. 자녀 기본 프로필 조회
```http
GET /api/parent/child/{childId}/profile
```

**응답 예시:**
```json
{
  "code": 200,
  "data": {
    "id": "user123",
    "name": "김민수",
    "points": 2500,
    "tendency": "균형잡힌 투자자",
    "level": 3,
    "nextLevelPoints": 4000
  },
  "msg": "자녀 프로필 조회 성공"
}
```

### 2. 경제 성향 그래프 데이터
```http
GET /api/parent/child/{childId}/tendency-graph
```

**응답 예시:**
```json
{
  "code": 200,
  "data": {
    "scores": {
      "공격성": 65.0,
      "적극성": 78.0,
      "위험중립성": 55.0,
      "안정추구성": 82.0,
      "신중함": 75.0
    },
    "finalType": "신중한 성장투자자",
    "feedback": "안정성을 중시하면서도 성장 가능성을 놓치지 않는 균형잡힌 성향입니다.",
    "guidance": "위험도가 낮은 대형주 위주로 투자해보세요.",
    "lastAnalyzedAt": "2024-01-15T10:30:00"
  },
  "msg": "성향 그래프 데이터 조회 성공"
}
```

### 3. 활동 로그 요약
```http
GET /api/parent/child/{childId}/activity-summary?days=7
```

**파라미터:**
- `days` (optional): 조회할 일수 (기본값: 7일)

**응답 예시:**
```json
{
  "code": 200,
  "data": {
    "totalActivities": 25,
    "activityByType": {
      "QUIZ": 8,
      "TRADE": 5,
      "LOGIN": 7,
      "ARTICLE_READ": 5
    },
    "activityByStatus": {
      "SUCCESS": 20,
      "FAIL": 3,
      "PENDING": 2
    },
    "periodDays": 7,
    "mostActiveDay": "TUESDAY",
    "averageActivitiesPerDay": 3.6
  },
  "msg": "활동 요약 조회 성공"
}
```

### 4. 학습 성과 분석
```http
GET /api/parent/child/{childId}/learning-progress
```

**응답 예시:**
```json
{
  "code": 200,
  "data": {
    "totalQuizzes": 45,
    "correctAnswers": 36,
    "accuracyRate": 80.0,
    "totalPointsEarned": 2500,
    "currentLevel": 3,
    "recentTrend": [
      {"batch": 1, "accuracy": 85.0},
      {"batch": 2, "accuracy": 75.0},
      {"batch": 3, "accuracy": 90.0}
    ]
  },
  "msg": "학습 성과 조회 성공"
}
```

### 5. 투자 포트폴리오 분석
```http
GET /api/parent/child/{childId}/investment-analysis
```

**응답 예시:**
```json
{
  "code": 200,
  "data": {
    "hasInvestments": true,
    "totalStocks": 4,
    "stockComposition": {
      "삼성전자": 2,
      "스타벅스": 1,
      "맥도날드": 3,
      "넥슨게임즈": 1
    },
    "categoryDistribution": {
      "IT": 2,
      "음식료": 2
    },
    "totalInvestmentValue": 450000,
    "diversificationScore": 2
  },
  "msg": "투자 분석 조회 성공"
}
```

### 6. 통합 대시보드 (⭐ 메인 API)
```http
GET /api/parent/child/{childId}/dashboard
```

**응답 예시:**
```json
{
  "code": 200,
  "data": {
    "profile": { /* 프로필 데이터 */ },
    "tendency": {
      "type": "신중한 성장투자자",
      "lastAnalyzed": "2024-01-15T10:30:00"
    },
    "recentActivity": { /* 7일간 활동 요약 */ },
    "learningProgress": { /* 학습 성과 */ },
    "investment": {
      "hasInvestments": true,
      "totalStocks": 4,
      "totalValue": 450000
    }
  },
  "msg": "대시보드 데이터 조회 성공"
}
```

### 7. 성향 변화 추이 분석
```http
GET /api/parent/child/{childId}/tendency-history
```

**응답 예시:**
```json
{
  "code": 200,
  "data": [
    {
      "date": "2024-01-15",
      "type": "신중한 성장투자자",
      "scores": {
        "공격성": 65.0,
        "적극성": 78.0,
        "위험중립성": 55.0,
        "안정추구성": 82.0,
        "신중함": 75.0
      },
      "feedback": "균형잡힌 성향으로 발전하고 있습니다."
    }
  ],
  "msg": "성향 변화 추이 조회 성공"
}
```

### 8. 교육 추천사항
```http
GET /api/parent/child/{childId}/recommendations
```

**응답 예시:**
```json
{
  "code": 200,
  "data": {
    "tendencyBasedAdvice": "위험도가 낮은 대형주 위주로 투자해보세요.",
    "recommendedLearningAreas": [
      "균형잡힌 투자 포트폴리오",
      "기본 경제 원리 복습"
    ],
    "learningRecommendations": [
      "중급 수준의 경제 학습을 권장합니다.",
      "투자 시뮬레이션을 더 활용해보세요."
    ],
    "investmentRecommendations": [
      "다양한 업종의 주식에 분산 투자해보세요.",
      "투자 결과를 정기적으로 검토해보세요."
    ]
  },
  "msg": "교육 추천사항 조회 성공"
}
```

---

## 📱 활용 예시

### React 컴포넌트 예시
```jsx
// ParentDashboard.jsx
import { useState, useEffect } from 'react';

const ParentDashboard = ({ childId }) => {
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        const response = await fetch(`/api/parent/child/${childId}/dashboard`);
        const data = await response.json();
        
        if (data.code === 200) {
          setDashboardData(data.data);
        }
      } catch (error) {
        console.error('대시보드 로딩 실패:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboard();
  }, [childId]);

  if (loading) return <div>로딩 중...</div>;

  return (
    <div className="parent-dashboard">
      <h1>{dashboardData?.profile?.name}님의 경제 교육 현황</h1>
      
      {/* 프로필 카드 */}
      <div className="profile-card">
        <h3>기본 정보</h3>
        <p>레벨: {dashboardData?.profile?.level}</p>
        <p>포인트: {dashboardData?.profile?.points}</p>
        <p>성향: {dashboardData?.tendency?.type}</p>
      </div>

      {/* 학습 성과 */}
      <div className="learning-progress">
        <h3>학습 성과</h3>
        <p>정답률: {dashboardData?.learningProgress?.accuracyRate}%</p>
        <p>푼 문제: {dashboardData?.learningProgress?.totalQuizzes}개</p>
      </div>

      {/* 투자 현황 */}
      <div className="investment-status">
        <h3>투자 현황</h3>
        {dashboardData?.investment?.hasInvestments ? (
          <>
            <p>보유 주식: {dashboardData?.investment?.totalStocks}개</p>
            <p>총 투자금액: {dashboardData?.investment?.totalValue?.toLocaleString()}원</p>
          </>
        ) : (
          <p>아직 투자를 시작하지 않았습니다.</p>
        )}
      </div>
    </div>
  );
};

export default ParentDashboard;
```

### 성향 그래프 예시 (Chart.js)
```jsx
// TendencyChart.jsx
import { Radar } from 'react-chartjs-2';

const TendencyChart = ({ childId }) => {
  const [tendencyData, setTendencyData] = useState(null);

  useEffect(() => {
    const fetchTendency = async () => {
      const response = await fetch(`/api/parent/child/${childId}/tendency-graph`);
      const data = await response.json();
      setTendencyData(data.data);
    };
    
    fetchTendency();
  }, [childId]);

  if (!tendencyData) return <div>로딩 중...</div>;

  const chartData = {
    labels: Object.keys(tendencyData.scores),
    datasets: [{
      label: '경제 성향',
      data: Object.values(tendencyData.scores),
      backgroundColor: 'rgba(54, 162, 235, 0.2)',
      borderColor: 'rgba(54, 162, 235, 1)',
      borderWidth: 2
    }]
  };

  const options = {
    scales: {
      r: {
        beginAtZero: true,
        max: 100
      }
    }
  };

  return (
    <div className="tendency-chart">
      <h3>자녀의 경제 성향 분석</h3>
      <Radar data={chartData} options={options} />
      <p><strong>성향 유형:</strong> {tendencyData.finalType}</p>
      <p><strong>피드백:</strong> {tendencyData.feedback}</p>
    </div>
  );
};
```

---

## 💡 학부모 가이드

### 대시보드 활용법
1. **주간 모니터링**: 매주 `/dashboard` API로 종합 현황 확인
2. **성향 변화 추적**: `/tendency-history`로 장기적 변화 관찰
3. **학습 격려**: 정답률이 낮으면 `/recommendations`로 맞춤 가이드 제공
4. **투자 교육**: 포트폴리오 분석으로 분산투자 중요성 교육

### 주의사항
- 모든 데이터는 교육 목적의 가상 환경입니다
- 자녀의 성향은 시간에 따라 변화할 수 있습니다
- 정기적인 대화를 통해 교육 효과를 높이세요

---

## 🔧 기술 정보

### 인증
현재는 별도 인증이 없지만, 실제 서비스에서는 다음이 필요할 수 있습니다:
- 학부모-자녀 관계 인증
- JWT 토큰 기반 인증
- RBAC (Role-Based Access Control)

### 에러 처리
모든 API는 통일된 에러 형식을 제공합니다:
```json
{
  "code": 500,
  "data": null,
  "msg": "오류 메시지"
}
```

### 성능 최적화
- 대시보드 API는 캐싱 권장
- 대용량 데이터 조회 시 페이지네이션 고려
- 프론트엔드에서 적절한 로딩 상태 처리

---

**🎯 이제 학부모님들이 자녀의 경제 교육 현황을 체계적으로 모니터링하고 맞춤형 가이드를 제공할 수 있습니다!** 