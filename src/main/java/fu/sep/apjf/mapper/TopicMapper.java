package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.TopicDto;
import fu.sep.apjf.entity.Topic;

public final class TopicMapper {

    private TopicMapper() {
        // Private constructor to prevent instantiation
    }

    public static TopicDto toDto(Topic topic) {
        if (topic == null) {
            return null;
        }

        return new TopicDto(topic.getId(), topic.getName());
    }

    public static Topic toEntity(TopicDto topicDto) {
        if (topicDto == null) {
            return null;
        }

        Topic topic = new Topic();
        topic.setId(topicDto.id());
        topic.setName(topicDto.name());

        return topic;
    }
}
