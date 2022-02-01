package AMS.models.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@Data
@RequiredArgsConstructor
public class RoleResponse {
    @NonNull
    private String role;

    private RoleResponse() {
    }
}
