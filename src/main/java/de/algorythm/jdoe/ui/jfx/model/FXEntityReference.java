package de.algorythm.jdoe.ui.jfx.model;

import de.algorythm.jdoe.model.entity.IEntityReference;
import javafx.beans.property.ReadOnlyStringProperty;

public interface FXEntityReference extends IEntityReference {

	ReadOnlyStringProperty labelProperty();
	void assign(final FXEntityReference entityRef);
	FXEntityReference copy();
}
