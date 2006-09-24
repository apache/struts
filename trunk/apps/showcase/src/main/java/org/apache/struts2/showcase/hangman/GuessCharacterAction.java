package org.apache.struts2.showcase.hangman;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

public class GuessCharacterAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 9050915577007590674L;
	
	private Map session;
	private Character character;
	private Hangman hangman;
	
	public String execute() throws Exception {
		hangman = (Hangman) session.get(HangmanConstants.HANGMAN_SESSION_KEY);
		hangman.guess(character);
		
		return SUCCESS;
	}
	
	public Hangman getHangman() {
		return hangman;
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
