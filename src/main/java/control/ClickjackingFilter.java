package control;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class ClickjackingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inizializzazione del filtro (se necessario)
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("X-Frame-Options", "DENY"); // Impedisce il caricamento del sito all'interno di un frame
        httpServletResponse.setHeader("Content-Security-Policy", "frame-ancestors 'none'"); // Impedisce l'inclusione del sito in un frame
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup (se necessario)
    }
}
