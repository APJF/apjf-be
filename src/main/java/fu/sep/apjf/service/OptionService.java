package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.OptionRequestDto;
import fu.sep.apjf.dto.response.OptionResponseDto;
import fu.sep.apjf.entity.Option;
import fu.sep.apjf.entity.Question;
import fu.sep.apjf.mapper.OptionMapper;
import fu.sep.apjf.repository.OptionRepository;
import fu.sep.apjf.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OptionService {

    private final OptionRepository optionRepository;
    private final QuestionRepository questionRepository;
    private final OptionMapper optionMapper;



    public OptionResponseDto updateOption(String id, OptionRequestDto dto) {
        Option option = optionRepository.findById(id).orElseThrow();
        option.setContent(dto.content());
        option.setIsCorrect(dto.isCorrect());
        return optionMapper.toDto(optionRepository.save(option));
    }

    public void deleteOption(String id) {
        optionRepository.deleteById(id);
    }

}