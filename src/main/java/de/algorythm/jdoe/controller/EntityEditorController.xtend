package de.algorythm.jdoe.controller

import com.google.inject.Injector
import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.model.dao.IObserver
import de.algorythm.jdoe.taskQueue.TaskQueue
import de.algorythm.jdoe.ui.jfx.model.EditorStateModel
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue
import de.algorythm.jdoe.ui.jfx.util.IEntityEditorManager
import java.util.HashMap
import java.util.LinkedList
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.geometry.VPos
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javax.inject.Inject
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1

import static javafx.application.Platform.*

public class EntityEditorController implements IController, IObserver {
	
	@Inject extension TaskQueue
	@Inject extension IEntityEditorManager
	@Inject extension Injector
	@Inject extension IDAO<FXEntityReference,IFXPropertyValue<?>,FXEntity> dao
	@FXML var GridPane gridPane
	@FXML var EditorStateModel editorState
	var FXEntity transientEntity
	var FXEntity savedEntity
	val propertySaveCallbacks = new LinkedList<Procedure0>
	val propertyUpdateCallbacks = new LinkedList<Procedure0>
	var Procedure1<FXEntity> saveCallback
	val createdContainedEntities = new LinkedList<FXEntityReference>
	val visibleEntityMap = new HashMap<String,FXEntity>
	
	override init() {}
	
	def init(FXEntity entity, Procedure1<FXEntity> saveCallback) {
		savedEntity = entity
		transientEntity = new FXEntity(entity)
		this.saveCallback = saveCallback
		
		runLater [|
			var i = 0
			
			for (IFXPropertyValue<?> value : transientEntity.values) {
				val label = new Label(value.property.label + ': ')
				
				GridPane.setValignment(label, VPos.TOP)
				GridPane.setMargin(label, new Insets(4, 0, 0, 0))
				
				gridPane.add(label, 0, i)
				
				val visitor = new PropertyValueEditorVisitor(gridPane, i, createdContainedEntities, propertySaveCallbacks, propertyUpdateCallbacks)
				
				visitor.injectMembers
				
				value.doWithValue(visitor)
				
				i = i + 1
			}
			
			addObserver(this)
			update
		]
	}
	
	def save() {
		for (callback : propertySaveCallbacks)
			callback.apply
		
		if (transientEntity.isTransientInstance || saveCallback == null) {
			editorState.busy = true
			val saveEntity = new FXEntity(transientEntity)
			
			runTask('saving entity') [|
				runLater [|
					editorState.busy = true
				]
				
				try {
					saveEntity.save
					
					runLater [|
						assignSavedEntity(saveEntity)
					]
				} finally {
					runLater [|
						editorState.busy = false
					]
				}
			]
		} else {
			saveCallback.apply(transientEntity)
			assignSavedEntity(transientEntity)
		}
	}
	
	def delete() {
		editorState.busy = true
		val deleteEntity = new FXEntity(transientEntity)
		
		runTask('deleting entity') [|
			runLater [|
				editorState.busy = true
			]
			
			try {
				deleteEntity.delete
			} finally {
				runLater [|
					editorState.busy = false
				]
			}
		]
	}

	override update() {
		if (!transientEntity.isTransientInstance || transientEntity.exists)
			for (callback : propertyUpdateCallbacks)
				callback.apply
		else
			transientEntity.closeEntityEditor
	}
	
	def close() {
		removeObserver(this)
		
		for (entity : createdContainedEntities)
			if (!entity.isTransientInstance)
				entity.closeEntityEditor
	}
	
	def private void assignSavedEntity(FXEntity entity) {
		savedEntity.assign(entity)
		transientEntity.transientInstance = entity.isTransientInstance
	}
}
