package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.response.UserResponseDto;
import fu.sep.apjf.entity.User;
import org.mapstruct.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "authorities", source = "authorities", qualifiedByName = "mapAuthorities")
    UserResponseDto toDto(User user);

    @Named("mapAuthorities")
    default List<String> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        if (authorities == null || authorities.isEmpty()) {
            return List.of();
        }
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }
}
