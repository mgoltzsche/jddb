package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import java.util.Collection;
import java.util.LinkedList;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.MProperty;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;

public class FXAssociations extends AbstractFXPropertyValue<Collection<FXEntityReference>> implements ListChangeListener<FXEntityReference> {

	static private final long serialVersionUID = -2428408831904938958L;
	
	private final ListProperty<FXEntityReference> observableValue = new SimpleListProperty<>(FXCollections.observableList(new LinkedList<FXEntityReference>()));

	public FXAssociations(final MProperty property) {
		super(property);
		setValue(new LinkedList<FXEntityReference>());
	}
	
	@Override
	public void visit(final IPropertyValueVisitor<FXEntityReference> visitor) {
		visitor.doWithAssociations(this);
	}
	
	@Override
	public void visit(final IFXPropertyValueVisitor visitor) {
		visitor.doWithAssociations(this);
	}
	
	@Override
	public void toString(final StringBuilder sb) {
		sb.append(String.valueOf(getValue().size()));
	}
	
	@Override
	public IFXPropertyValue<Collection<FXEntityReference>> copy() {
		final FXAssociations copy = new FXAssociations(getProperty());
		
		copy.setValue(new LinkedList<>(getValue()));
		
		return copy;
	}
	
	public ListProperty<FXEntityReference> valueProperty() {
		return observableValue;
	}
	
	@Override
	public void onChanged(final Change<? extends FXEntityReference> change) {
		onObservableValueChanged(new LinkedList<FXEntityReference>(observableValue));
	}
	
	@Override
	public void setObservableValue(final Collection<FXEntityReference> value) {
		observableValue.setAll(value);
	}
	
	@Override
	protected void removeValueListener() {
		observableValue.removeListener(this);
	}

	@Override
	protected void addValueListener() {
		observableValue.addListener(this);
	}
	
	@Override
	public int compareTo(final IFXPropertyValue<Collection<FXEntityReference>> propertyValue) {
		return getValue().size() - propertyValue.getValue().size();
	}
}
