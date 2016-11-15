package com.example.uhfxintong.eventbus;

public class NoteListener {
	private String content;
	private int position;
	public NoteListener(String content, int position) {
		super();
		this.content = content;
		this.position = position;
	}
	public String getContent() {
		return content;
	}
	public int getPosition() {
		return position;
	}
	
}
