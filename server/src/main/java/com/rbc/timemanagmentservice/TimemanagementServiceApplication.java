package com.rbc.timemanagmentservice;

import com.rbc.timemanagmentservice.model.User;
import com.rbc.timemanagmentservice.persistence.UserRepository;
import com.rbc.timemanagmentservice.util.StartupUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@SpringBootApplication
public class TimemanagementServiceApplication {
    private static final Logger LOG = LoggerFactory.getLogger(TimemanagementServiceApplication.class);

    @Autowired
    private Environment environment;

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(TimemanagementServiceApplication.class, args);
    }


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/customers").allowedOrigins("http://localhost:8888");
            }
        };
    }


    @Bean
    @Transactional(propagation = Propagation.REQUIRED)
    public CommandLineRunner demo(StartupUtility startupUtility) {
        return (args) -> {
            if(environment.getActiveProfiles().length != 0 && Arrays.asList(environment.getActiveProfiles()).contains("runtime")){
                startupUtility.init();
            }
        };
    }
    // CORS
    @Bean
    FilterRegistrationBean corsFilter(
            @Value("${tagit.origin:http://localhost:8080}") String origin) {
        return new FilterRegistrationBean(new Filter() {
            public void doFilter(ServletRequest req, ServletResponse res,
                                 FilterChain chain) throws IOException, ServletException {
                HttpServletRequest request = (HttpServletRequest) req;
                HttpServletResponse response = (HttpServletResponse) res;
                String method = request.getMethod();
                // this origin value could just as easily have come from a database
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("Access-Control-Allow-Methods",
                        "POST,GET,OPTIONS,DELETE");
                response.setHeader("Access-Control-Max-Age", Long.toString(60 * 60));
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader(
                        "Access-Control-Allow-Headers",
                        "Origin,Accept,X-Requested-With,Content-Type," +
                        "Access-Control-Request-Method,Access-Control-Request-Headers,Authorization");
                if ("OPTIONS".equals(method)) {
                    response.setStatus(HttpStatus.OK.value());
                }
                else {
                    chain.doFilter(req, res);
                }
            }

            public void init(FilterConfig filterConfig) {
            }

            public void destroy() {
            }
        });
    }



//    @Configuration
//    class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
//
//        EmployeeRepository employeeRepository;
//
//        public WebSecurityConfiguration() {
//        }
//
//        @Override
//        public void init(AuthenticationManagerBuilder auth) throws Exception {
//            auth.userDetailsService(userDetailsService());
//        }
//
//        @Bean
//        UserDetailsService userDetailsService() {
//            return (username) -> employeeRepository
//                    .findByUsername(username)
//                    .map(a -> new User(a.getUsername(), a.getPassword(), true, true, true, true,
//                            AuthorityUtils.createAuthorityList("USER", "write")))
//                    .orElseThrow(
//                            () -> new UsernameNotFoundException("could not find the user '"
//                                    + username + "'"));
//        }
//    }

//    @Configuration
//    @EnableResourceServer
//    @EnableAuthorizationServer
//    class OAuth2Configuration extends AuthorizationServerConfigurerAdapter {
//
//        String applicationName = "bookmarks";
//
//        // This is required for password grants, which we specify below as one of the
//        // {@literal authorizedGrantTypes()}.
//        @Autowired
//        AuthenticationManagerBuilder authenticationManager;
//
//        @Override
//        public void configure(AuthorizationServerEndpointsConfigurer endpoints)
//                throws Exception {
//            // Workaround for https://github.com/spring-projects/spring-boot/issues/1801
//            endpoints.authenticationManager(new AuthenticationManager() {
//                @Override
//                public Authentication authenticate(Authentication authentication)
//                        throws AuthenticationException {
//                    return authenticationManager.getOrBuild().authenticate(authentication);
//                }
//            });
//        }
//
//        @Override
//        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//
//            clients.inMemory().withClient("android-" + applicationName)
//                    .authorizedGrantTypes("password", "authorization_code", "refresh_token")
//                    .authorities("ROLE_USER").scopes("write").resourceIds(applicationName)
//                    .secret("123456");
//        }
//    }

}

