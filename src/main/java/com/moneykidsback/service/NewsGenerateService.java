package com.moneykidsback.service;

import com.moneykidsback.model.entity.Article;
import com.moneykidsback.model.entity.Stock;
import com.moneykidsback.repository.ArticleRepository;
import com.moneykidsback.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class NewsGenerateService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private OpenAiService openAiService;

    @Autowired
    private NewsBasedPriceService newsBasedPriceService;

    private final Random random = new Random();

    // 1시간마다 기사 생성 (API 토큰 절약)
    @Scheduled(fixedDelay = 3600000) // 1시간 = 3600000ms
    public void scheduledNewsGeneration() {
        System.out.println("📰 [SCHEDULED] 정기 기사 생성 시작: " + LocalDateTime.now());
        generateAndSaveNewsForAllStocks();
    }

    public List<String> generateAndSaveNewsForAllStocks() {
        List<Stock> stocks = stockRepository.findAll();
        List<String> generatedArticles = new ArrayList<>();

        for (Stock stock : stocks) {
            try {
                // 30% 확률로 기사 생성 (API 토큰 절약)
                if (random.nextDouble() < 0.3) {
                    String articleContent = generateNewsArticle(stock);
                    
                    if (articleContent != null && !articleContent.trim().isEmpty()) {
                        Article article = saveArticle(stock, articleContent);
                        generatedArticles.add(stock.getName() + ": " + article.getTitle());
                        
                        // 기사 기반 주가 변동 시작
                        newsBasedPriceService.startPriceMovementForArticle(article);
                        
                        System.out.println("📰 새 기사 생성 완료: " + stock.getName() + " - " + article.getTitle());
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ " + stock.getName() + " 기사 생성 실패: " + e.getMessage());
            }
        }

        System.out.println("📰 총 " + generatedArticles.size() + "개 기사 생성 완료");
        return generatedArticles;
    }

    private String generateNewsArticle(Stock stock) {
        try {
            // OpenAI 프롬프트 생성 - 더 자연스러운 기업 뉴스 요청
            String prompt = String.format(
                "다음 기업에 대한 자연스러운 기업 뉴스를 작성해주세요.\n\n" +
                "기업명: %s\n\n" +
                "요구사항:\n" +
                "1. 실제 기업 뉴스처럼 자연스럽게 작성 (주식, 주가, 투자자 등 직접적 표현 금지)\n" +
                "2. 기업의 사업 활동, 신제품, 서비스 개선, 사회공헌 등에 초점\n" +
                "3. 긍정적, 부정적, 중립적 내용 중 하나를 선택\n" +
                "4. 제목과 내용을 구분해서 작성\n" +
                "5. 첫 줄에 감정(긍정:/부정:/중립:)과 영향도(강함/보통/약함)를 표시\n" +
                "6. 제목은 50자 이내, 내용은 200자 내외\n" +
                "7. 업종별 특성 반영 (식품업체-신메뉴/위생, 게임업체-신작/업데이트, 소매업체-매장/서비스 등)\n\n" +
                "형식:\n" +
                "감정:영향도\n" +
                "제목: [제목 내용]\n" +
                "내용: [기사 내용]",
                stock.getName()
            );

            return openAiService.generateNewsArticle(prompt);
        } catch (Exception e) {
            System.err.println("OpenAI 기사 생성 실패: " + e.getMessage());
            return generateFallbackArticle(stock);
        }
    }

    // OpenAI 실패 시 대체 기사 생성 - 더 자연스러운 템플릿으로 개선
    private String generateFallbackArticle(Stock stock) {
        String[] sentiments = {"긍정", "부정", "중립"};
        String[] impacts = {"강함", "보통", "약함"};
        
        // 기업별 맞춤형 뉴스 템플릿
        String[] templates = getNewsTemplatesByCompany(stock.getName());
        String[] contentTemplates = getContentTemplatesByCompany(stock.getName());

        String sentiment = sentiments[random.nextInt(sentiments.length)];
        String impact = impacts[random.nextInt(impacts.length)];
        String title = String.format(templates[random.nextInt(templates.length)], stock.getName());
        String content = String.format(contentTemplates[random.nextInt(contentTemplates.length)], stock.getName());

        return String.format("%s:%s\n제목: %s\n내용: %s", sentiment, impact, title, content);
    }
    
    // 기업별 맞춤형 뉴스 제목 템플릿
    private String[] getNewsTemplatesByCompany(String companyName) {
        if (companyName.contains("레고")) {
            return new String[]{
                "%s, 새로운 테마 세트 출시 예정",
                "%s, 창의력 교육 프로그램 확대",
                "%s, 친환경 소재 개발 노력",
                "%s, 글로벌 전시회 참가 소식",
                "%s, 디지털 체험 공간 오픈"
            };
        } else if (companyName.contains("포켓몬")) {
            return new String[]{
                "%s, 신규 카드 시리즈 발표",
                "%s, 수집가를 위한 특별판 출시",
                "%s, 게임과 연동된 새로운 체험",
                "%s, 팬 커뮤니티 이벤트 개최",
                "%s, 어린이 교육 콘텐츠 제작"
            };
        } else if (companyName.contains("맥도날드")) {
            return new String[]{
                "%s, 신메뉴 버거 시리즈 출시",
                "%s, 매장 리뉴얼 프로젝트 진행",
                "%s, 건강한 식단 옵션 확대",
                "%s, 지역 농산물 활용 확대",
                "%s, 디지털 주문 시스템 개선"
            };
        } else if (companyName.contains("스타벅스")) {
            return new String[]{
                "%s, 시즌 한정 음료 출시",
                "%s, 친환경 매장 운영 확대",
                "%s, 바리스타 교육 프로그램 강화",
                "%s, 지역 특화 메뉴 개발",
                "%s, 고객 경험 개선 서비스 도입"
            };
        } else if (companyName.contains("농심") || companyName.contains("오리온")) {
            return new String[]{
                "%s, 건강 지향 신제품 라인업 공개",
                "%s, 해외 시장 진출 확대",
                "%s, 품질 관리 시스템 업그레이드",
                "%s, 지속가능한 포장재 도입",
                "%s, 소비자 취향 분석 연구 발표"
            };
        } else if (companyName.contains("넥슨")) {
            return new String[]{
                "%s, 신작 모바일 게임 개발 발표",
                "%s, 게임 업데이트 및 이벤트 공개",
                "%s, e스포츠 대회 후원 확대",
                "%s, 게임 개발자 육성 프로그램 론칭",
                "%s, 글로벌 게임 퍼블리싱 강화"
            };
        } else {
            return new String[]{
                "%s, 고객 서비스 품질 향상 계획",
                "%s, 디지털 혁신 프로젝트 시작",
                "%s, 사회공헌 활동 확대 발표",
                "%s, 새로운 브랜드 전략 공개",
                "%s, 운영 효율성 개선 추진"
            };
        }
    }
    
    // 기업별 맞춤형 뉴스 내용 템플릿
    private String[] getContentTemplatesByCompany(String companyName) {
        if (companyName.contains("레고")) {
            return new String[]{
                "%s가 창의적인 놀이 문화 확산을 위한 새로운 시도를 하고 있다. 어린이들의 상상력 발달에 도움이 되는 혁신적인 제품으로 주목받고 있다.",
                "%s가 교육적 가치를 높인 제품 개발에 집중하고 있다. 놀이를 통한 학습 효과를 극대화하는 방향으로 사업을 확장하고 있다."
            };
        } else if (companyName.contains("맥도날드")) {
            return new String[]{
                "%s가 고객 만족도 향상을 위한 메뉴 다양화에 나서고 있다. 건강하고 맛있는 옵션을 늘려 더 많은 고객층의 니즈를 충족시키고자 한다.",
                "%s가 매장 환경 개선과 서비스 품질 향상에 투자하고 있다. 고객들이 더욱 편안하게 이용할 수 있는 공간 조성에 집중하고 있다."
            };
        } else if (companyName.contains("농심") || companyName.contains("오리온")) {
            return new String[]{
                "%s가 소비자 건강을 고려한 제품 개발에 힘쓰고 있다. 맛과 영양을 동시에 만족시키는 혁신적인 제품으로 시장에서 긍정적인 반응을 얻고 있다.",
                "%s가 지속가능한 경영을 위한 환경친화적 생산 방식을 도입하고 있다. 이를 통해 사회적 책임을 다하며 브랜드 가치를 높여가고 있다."
            };
        } else if (companyName.contains("넥슨")) {
            return new String[]{
                "%s가 게임 산업의 새로운 트렌드를 선도하는 콘텐츠를 선보이고 있다. 혁신적인 게임플레이와 스토리텔링으로 사용자들의 큰 관심을 받고 있다.",
                "%s가 글로벌 게임 시장에서의 경쟁력 강화를 위한 다양한 전략을 추진하고 있다. 최신 기술을 활용한 게임 개발로 시장 점유율 확대를 노리고 있다."
            };
        } else {
            return new String[]{
                "%s가 고객 중심의 서비스 개선에 지속적으로 노력하고 있다. 시장 변화에 발빠르게 대응하며 경쟁력을 유지하고 있다.",
                "%s가 혁신적인 기술 도입을 통해 업무 효율성을 높이고 있다. 이를 통해 더 나은 서비스를 제공하고자 하는 의지를 보여주고 있다."
            };
        }
    }

    private Article saveArticle(Stock stock, String articleContent) {
        // 기사 내용 파싱
        String[] lines = articleContent.split("\n");
        String sentimentAndImpact = lines[0];
        String title = lines[1].replace("제목: ", "");
        String content = lines[2].replace("내용: ", "");

        // 감정과 영향도 분석
        String[] parts = sentimentAndImpact.split(":");
        String sentiment = parts[0];
        String impact = parts.length > 1 ? parts[1] : "보통";

        // 목표 등락률 계산 (±3% 제한)
        double targetChangeRate = calculateTargetChangeRate(sentiment, impact);

        Article article = new Article();
        // ID는 자동생성되므로 설정하지 않음
        article.setStockId(stock.getId());
        article.setTitle(title);
        article.setContent(content);
        article.setEffect(String.valueOf(targetChangeRate));
        article.setDate(LocalDateTime.now().toString());
        article.setSentiment(sentiment);
        article.setImpact(impact);

        Article savedArticle = articleRepository.save(article);
        System.out.println("✅ " + stock.getName() + " 기사 저장 완료: " + title);
        return savedArticle;
    }

    private double calculateTargetChangeRate(String sentiment, String impact) {
        double baseRate = 0;
        
        // 감정에 따른 기본 등락률 (소폭 변동)
        switch (sentiment) {
            case "긍정":
                baseRate = 1.5; // +1.5%
                break;
            case "부정":
                baseRate = -1.5; // -1.5%
                break;
            case "중립":
                baseRate = 0.0; // 0%
                break;
            default:
                baseRate = random.nextGaussian() * 0.5; // 랜덤 ±0.5%
        }

        // 영향도에 따른 배율 조정 (더 보수적으로)
        double multiplier = 1.0;
        switch (impact) {
            case "강함":
                multiplier = 2.0; // 2배
                break;
            case "보통":
                multiplier = 1.0; // 1배
                break;
            case "약함":
                multiplier = 0.5; // 0.5배
                break;
        }

        double targetRate = baseRate * multiplier;
        
        // 최대 ±3% 제한
        return Math.max(-3.0, Math.min(3.0, targetRate));
    }
}
