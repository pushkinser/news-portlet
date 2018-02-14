package ru.news.service.Impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import org.springframework.stereotype.Service;
import ru.news.comparator.JournalArticleDTOComparator;
import ru.news.mapper.JournalArticleMap;
import ru.news.model.JournalArticleDTO;
import ru.news.service.JournalArticleService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class JournalArticleServiceImpl implements JournalArticleService {

    private static final int JOURNAL_ARTICLE_STATUS_APPROVED = 0;

    @Override
    public List<JournalArticleDTO> getJournalArticlesLatestVersion() {
        List<JournalArticleDTO> journalArticleDTOS = JournalArticleMap.toDto(getLatestVersionJA());
        journalArticleDTOS.sort(new JournalArticleDTOComparator().reversed());
        return journalArticleDTOS;
    }

    @Override
    public JournalArticleDTO getJournalArticleLatestVersion(long groupId, String articleId) {
        JournalArticle latestVersion = getLatestVersion(groupId, articleId);
        return JournalArticleMap.toDto(latestVersion);
    }

    @Override
    public List<JournalArticleDTO> getJournalArticleByTag(String tag) {
        List<JournalArticleDTO> journalArticleDTOS = getJournalArticlesLatestVersion();
        List<JournalArticleDTO> articleDTOListWithTag = new ArrayList<>();
        for (JournalArticleDTO journalArticleDTO : journalArticleDTOS) {
            if (journalArticleDTO.getTags().contains(tag)) {
                articleDTOListWithTag.add(journalArticleDTO);
            }
        }
        return articleDTOListWithTag;
    }

    @Override
    public List<JournalArticleDTO> getJournalArticleByCategory(String category) {
        List<JournalArticleDTO> journalArticleDTOS = getJournalArticlesLatestVersion();
        List<JournalArticleDTO> articleDTOListWithCategory = new ArrayList<>();
        for (JournalArticleDTO journalArticleDTO : journalArticleDTOS) {
            if (journalArticleDTO.getCategory().contains(category)) {
                articleDTOListWithCategory.add(journalArticleDTO);
            }
        }
        return articleDTOListWithCategory;
    }

    private JournalArticle getLatestVersion(long groupId, String articleId) {
        double latestVersion;
        JournalArticle journalArticle = null;
        try {
            latestVersion = JournalArticleLocalServiceUtil.getLatestVersion(groupId, articleId);
            journalArticle = JournalArticleLocalServiceUtil.getArticle(groupId, articleId, latestVersion);
        } catch (PortalException | SystemException e) {
            e.printStackTrace();
        }
        return journalArticle;
    }

    private List<JournalArticle> getLatestVersionJA() {
        HashMap<String, JournalArticle> journalArticleHashMap = new HashMap<>();
        try {
            for (JournalArticle journalArticle : JournalArticleLocalServiceUtil.getArticles()) {
                String articleId = journalArticle.getArticleId();

                if (!journalArticle.isInTrash()) {
                    if (journalArticle.getStatus() == JOURNAL_ARTICLE_STATUS_APPROVED)
                    if (!journalArticleHashMap.containsKey(articleId)) {
                        journalArticleHashMap.put(articleId, journalArticle);
                    }
                }
            }
        } catch (SystemException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(journalArticleHashMap.values());
    }

}
