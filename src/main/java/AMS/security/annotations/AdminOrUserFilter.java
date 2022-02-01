package AMS.security.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminOrUserFilter {
}