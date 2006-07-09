package org.apache.struts2.showcase.hangman;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork.ActionSupport;

public class GuessCharacterAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 9050915577007590674L;
	
	private Map session;
	private Character character;
	
	
	public String execute() throws Exception {
		Hangman hangman = (Hangman) session.get(HangmanConstants.HANGMAN_SESSION_KEY);
		hangman.guess(character);
		
		System.out.println("\n\n\n");
		System.out.println("character="+character);
		System.out.println("available="+hangman.getCharactersAvailable().size());
		System.out.println("\n\n\n");
		
		return SUCCESS;
	}

	public void setSession(Map session) {
		this.session = session;
	}
	
	public void setCharacter(Character character) {
		this.character = character;
	}
	
	public Character getCharacter() {
		return this.character;
	}
}
