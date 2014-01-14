package de.algorythm.jdoe.ui.jfx.model;

import javafx.beans.property.ReadOnlyStringProperty;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValueVisitor;

public interface FXEntityReference extends IEntityReference {

	ReadOnlyStringProperty labelProperty();
	void assign(final FXEntityReference entityRef);
	void visit(IFXPropertyValueVisitor propertyVisitor);
}
