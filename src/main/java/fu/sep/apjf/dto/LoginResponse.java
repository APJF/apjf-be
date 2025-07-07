package fu.sep.apjf.dto;

import java.util.List;

public record LoginResponse(String username, String avatar, List<String> roles, String jwtToken) {
}
