package user;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;

import com.advnote.server.model.usuario.Usuario;
import com.advnote.server.model.usuario.UsuarioDAO;
import com.advnote.server.util.AES;

public class TestSaveUsuarioWithSucess {

	@Test
	public void test() {
		try {
			Usuario usuario = new Usuario();
			
			usuario.setNmUsuario("Antonio Salviano");
			usuario.setDsEmail("salvianojrb@hotmail.com");
			usuario.setDsToken(UUID.randomUUID().toString().replace("-", ""));
			AES aes = new AES();
			usuario.setDsSenha(aes.encrypt(usuario.getDsToken(), "assj78"));
			usuario.setDtCriacao(new Date());
			usuario.setDtUltimoAcesso(new Date());
			usuario.setSnAtivo("N");
			
			UsuarioDAO usuarioDao = new UsuarioDAO();
			
			usuarioDao.save(usuario);
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

}
