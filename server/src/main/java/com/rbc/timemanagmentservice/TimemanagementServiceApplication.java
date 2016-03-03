package com.rbc.timemanagmentservice;

import com.rbc.timemanagmentservice.util.StartupUtility;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.MathTool;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.WebContentInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

@SpringBootApplication
public class TimemanagementServiceApplication {
    private static final Logger LOG = LoggerFactory.getLogger(TimemanagementServiceApplication.class);

    @Autowired
    private Environment environment;

    @Autowired
    private PlatformTransactionManager transactionManager;

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

            @Override
            public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
                configurer.enable();
            }

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(webContentInterceptor());
            }
        };


    }


    @Bean
    @Transactional(propagation = Propagation.REQUIRED)
    public CommandLineRunner demo(StartupUtility startupUtility) {
        return (args) -> {
            if(environment.getActiveProfiles().length != 0 && Arrays.asList(environment.getActiveProfiles()).contains("demo")){
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


    @Bean
    public WebContentInterceptor webContentInterceptor() {
        WebContentInterceptor interceptor = new WebContentInterceptor();
        interceptor.setCacheSeconds(0);
//        interceptor.setUseExpiresHeader(true);
//        interceptor.setUseCacheControlHeader(true);
//        interceptor.setUseCacheControlNoStore(true);

        return interceptor;
    }

    @Bean
    public InternalResourceViewResolver getInternalResourceViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    //------------------- Velocity
    @Bean
    public VelocityEngine velocityEngine() throws VelocityException, IOException {
        VelocityEngineFactoryBean factory = new VelocityEngineFactoryBean();
        Properties props = new Properties();
        props.put("resource.loader", "class");
        props.put("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader." +
                        "ClasspathResourceLoader");
        props.put("date",new DateTool());
        factory.setVelocityProperties(props);
        return factory.createVelocityEngine();
    }

    @Bean
    public VelocityContext velocityContext() {
        final VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("math", new MathTool());
        return velocityContext;
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

