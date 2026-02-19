package filter;


import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

@WebFilter("/*") // すべてのリクエストを対象
public class AccessLogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("[AccessLog] Filter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        long start = System.currentTimeMillis();
        String uri = req.getRequestURI();
        String query = req.getQueryString();
        String method = req.getMethod();

        System.out.println(String.format("[AccessLog] --> %s %s%s",
                method, uri, (query != null ? "?" + query : "")));

        try {
            chain.doFilter(request, response); // 次へ
        } finally {
            long took = System.currentTimeMillis() - start;
            System.out.println(String.format("[AccessLog] <-- %s %s (%d ms)",
                    method, uri, took));
        }
    }

    @Override
    public void destroy() {
        System.out.println("[AccessLog] Filter destroy");
    }
}
