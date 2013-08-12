package de.algorythm.jdoe.controller

import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.model.dao.IObserver
import de.algorythm.jdoe.model.entity.IPropertyValue
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.util.IEntityEditorManager
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

public class EntityEditorController extends AbstractXtendController implements IController, IObserver {
	
	@Inject extension IDAO dao
	@FXML var ScrollPane scrollPane
	var FXEntity entity
	val propertySaveCallbacks = new LinkedList<Procedure0>
	val propertyUpdateCallbacks = new LinkedList<Procedure0>
	var Procedure1<FXEntity> saveCallback
	val createdContainedEntities = new LinkedList<FXEntity>
	extension IEntityEditorManager editorManager
	
	override init() {}
	
	def init(FXEntity entity, IEntityEditorManager editorManager, Procedure1<FXEntity> saveCallback) {
		this.entity = entity
		this.saveCallback = saveCallback
		this.editorManager = editorManager
		
		val gridPane = new GridPane
		var i = 0
		
		gridPane.padding = new Insets(20)
		gridPane.vgap = 20
		gridPane.hgap = 10
		
		for (IPropertyValue<?> value : entity.model.values) {
			val label = new Label(value.property.label + ': ')
			
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
	
	def save() {
		for (callback : propertySaveCallbacks)
			callback.apply
		
		if (entity.persisted || saveCallback == null) {
			runTask('saving entity') [|
				entity.model.save
				entity.applyPropertyValues
			]
		} else {
			saveCallback.apply(entity)
			entity.applyPropertyValues
		}
	}
	
	def delete() {
		runTask('deleting entity') [|
			entity.model.delete
		]
	}

	override update() {
		if (!entity.persisted || entity.model.update)
			for (callback : propertyUpdateCallbacks)
				callback.apply
		else
			entity.closeEntityEditor
	}
	
	def close() {
		dao.removeObserver(this)
		
		for (entity : createdContainedEntities)
			if (!entity.persisted)
				entity.closeEntityEditor
	}
}
