package org.apache.struts2.showcase.hangman;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

public class GetUpdatedHangmanAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 5506025785406043027L;
	
	private Map session;
	private Hangman hangman;
	
	
	public String execute() throws Exception {
		hangman = (Hangman) session.get(HangmanConstants.HANGMAN_SESSION_KEY);
		
		System.out.println("\n\n\n");
		System.out.println("hangman="+hangman);
		System.out.println("available = "+hangman.getCharactersAvailable().size());
		System.out.println("guess left="+hangman.guessLeft());
		System.out.println("\n\n\n");
		
		return SUCCESS;
	}

	public void setSession(Map session) {
		this.session = session;
	}
	
	public Hangman getHangman() {
		return hangman;
	}
	public void setHangman(Hangman hangman) {
		this.hangman = hangman;
	}
}
