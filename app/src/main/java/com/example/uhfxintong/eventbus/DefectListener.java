package com.example.uhfxintong.eventbus;

public class DefectListener {
	private String content;
	private String photoPaths;
	private int position;
	public DefectListener(String content, String photoPaths, int position) {
		super();
		this.content = content;
		this.photoPaths = photoPaths;
		this.position = position;
	}
	public String getContent() {
		return content;
	}
	public String getPhotoPaths() {
		return photoPaths;
	}
	public int getPosition() {
		return position;
	}
	
}
