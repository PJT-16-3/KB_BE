package org.scoula.config;

import org.scoula.security.config.SecurityConfig;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;

public class WebConfig extends AbstractAnnotationConfigDispatcherServletInitializer {
    final String LOCATION = "c:/upload";
    final long MAX_FILE_SIZE = 1024 * 1024 * 10L;
    final long MAX_REQUEST_SIZE = 1024 * 1024 *20L;
    final int FILE_SIZE_THRESHOLD = 1024 * 1024 * 5;

    @Override
    //DispatcherServlet의 동작을 커스터마이징하는 핵심 코드
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        //요청한 URL에 매핑된 컨트롤러가 없으면 → 404.html 같은 기본 에러 페이지 반환
        registration.setInitParameter("throwExceptionIfNoHandlerFound", "true");

        //파일 업로드 처리 설정
        MultipartConfigElement multipartConfig =
                new MultipartConfigElement(
                        LOCATION,  // 업로드 처리 디렉토리경로
                        MAX_FILE_SIZE, // 업로드 가능한 파일 하나의 최대크기
                        MAX_REQUEST_SIZE, //업로드 가능한 전체 최대크기(여러파일업로드하는경우)
                        FILE_SIZE_THRESHOLD // 메모리 파일의 최대크기(이보다작으면실제메모리에서만작업)
                );
        registration.setMultipartConfig(multipartConfig);
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{RootConfig.class, SecurityConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] {ServletConfig.class};
    }

    //스프링의 FrontController인 DispatcherServlet이 담당할 Url 매핑 패턴,  / : 모든 요청에 대해 매핑
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    // POSTbody문자인코딩필터설정-UTF-8설정
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter characterEncodingFilter= new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return new Filter[] {characterEncodingFilter};
    }

}
