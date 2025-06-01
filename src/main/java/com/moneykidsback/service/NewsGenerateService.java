package com.moneykidsback.service;

import com.moneykidsback.model.dto.request.NewsSaveDto;
import com.moneykidsback.model.dto.request.RateForAiNewsDto;
import com.moneykidsback.model.entity.Article;
import com.moneykidsback.model.entity.Stock;
import com.moneykidsback.repository.ArticleRepository;
import com.moneykidsback.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsGenerateService {

    private final OpenAiService openAiService;
    private final StockRepository stockRepository;
    private final ArticleRepository articleRepository;

    public NewsGenerateService(OpenAiService openAiService, StockRepository stockRepository, ArticleRepository articleRepository) {
        this.openAiService = openAiService;
        this.stockRepository = stockRepository;
        this.articleRepository = articleRepository;
    }

    // 예시 메소드: 주식별 경제기사 자동 생성!
    public List<String> generateAndSaveNewsForAllStocks() throws InterruptedException {
        List<RateForAiNewsDto> stocks = stockRepository.findChangeRateForAiNews();
        List<String> articles = new ArrayList<>();
        int maxRetry = 5;

        for (RateForAiNewsDto stock : stocks) {
            int stockID = stock.getCode(); // 주식 ID
            String prompt = makePromptForStock(stock);
            String article = null;

            for (int retry = 0; retry < maxRetry; retry++) {
                try {
                    article = openAiService.getChatCompletionSync(prompt);
                    // 기사 파싱 (ex: [호재], 제목, 본문)
                    NewsSaveDto parsedArticle = parse(article);
                    // DB에 저장
                    saveArticleToDb(stockID, parsedArticle);
                    articles.add(article);
                    Thread.sleep(2000); // 2초대기(API 호출 제한 방지)
                    break; // 성공 시 반복문 탈출
                } catch (WebClientResponseException.TooManyRequests e) {
                    System.out.println("429 에러. " + (retry + 1) + "번째 재시도, 1분 대기");
                    Thread.sleep(60000); // 1분 대기
                }
            }

            // 만약 maxRetry 만큼 실패했으면 로그로 남기거나, 실패 데이터 따로 관리해도 됨!
            if (article == null) {
                System.out.println("기사 생성 실패(최대 재시도 도달): " + stock.getName());
            }
        }
        return articles;
    }


    // 프롬프트 만드는 함수 (ex: 악재/호재 자동 분기)
    private String makePromptForStock(RateForAiNewsDto stock) {
        return String.format(
                "%s 회사(종목: %s, 변동률: %s%%)에 대해 경제·기술 뉴스 기사를 200자 내외로 써줘.\n" +
                        "- 기사 첫 줄은 '[호재/악재/중립] 제목' 형식.\n" +
                        "- 본문은 초등학생도 이해할 수 있는 쉬운 말로, 편향·감정 없이 작성.\n" +
                        "- **변동률 의 크기에 따라 더 심각한 이슈**를 다뤄줘.\n" +
                        "- 반드시 매번 **완전히 새로운 주제**와 **관점**으로, 실제로 있을 법한 최신 뉴스처럼 써.\n" +
                        "- 이전 기사에서 다뤘던 이슈/단어/표현/예시는 절대 반복하지 마.\n" +
                        "- 예시: 신제품 출시, 해외진출, 관세부과, CEO 교체, 신기술 도입, 사회공헌, 환경정책, 경쟁사 동향 등 실존/가상 소재를 매번 다르게 섞어 써.\n" +
                        "- 이번 기사에만 등장하는 상징적 사건·숫자·배경·비유도 추가해.\n" +
                        "- 매번 기사 소재를 랜덤하게 골라서, 이전 기사들과 한 번도 겹치지 않게 작성해." +
                        "- 주가에 대한 언급은 절대 금지. 주식 시장에 영향을 줄 수 있는 회사의 최근 소식에 집중해줘.\n",
                stock.getName(),
                stock.getCategory(),
                stock.getChangeRate()
        );

    }

    private void saveArticleToDb(int stock, NewsSaveDto article) {
        // 리포지토리의 저장 메소드
        Article ArticleEntity = new Article();

        Stock stockEntity = stockRepository.findByID(stock);

        ArticleEntity.setStockId(stockEntity);
        ArticleEntity.setEffect(article.getEffect());
        ArticleEntity.setTitle(article.getTitle());
        ArticleEntity.setContent(article.getContent());

        articleRepository.save(ArticleEntity);
        System.out.printf("%s번 기사 저장됨.", stockEntity);
    }


    // 기사 파싱 함수 (ex: [호재], 제목, 본문)
    public static NewsSaveDto parse(String articles) {
        String[] lines = articles.split("\\r?\\n", 2); // 개행 기준으로 두 덩이만 나눔
        String firstLine = lines.length > 0 ? lines[0] : "";
        String content = lines.length > 1 ? lines[1].trim() : "";

        // 영향과 제목 분리
        // 실제로는 DB에서 주식 ID를 조회해야 함. 여기서는 예시로 0으로 설정.
        String effect = "";
        String title = firstLine;

        if (firstLine.startsWith("[")) {
            int closeIdx = firstLine.indexOf("]");
            if (closeIdx != -1) {
                effect = firstLine.substring(1, closeIdx); // [] 사이
                title = firstLine.substring(closeIdx + 1).trim(); // 뒤의 제목
            }
        }
        return new NewsSaveDto( // 주식 ID로 Stock 객체 생성
                effect, // 영향
                title, // 제목
                content // 본문
        );
    }
}
