package de.algorythm.jdoe.ui.jfx.model;

import java.util.Map;

import javafx.beans.property.ReadOnlyStringProperty;
import de.algorythm.jdoe.model.entity.IEntityReference;

public interface FXEntityReference extends IEntityReference {

	ReadOnlyStringProperty labelProperty();
	void assign(final FXEntityReference entityRef);
	FXEntityReference copy();
	FXEntityReference copy(Map<String,FXEntityReference> copiedEntities);
}
