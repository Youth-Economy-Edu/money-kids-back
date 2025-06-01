package com.moneykidsback.service;

import com.moneykidsback.model.dto.request.NewSaveDto;
import com.moneykidsback.model.dto.request.RateForAiNewsDto;
import com.moneykidsback.model.entity.Stock;
import com.moneykidsback.repository.ArticleRepository;
import com.moneykidsback.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
            String prompt = makePromptForStock(stock);
            String article = null;

            for (int retry = 0; retry < maxRetry; retry++) {
                try {
                    article = openAiService.getChatCompletionSync(prompt);
                    // 기사 파싱 (ex: [호재], 제목, 본문)
                    NewSaveDto parsedArticle = parse(article);
                    // DB에 저장
                    saveArticleToDb(stock, parsedArticle);
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
                "다음 변동률을 참고해서 %s 회사에 대한 경제·기술 뉴스 기사를 200자 내외로 써줘." +
                        "- 기사 첫 줄은 '[호재/악재/영향없음] 제목' 형식." +
                        "- 본문은 초등학생도 이해할 수 있는 쉬운 말로, 편향과 감정 없이 작성." +
                        "- 주제는 변동률 자체가 아니라, 변동을 일으킨 회사의 최근 소식이야." +
                        "변동률: %s",
                stock.getName(),
                stock.getChangeRate()
        );
    }

    private void saveArticleToDb(RateForAiNewsDto stock, NewSaveDto article) {
        // 리포지토리의 저장 메소드
//        articleRepository.save(article);
        System.out.println("Saving article for stock: " + stock.getName());
    }


    // 기사 파싱 함수 (ex: [호재], 제목, 본문)
    public static NewSaveDto parse(String articles) {
        String[] lines = articles.split("\\r?\\n", 2); // 개행 기준으로 두 덩이만 나눔
        String firstLine = lines.length > 0 ? lines[0] : "";
        String content = lines.length > 1 ? lines[1].trim() : "";

        // 영향과 제목 분리
        String effect = "";
        String title = firstLine;

        if (firstLine.startsWith("[")) {
            int closeIdx = firstLine.indexOf("]");
            if (closeIdx != -1) {
                effect = firstLine.substring(1, closeIdx); // [] 사이
                title = firstLine.substring(closeIdx + 1).trim(); // 뒤의 제목
            }
        }
        return new NewSaveDto(effect, title, content);
    }
}
