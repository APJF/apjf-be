package fu.sep.apjf.controller;

import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.entity.Topic;
import fu.sep.apjf.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
@Slf4j
public class TopicController {

    private final TopicService topicService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<Topic>>> getAllTopics() {
        List<Topic> topics = topicService.getAllTopics();
        return ResponseEntity.ok(ApiResponseDto.ok("Lấy danh sách topics thành công", topics));
    }
}
