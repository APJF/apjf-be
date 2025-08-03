package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.TopicDto;
import fu.sep.apjf.entity.Topic;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TopicMapper {

    TopicDto toDto(Topic topic);

    @Mapping(target = "courses", ignore = true)
    Topic toEntity(TopicDto topicDto);
}
