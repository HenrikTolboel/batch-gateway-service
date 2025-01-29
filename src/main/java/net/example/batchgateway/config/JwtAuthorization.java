package net.example.batchgateway.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// This annotation type is used to annotate service endpoints with required Roles and audiences.
///
/// The roles and audiences should be setup and used as annotation header for the request in JWT format.
///
/// The annotations are used in the JwtAuthorizationFilter class to decide if a service call is allowed or not.
///
/// * **oneOfRoles**                  One of the roles (strings) specified must be in the JwtToken sent to the service
/// * **allRoles**                    All the roles (strings) specified must be in the JwtToken sent to the service
/// * **audience**                    One of the audiences (strings) specified must be in the JwtToken sent to the service
/// * **acceptVerifiedOnlyToken**     When no roles are specified (in the above) the authorization header Bearer JwtToken
///                                   is giving access to the service when it is supplied and can be verified
/// * **acceptNoAuthorizationHeader** Specifies whether not specifying an authorization header with Bearer
///                                   token gives access or not
/// * **comments**                    Can be used to describe the settings given
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JwtAuthorization {
    String[] oneOfRoles() default "";

    String[] allRoles() default "";

    String[] audience() default "";

    boolean acceptVerifiedOnlyToken() default false;

    boolean acceptNoAuthorizationHeader() default false;

    String[] comments() default "";
}
