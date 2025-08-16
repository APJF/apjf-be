package fu.sep.apjf.service;

import fu.sep.apjf.entity.Topic;
import fu.sep.apjf.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicService {

    private final TopicRepository topicRepository;

    /**
     * Lấy danh sách tất cả các topic
     */
    public List<Topic> getAllTopics() {
        log.info("Lấy danh sách tất cả topics");
        List<Topic> topics = topicRepository.findAll();
        log.info("Tìm thấy {} topics", topics.size());
        return topics;
    }
}
