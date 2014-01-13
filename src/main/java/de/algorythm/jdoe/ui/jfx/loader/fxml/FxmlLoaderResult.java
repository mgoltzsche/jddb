package de.algorythm.jdoe.ui.jfx.loader.fxml;

public class FxmlLoaderResult<N, C> {

	private N node;
	private C controller;
	
	public FxmlLoaderResult(final N node, final C controller) {
		this.node = node;
		this.controller = controller;
	}
	
	public N getNode() {
		return node;
	}
	
	public C getController() {
		return controller;
	}
}
