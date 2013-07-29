package de.algorythm.jdoe.controller

import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.model.dao.IObserver
import de.algorythm.jdoe.model.entity.IEntity
import de.algorythm.jdoe.model.entity.IPropertyValue
import java.util.LinkedList
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.geometry.VPos
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.GridPane
import javax.inject.Inject
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1

public class EntityEditorController implements IController, IObserver {
	
	@Inject extension IDAO dao
	@FXML var ScrollPane scrollPane
	var IEntity entity
	val propertySaveCallbacks = new LinkedList<Procedure0>
	val propertyUpdateCallbacks = new LinkedList<Procedure0>
	var Procedure1<IEntity> saveCallback
	val createdContainedEntities = new LinkedList<IEntity>
	extension IEntityEditorManager editorManager
	
	override init() {
	}
	
	def init(IEntity entity, IEntityEditorManager editorManager, Procedure1<IEntity> saveCallback) {
		this.entity = entity
		this.saveCallback = saveCallback
		this.editorManager = editorManager
		
		val gridPane = new GridPane
		var i = 0
		
		gridPane.padding = new Insets(20)
		gridPane.vgap = 20
		gridPane.hgap = 10
		
		for (IPropertyValue value : entity.getValues()) {
			val label = new Label(value.getProperty().getLabel() + ': ')
			
			GridPane.setValignment(label, VPos.TOP)
			
			gridPane.add(label, 0, i)
			
			value.doWithValue(new PropertyValueEditorVisitor(gridPane, i, dao, editorManager, createdContainedEntities, propertySaveCallbacks, propertyUpdateCallbacks))
			
			i = i + 1
		}
		
		update
		
		scrollPane.content = gridPane
		
		// add/remove observer
		dao.addObserver(this)
	}
	
	def getLabel() {
		if (entity.id == null)
			entity.type.label + ' (neu)'
		else
			entity.type.label + ": " + entity
	}
	
	def save() {
		for (callback : propertySaveCallbacks)
			callback.apply
		
		if (entity.id != null || saveCallback == null)
			entity.save
		else
			saveCallback.apply(entity)
	}
	
	def void cancel() {
		
	}

	override update() {
		for (callback : propertyUpdateCallbacks)
			callback.apply
	}
	
	def close() {
		dao.removeObserver(this)
		
		for (entity : createdContainedEntities)
			if (entity.id == null)
				entity.closeEntityEditor
	}
}
