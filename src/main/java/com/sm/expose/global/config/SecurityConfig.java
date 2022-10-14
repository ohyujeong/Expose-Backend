package com.sm.expose.global.config;

import com.sm.expose.global.security.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//@Configuration
//@RequiredArgsConstructor
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(
//        securedEnabled = true,
//        jsr250Enabled = true,
//        prePostEnabled = true
//)
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//    private final UserDetailsServiceImpl customUserDetailsService;
//    private final CustomOAuth2UserService customOAuth2UserService;
//    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
//    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
//
//    @Bean
//    public TokenAuthenticationFilter tokenAuthenticationFilter() {
//        return new TokenAuthenticationFilter();
//    }
//
//    /**
//     * JWT를 사용하면 Session에 저장하지 않고 Authorization Request를 Based64 encoded cookie에 저장
//     */
//    @Bean
//    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository(){
//        return new HttpCookieOAuth2AuthorizationRequestRepository();
//    }
//
//    @Bean
//    PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
//
//    /**
//     * Authrization에 사용할 userDetailsService와 PasswordEncode 정의
//     */
//    @Override
//    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
//        builder
//                .userDetailsService(customUserDetailsService)
//                .passwordEncoder(passwordEncoder());
//    }
//
//    /**
//     * AhthenticationManager를 외부에서 사용하기 위해 @Bean 설정으로
//     * Spring Security 밖으로 추출
//     */
//    @Bean(BeanIds.AUTHENTICATION_MANAGER)
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .cors() //cors 허용
//                .and()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //Session 비활성화
//                .and()
//                .csrf().disable() //csrf 비활성화
//                .formLogin().disable() //로그인폼 비활성화
//                .httpBasic().disable() //기본 로그인 창 비활성화
//                .authorizeRequests()
//                .antMatchers("/").permitAll()
//                .antMatchers("/api/**").hasAnyRole(Role.GUEST.name(), Role.USER.name())
//                .antMatchers("/auth/**", "oauth2/**").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .oauth2Login()
//                .authorizationEndpoint()
//                .baseUri("/oauth2/authorization") //클라이언트 첫 로그인 URI
//                .authorizationRequestRepository(cookieAuthorizationRequestRepository())
//                .and()
//                .userInfoEndpoint()
//                .userService(customOAuth2UserService)
//                .and()
//                .successHandler(oAuth2AuthenticationSuccessHandler);
//        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//    }
//}
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//    private final CustomOAuth2UserService customOAuth2UserService;
//    private final OAuth2SuccessHandler oAuth2SuccessHandler;
//    private final TokenAuthenticationFilter tokenAuthenticationFilter;
//    private final TokenProvider tokenProvider;
//
//
//    public SecurityConfig(@Lazy CustomOAuth2UserService customOAuth2UserService, OAuth2SuccessHandler oAuth2SuccessHandler, TokenAuthenticationFilter tokenAuthenticationFilter, TokenProvider tokenProvider) {
//        this.customOAuth2UserService = customOAuth2UserService;
//        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
//        this.tokenAuthenticationFilter = tokenAuthenticationFilter;
//        this.tokenProvider = tokenProvider;
//    }
//
//    //AuthenticationManagerBean 등록 -> 하단의 configure에서 LoginForm을 이용한 자동 처리 사용 X,
//    // 수동으로 Authentication을 만들어서 SecurityContext에 저장
//    @Bean
//    @Override
//    public AuthenticationManager authenticationManagerBean()  throws Exception{
//        return super.authenticationManagerBean();
//    }
//
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationSuccessHandler oAuth2SuccessHandler() {
//        SimpleUrlAuthenticationSuccessHandler handler = new OAuth2SuccessHandler(tokenProvider);
//        handler.setUseReferer(true);
//        return handler;
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .cors().and()
//                .csrf().disable()
//                .headers().frameOptions().disable()
//                .and()
//                .authorizeRequests()
//                .antMatchers("/**").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .oauth2Login()
//                .userInfoEndpoint()// 로그인 성공 후 사용자 정보를 가져옴
//                .userService(customOAuth2UserService)// userInfoEndpoint()로 가져온 사용자 정보를 처리할 때 사용
//                .and()
//                .successHandler(oAuth2SuccessHandler())
//                .and()
//                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//    }
//
//}
@EnableWebSecurity // spring security 설정을 활성화시켜주는 어노테이션
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService oAuthService;

    public SecurityConfig(CustomOAuth2UserService oAuthService) {
        this.oAuthService = oAuthService;
    }

        //AuthenticationManagerBean 등록 -> 하단의 configure에서 LoginForm을 이용한 자동 처리 사용 X,
    // 수동으로 Authentication을 만들어서 SecurityContext에 저장
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean()  throws Exception{
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.oauth2Login() // OAuth2 로그인 설정 시작점
                .userInfoEndpoint() // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때 설정 담당
                .userService(oAuthService); // OAuth2 로그인 성공 시, 후작업을 진행할 UserService 인터페이스 구현체 등록
    }
}