package de.algorythm.jdoe.controller

import com.google.inject.Injector
import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.model.dao.IObserver
import de.algorythm.jdoe.model.entity.IPropertyValue
import de.algorythm.jdoe.taskQueue.TaskQueue
import de.algorythm.jdoe.ui.jfx.model.EditorStateModel
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.util.IEntityEditorManager
import java.util.LinkedList
import javafx.fxml.FXML
import javafx.geometry.VPos
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javax.inject.Inject
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1

import static javafx.application.Platform.*

public class EntityEditorController implements IController, IObserver {
	
	@Inject extension IDAO
	@Inject extension TaskQueue
	@Inject extension IEntityEditorManager
	@Inject extension Injector
	@FXML var GridPane gridPane
	@FXML var EditorStateModel editorState
	var FXEntity entity
	val propertySaveCallbacks = new LinkedList<Procedure0>
	val propertyUpdateCallbacks = new LinkedList<Procedure0>
	var Procedure1<FXEntity> saveCallback
	val createdContainedEntities = new LinkedList<FXEntity>
	
	override init() {}
	
	def init(FXEntity entity, Procedure1<FXEntity> saveCallback) {
		this.entity = entity
		this.saveCallback = saveCallback
		
		runLater [|
			var i = 0
			
			for (IPropertyValue<?> value : entity.model.values) {
				val label = new Label(value.property.label + ': ')
				
				GridPane.setValignment(label, VPos.TOP)
				
				gridPane.add(label, 0, i)
				
				val visitor = new PropertyValueEditorVisitor(gridPane, i, createdContainedEntities, propertySaveCallbacks, propertyUpdateCallbacks)
				
				visitor.injectMembers
				
				value.doWithValue(visitor)
				
				i = i + 1
			}
			
			update
		]
		
		addObserver(this)
	}
	
	def save() {
		for (callback : propertySaveCallbacks)
			callback.apply
		
		if (entity.persisted || saveCallback == null) {
			editorState.disableProperty.value = true
			
			runTask('saving entity') [|
				try {
					entity.model.save
				} finally {
					runLater [|
						entity.applyPropertyValues
						editorState.disableProperty.value = false
					]
				}
			]
		} else {
			saveCallback.apply(entity)
			entity.applyPropertyValues
		}
	}
	
	def delete() {
		editorState.disableProperty.value = true
		
		runTask('deleting entity') [|
			try {
				entity.model.delete
			} finally {
				runLater [|
					editorState.disableProperty.value = false
				]
			}
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
		removeObserver(this)
		
		for (entity : createdContainedEntities)
			if (!entity.persisted)
				entity.closeEntityEditor
	}
}
