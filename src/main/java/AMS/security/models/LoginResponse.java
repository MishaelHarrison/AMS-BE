package AMS.security.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class LoginResponse {

    private String jwt;
    private String message;

    private LoginResponse(){}

    public static LoginResponse withMessage(String message){
        LoginResponse ret = new LoginResponse();
        ret.message = message;
        return ret;
    }
    public static LoginResponse withToken(String token){
        LoginResponse ret = new LoginResponse();
        ret.jwt = token;
        return ret;
    }

}
