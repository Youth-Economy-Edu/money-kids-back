package com.moneykidsback.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OpenAiService {

    @Value("${openai.api.key}")
    private String apiKey;

    @jakarta.annotation.PostConstruct
    private void debugApiKey() {
        System.out.println("=== OpenAI API 키 상태 ===");
        System.out.println("API 키 값: " + (apiKey != null ? apiKey.substring(0, Math.min(20, apiKey.length())) + "..." : "null"));
        System.out.println("API 키 길이: " + (apiKey != null ? apiKey.length() : 0));
        System.out.println("sk-로 시작: " + (apiKey != null && apiKey.startsWith("sk-")));
        System.out.println("sk-proj-로 시작: " + (apiKey != null && apiKey.startsWith("sk-proj-")));
        System.out.println("유효성 검사 결과: " + !isInvalidApiKey(apiKey));
        
        if (isInvalidApiKey(apiKey)) {
            System.out.println("⚠️ OpenAI API 키가 설정되지 않았습니다.");
            System.out.println("💡 .env 파일에 OPENAI_API_KEY=sk-your-key-here 를 설정하세요.");
            System.out.println("🔄 기본 응답 모드로 동작합니다.");
        } else {
            System.out.println("✅ OpenAI API 키가 올바르게 설정되었습니다.");
        }
        System.out.println("========================");
    }

    public String getChatCompletionSync(String prompt) {
        System.out.println("🤖 OpenAI API 호출 시작 - 프롬프트: " + prompt.substring(0, Math.min(50, prompt.length())) + "...");
        System.out.println("🔑 API 키 유효성: " + !isInvalidApiKey(apiKey));
        
        // API 키가 비어있거나 더미 키인 경우 시나리오별 기본 응답 반환
        if (isInvalidApiKey(apiKey)) {
            System.out.println("❌ API 키가 유효하지 않아 기본 응답 반환");
            return generateFallbackNews(prompt);
        }
        
        System.out.println("✅ API 키가 유효하여 실제 OpenAI API 호출 시도");
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));
            requestBody.put("temperature", 0.7);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions", 
                request, 
                Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            
            if (responseBody == null || !responseBody.containsKey("choices")) {
                throw new RuntimeException("OpenAI 응답 형식 오류");
            }

            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String content = message.get("content").toString();
            
            return content;
            
        } catch (Exception e) {
            System.err.println("OpenAI API 호출 실패: " + e.getMessage());
            System.err.println("오류 유형: " + e.getClass().getSimpleName());
            if (e.getCause() != null) {
                System.err.println("원인: " + e.getCause().getMessage());
            }
            e.printStackTrace();
            return generateFallbackNews(prompt);
        }
    }

    // 프롬프트 기반으로 적절한 기본 뉴스 생성
    private String generateFallbackNews(String prompt) {
        Random random = new Random();
        
        // 회사명 추출 시도
        String companyName = "회사";
        if (prompt.contains("레고")) companyName = "레고코리아";
        else if (prompt.contains("포켓몬")) companyName = "포켓몬카드";
        else if (prompt.contains("맥도날드")) companyName = "맥도날드";
        else if (prompt.contains("스타벅스")) companyName = "스타벅스";
        else if (prompt.contains("넥슨")) companyName = "넥슨게임즈";
        else if (prompt.contains("오리온")) companyName = "오리온";
        else if (prompt.contains("농심")) companyName = "농심";
        else if (prompt.contains("배스킨")) companyName = "배스킨라빈스";
        else if (prompt.contains("코카콜라")) companyName = "코카콜라";
        
        String[] effects = {"호재", "악재", "중립"};
        String effect = effects[random.nextInt(effects.length)];
        
        String[] newsTemplates = {
            "%s, 신제품 출시로 주목받아",
            "%s, 해외 시장 진출 계획 발표",
            "%s, 환경 친화적 경영 방침 도입",
            "%s, 디지털 혁신 프로젝트 시작",
            "%s, 고객 서비스 품질 개선 노력",
            "%s, 사회공헌 활동 확대 발표",
            "%s, 새로운 마케팅 전략 공개",
            "%s, 기술 개발 투자 증대",
            "%s, 브랜드 리뉴얼 프로젝트 진행",
            "%s, 글로벌 파트너십 체결"
        };
        
        String[] contentTemplates = {
            "%s가 최근 새로운 변화를 시도하고 있습니다. 이번 계획은 고객들에게 더 나은 서비스를 제공하기 위한 것으로, 업계에서 주목받고 있습니다.",
            "%s가 혁신적인 접근 방식을 도입한다고 발표했습니다. 이를 통해 시장에서의 경쟁력을 높이고 고객 만족도를 개선할 것으로 기대됩니다.",
            "%s가 지속가능한 경영을 위한 새로운 방향을 제시했습니다. 환경을 생각하는 기업 활동으로 사회적 책임을 다하고 있습니다.",
            "%s가 디지털 시대에 맞는 변화를 추진하고 있습니다. 최신 기술을 활용하여 고객 경험을 개선하는 것이 목표입니다."
        };
        
        String title = String.format(newsTemplates[random.nextInt(newsTemplates.length)], companyName);
        String content = String.format(contentTemplates[random.nextInt(contentTemplates.length)], companyName);
        
        return String.format("[%s] %s\n%s", effect, title, content);
    }

    public int getRandomPriceFromOpenAi(int currentPrice) {
        // API 키가 비어있거나 더미 키인 경우 랜덤 변동 적용
        if (isInvalidApiKey(apiKey)) {
            Random random = new Random();
            double changeRate = (random.nextDouble() - 0.5) * 0.1; // -5% ~ +5%
            int randomPrice = (int) (currentPrice * (1 + changeRate));
            return randomPrice;
        }
        
        try {
            String prompt = String.format(
                "현재 주가가 %d원입니다. 실제 주식 시장처럼 약간의 변동을 주어 새로운 주가를 제안해주세요. " +
                "±5%% 범위 내에서 자연스러운 변동을 적용하되, 정수로만 답변해주세요.", 
                currentPrice
            );
            
            String response = getChatCompletionSync(prompt);
            
            // 응답에서 숫자만 추출
            int newPrice = extractNumber(response);
            
            // 유효하지 않은 가격이면 현재 가격 기준으로 약간의 랜덤 변동 적용
            if (newPrice <= 0 || Math.abs(newPrice - currentPrice) > currentPrice * 0.1) {
                Random random = new Random();
                double changeRate = (random.nextDouble() - 0.5) * 0.1; // -5% ~ +5%
                newPrice = (int) (currentPrice * (1 + changeRate));
            }
            
            return newPrice;
        } catch (Exception e) {
            // API 호출 실패시 랜덤 변동 적용
            Random random = new Random();
            double changeRate = (random.nextDouble() - 0.5) * 0.1; // -5% ~ +5%
            int fallbackPrice = (int) (currentPrice * (1 + changeRate));
            return fallbackPrice;
        }
    }

    // 응답 문자열에서 첫 번째 숫자 추출
    private int extractNumber(String content) {
        Matcher matcher = Pattern.compile("\\d+").matcher(content);
        return matcher.find() ? Integer.parseInt(matcher.group()) : -1;
    }

    private boolean isInvalidApiKey(String key) {
        return key == null || 
               key.trim().isEmpty() || 
               key.equals("dummy") || 
               key.equals("your-openai-api-key-here") ||
               key.equals("your-actual-api-key-here") ||
               key.equals("sk-proj-your-actual-openai-api-key-here") ||
               (!key.startsWith("sk-") && !key.startsWith("sk-proj-"));
    }

    /**
     * 기사 생성 전용 메서드
     * NewsGenerateService에서 사용
     */
    public String generateNewsArticle(String prompt) {
        System.out.println("📰 기사 생성 API 호출 시작");
        
        // API 키가 비어있거나 더미 키인 경우 대체 기사 생성
        if (isInvalidApiKey(apiKey)) {
            System.out.println("❌ API 키 없음 - 대체 기사 생성");
            return generateFallbackNewsArticle(prompt);
        }
        
        try {
            String response = getChatCompletionSync(prompt);
            
            // 응답 검증 및 정제
            if (response == null || response.trim().isEmpty()) {
                return generateFallbackNewsArticle(prompt);
            }
            
            // 기본 응답 형태가 아닌 경우 그대로 반환
            if (!response.contains("기본 응답")) {
                return response;
            }
            
            return generateFallbackNewsArticle(prompt);
            
        } catch (Exception e) {
            System.err.println("기사 생성 API 호출 실패: " + e.getMessage());
            return generateFallbackNewsArticle(prompt);
        }
    }

    /**
     * 대체 기사 생성 (API 실패 시)
     */
    private String generateFallbackNewsArticle(String prompt) {
        Random random = new Random();
        
        // 주식명 추출 시도
        String stockName = "회사";
        if (prompt.contains("기업명:")) {
            try {
                String[] lines = prompt.split("\n");
                for (String line : lines) {
                    if (line.contains("기업명:")) {
                        stockName = line.split("기업명:")[1].trim();
                        break;
                    }
                }
            } catch (Exception e) {
                stockName = "주요기업";
            }
        }
        
        String[] sentiments = {"긍정", "부정", "중립"};
        String[] impacts = {"강함", "보통", "약함"};
        
        String sentiment = sentiments[random.nextInt(sentiments.length)];
        String impact = impacts[random.nextInt(impacts.length)];
        
        // 기업별 맞춤형 템플릿 선택
        String[] titleTemplates = getTitleTemplatesByCompany(stockName);
        String[] contentTemplates = getContentTemplatesByCompany(stockName);
        
        String title = String.format(titleTemplates[random.nextInt(titleTemplates.length)], stockName);
        String content = String.format(contentTemplates[random.nextInt(contentTemplates.length)], stockName);
        
        return String.format("%s:%s\n제목: %s\n내용: %s", sentiment, impact, title, content);
    }
    
    // 기업별 맞춤형 제목 템플릿
    private String[] getTitleTemplatesByCompany(String companyName) {
        if (companyName.contains("레고")) {
            return new String[]{
                "%s, 새로운 테마 세트 글로벌 출시",
                "%s, 창의력 교육 프로그램 확대 운영",
                "%s, 친환경 소재 활용 제품 개발",
                "%s, 체험형 매장 확대 계획 발표",
                "%s, 어린이 안전 기준 강화 추진"
            };
        } else if (companyName.contains("포켓몬")) {
            return new String[]{
                "%s, 한정판 컬렉션 카드 출시",
                "%s, 팬 커뮤니티 이벤트 개최",
                "%s, 게임 연동 체험존 오픈",
                "%s, 교육용 카드게임 개발",
                "%s, 글로벌 토너먼트 개최 예정"
            };
        } else if (companyName.contains("맥도날드")) {
            return new String[]{
                "%s, 계절 한정 메뉴 라인업 공개",
                "%s, 매장 디자인 리뉴얼 프로젝트",
                "%s, 지역 특산물 활용 메뉴 개발",
                "%s, 친환경 포장재 전면 도입",
                "%s, 어린이 놀이공간 확대 운영"
            };
        } else if (companyName.contains("스타벅스")) {
            return new String[]{
                "%s, 시즌 시그니처 음료 출시",
                "%s, 지역 로스터리 매장 확장",
                "%s, 바리스타 역량 강화 프로그램",
                "%s, 리저브 매장 체험 공간 확대",
                "%s, 친환경 매장 운영 확대"
            };
        } else if (companyName.contains("농심")) {
            return new String[]{
                "%s, 건강 지향 신제품 라인 출시",
                "%s, 해외 현지화 제품 개발",
                "%s, 품질 관리 시스템 고도화",
                "%s, 지속가능 포장재 연구 발표",
                "%s, 소비자 맞춤형 제품 확대"
            };
        } else if (companyName.contains("오리온")) {
            return new String[]{
                "%s, 프리미엄 과자 브랜드 론칭",
                "%s, 글로벌 시장 진출 가속화",
                "%s, 원료 직접 조달 시스템 구축",
                "%s, 소비자 피드백 반영 제품 개선",
                "%s, 건강 간식 카테고리 확대"
            };
        } else if (companyName.contains("넥슨")) {
            return new String[]{
                "%s, 차세대 모바일 게임 개발 착수",
                "%s, 글로벌 게임 스튜디오 인수",
                "%s, e스포츠 생태계 육성 투자",
                "%s, 게임 개발자 교육 프로그램",
                "%s, 메타버스 플랫폼 구축 계획"
            };
        } else if (companyName.contains("배스킨")) {
            return new String[]{
                "%s, 여름 시즌 신맛 아이스크림 출시",
                "%s, 매장 인테리어 컨셉 변화",
                "%s, 케이크 커스터마이징 서비스",
                "%s, 비건 아이스크림 라인 확대",
                "%s, 지역별 한정 플레이버 개발"
            };
        } else if (companyName.contains("코카콜라")) {
            return new String[]{
                "%s, 무설탕 음료 포트폴리오 확장",
                "%s, 재활용 페트병 활용 확대",
                "%s, 지역 커뮤니티 후원 활동",
                "%s, 건강 음료 브랜드 강화",
                "%s, 디지털 마케팅 전략 혁신"
            };
        } else {
            return new String[]{
                "%s, 고객 서비스 디지털 혁신",
                "%s, 사회공헌 프로그램 확대",
                "%s, 지속가능 경영 실천 방안",
                "%s, 브랜드 경험 향상 프로젝트",
                "%s, 직원 복지 제도 개선"
            };
        }
    }
    
    // 기업별 맞춤형 내용 템플릿
    private String[] getContentTemplatesByCompany(String companyName) {
        if (companyName.contains("레고")) {
            return new String[]{
                "%s가 어린이들의 창의력 발달을 돕는 새로운 교육 프로그램을 선보인다. 놀이를 통한 학습 효과를 극대화하여 부모들의 관심이 높아지고 있다.",
                "%s가 환경 보호를 위한 친환경 소재 개발에 박차를 가하고 있다. 지속가능한 장난감 제작으로 글로벌 환경 경영을 실천하고 있다."
            };
        } else if (companyName.contains("포켓몬")) {
            return new String[]{
                "%s가 수집가들을 위한 특별한 경험을 제공하는 이벤트를 기획하고 있다. 팬들의 열정적인 참여로 커뮤니티가 더욱 활성화되고 있다.",
                "%s가 어린이 교육에 도움이 되는 카드게임 콘텐츠를 개발하고 있다. 재미와 학습을 동시에 추구하는 새로운 접근 방식이 주목받고 있다."
            };
        } else if (companyName.contains("맥도날드")) {
            return new String[]{
                "%s가 고객 만족도 향상을 위한 메뉴 혁신에 지속적으로 투자하고 있다. 다양한 연령층의 취향을 반영한 제품 개발로 호평을 받고 있다.",
                "%s가 매장 환경 개선을 통해 고객 경험을 한층 업그레이드하고 있다. 편안하고 쾌적한 공간 조성으로 브랜드 가치를 높이고 있다."
            };
        } else if (companyName.contains("스타벅스")) {
            return new String[]{
                "%s가 커피 문화 확산을 위한 다양한 체험 프로그램을 운영하고 있다. 고품질 커피에 대한 고객들의 관심과 만족도가 지속적으로 증가하고 있다.",
                "%s가 지역 커뮤니티와의 상생을 위한 프로그램을 확대하고 있다. 지역 특성을 반영한 매장 운영으로 고객들에게 특별한 경험을 제공한다."
            };
        } else if (companyName.contains("농심")) {
            return new String[]{
                "%s가 소비자 건강을 고려한 제품 혁신에 앞장서고 있다. 맛과 영양을 모두 만족시키는 제품 개발로 시장에서 좋은 반응을 얻고 있다.",
                "%s가 글로벌 시장에서 K푸드의 우수성을 알리는 데 기여하고 있다. 현지 입맛에 맞춘 제품 개발로 해외 진출을 확대하고 있다."
            };
        } else if (companyName.contains("오리온")) {
            return new String[]{
                "%s가 프리미엄 과자 시장에서 새로운 트렌드를 제시하고 있다. 고급 원료와 독창적인 맛으로 소비자들의 까다로운 입맛을 사로잡고 있다.",
                "%s가 건강한 간식 문화 조성을 위한 제품 개발에 힘쓰고 있다. 영양과 맛을 균형있게 고려한 제품으로 시장 반응이 긍정적이다."
            };
        } else if (companyName.contains("넥슨")) {
            return new String[]{
                "%s가 게임 산업의 미래를 선도하는 혁신적인 콘텐츠를 준비하고 있다. 최신 기술을 활용한 몰입감 높은 게임으로 사용자들의 기대가 높아지고 있다.",
                "%s가 e스포츠 생태계 발전을 위한 다양한 지원 활동을 펼치고 있다. 프로게이머 육성과 대회 개최를 통해 게임 문화 확산에 기여하고 있다."
            };
        } else if (companyName.contains("배스킨")) {
            return new String[]{
                "%s가 계절별 특색을 살린 아이스크림 개발에 주력하고 있다. 독창적인 맛과 디자인으로 고객들에게 새로운 즐거움을 선사하고 있다.",
                "%s가 고객 맞춤형 서비스 확대를 통해 특별한 경험을 제공하고 있다. 개인의 취향을 반영한 제품 서비스로 브랜드 충성도를 높이고 있다."
            };
        } else if (companyName.contains("코카콜라")) {
            return new String[]{
                "%s가 건강한 음료 문화 조성을 위한 제품 다양화에 나서고 있다. 소비자의 웰빙 트렌드에 맞춘 음료 개발로 시장 점유율을 확대하고 있다.",
                "%s가 환경 보호를 위한 지속가능한 포장재 사용을 확대하고 있다. 친환경 경영을 통해 사회적 책임을 다하며 브랜드 가치를 높이고 있다."
            };
        } else {
            return new String[]{
                "%s가 고객 중심의 서비스 혁신을 통해 시장에서의 경쟁력을 강화하고 있다. 고객 만족도 향상을 위한 지속적인 노력이 성과를 거두고 있다.",
                "%s가 디지털 기술을 활용한 업무 효율성 개선에 투자하고 있다. 혁신적인 시스템 도입으로 고객에게 더 나은 서비스를 제공하고 있다."
            };
        }
    }
}
