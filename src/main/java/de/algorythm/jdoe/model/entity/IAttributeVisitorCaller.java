package de.algorythm.jdoe.model.entity;

public interface IAttributeVisitorCaller {

	void doWithBoolean();
	void doWithDecimal();
	void doWithReal();
	void doWithDate();
	void doWithString();
	void doWithText();
	void doWithFile();
}
