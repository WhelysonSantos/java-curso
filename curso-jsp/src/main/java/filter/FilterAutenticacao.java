package filter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import connection.SingleConnectionBanco;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@WebFilter(urlPatterns = {"/principal/*"}) // Interceptas todas as requisições que vierem do projeto ou mapeamento
public class FilterAutenticacao extends HttpFilter implements Filter {
	
	private static Connection connection;
  
	private static final long serialVersionUID = 1L;

	public FilterAutenticacao() {
        
    }
    
    // Encerra os processos quando o servidor é parado
    // Mataria os processos de conxão com o banco
	public void destroy() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Intercepta as requisições e as respostas no sistema
	// Tudo que fizermos no sistema vai passar por ele doFIlter
	// Validação de autenticação
	// Dar commit ou rolback de transações do banco
	// Validar e fazer redirecionamento de páginas
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
	
		try {
		
			HttpServletRequest req = (HttpServletRequest) request; // Conversão
			HttpSession session = req.getSession();
			
			String usuarioLogado = (String) session.getAttribute("usuario"); // Conversão
			
			String urlParaAutenticar = req.getServletPath(); // Url que está sendo acessada
			
			// Validar se está logado se não redireciona para tela de login
			if (usuarioLogado == null &&
					!urlParaAutenticar.equalsIgnoreCase("/principal/ServletLogin")) { // Não está logado
				
				RequestDispatcher redireciona = request.getRequestDispatcher("/index.jsp?url=" + urlParaAutenticar);// Passando um paramétro
				request.setAttribute("msg", "Por favor realize o login");
				redireciona.forward(request, response); // Comando de redirecionamento
				return; // Para a execução e redireciona para o login
				
			}else {
				chain.doFilter(request, response);
			}
			
			connection.commit(); // Deu tudo certo commita as alterações no banco de dados 
		
		}catch (Exception e) {
			e.printStackTrace();
			
			RequestDispatcher redirecionar = request.getRequestDispatcher("erro.jsp");
			request.setAttribute("msg", e.getMessage());
			redirecionar.forward(request, response); 
			
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		
	}

	// Inicia os processos ou recurssos quando o servidor sobe o projeto
	// Iniciar a conexão com o banco
	public void init(FilterConfig fConfig) throws ServletException {
		connection = SingleConnectionBanco.getConnection();
	}

}
