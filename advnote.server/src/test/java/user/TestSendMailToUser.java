package user;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;

import com.advnote.server.common.MailManagement;
import com.advnote.server.model.usuario.Usuario;
import com.advnote.server.util.AES;

public class TestSendMailToUser {

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
			
			MailManagement mm = new MailManagement();
			
			mm.sendMailUnauthorized(usuario);
		} catch (Exception e) {
			System.out.println("Erro: " + e.getMessage());
		}
	}

}
