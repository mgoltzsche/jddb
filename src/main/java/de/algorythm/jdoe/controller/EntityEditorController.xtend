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
import java.util.LinkedList
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.geometry.VPos
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javax.inject.Inject
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1

import static javafx.application.Platform.*

import static extension de.algorythm.jdoe.ui.jfx.model.util.ContainmentCollector.*

public class EntityEditorController implements IController, IObserver {
	
	@Inject extension TaskQueue
	@Inject extension IEntityEditorManager
	@Inject extension Injector
	@Inject extension IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao
	@FXML var GridPane gridPane
	@FXML var EditorStateModel editorState
	var FXEntity transientEntity
	val propertyUpdateCallbacks = new LinkedList<Procedure0>
	var Procedure1<FXEntity> saveCallback
	val createdContainedEntities = new LinkedList<FXEntityReference>
	val SimpleStringProperty editorTitle = new SimpleStringProperty()
	
	override init() {}
	
	def init(StringProperty titleProperty, FXEntityReference entityRef, Procedure1<FXEntity> saveCallback) {
		this.saveCallback = saveCallback
		
		editorTitle.set(entityRef.labelProperty.value)
		titleProperty.bind(editorTitle)
		
		runTask('open-editor-' + entityRef.id) [|
			entityRef.find.initView
		]
	}
	
	def private initView(FXEntity entity) {
		transientEntity = entity
		
		runLater [|
			editorTitle.bind(transientEntity.labelProperty)
			
			var i = 0
			
			for (IFXPropertyValue<?> value : transientEntity.values) {
				val label = new Label(value.property.label + ': ')
				
				GridPane.setValignment(label, VPos.TOP)
				GridPane.setMargin(label, new Insets(4, 0, 0, 0))
				
				gridPane.add(label, 0, i)
				
				val visitor = new PropertyValueEditorVisitor(gridPane, i, transientEntity, createdContainedEntities, propertyUpdateCallbacks)
				
				visitor.injectMembers
				
				value.doWithValue(visitor)
				
				i = i + 1
			}
			
			addObserver(this)
			update
		]
	}
	
	def save() {
		if (transientEntity.type.embedded && transientEntity.exists) {
			editorState.busy = true
			val saveEntity = new FXEntity(transientEntity)
			val containments = transientEntity.allContainments
			
			runTask('save-entity-' + saveEntity.id) [|
				runLater [|
					editorState.busy = true
				]
				
				try {
					transaction [
						for (newContainment : containments.filter[e|!e.exists])
							save(newContainment)
						
						save(saveEntity)
					]
				} finally {
					runLater [|
						editorState.busy = false
					]
				}
			]
		}
		
		if (saveCallback != null) {
			saveCallback.apply(transientEntity)
			saveCallback = null
		}
	}
	
	def delete() {
		editorState.busy = true
		val deleteEntity = new FXEntity(transientEntity)
		
		runTask('delete-entity-' + deleteEntity.id) [|
			runLater [|
				editorState.busy = true
			]
			
			try {
				transaction [
					delete(deleteEntity)
				]
			} finally {
				runLater [|
					editorState.busy = false
				]
			}
		]
	}

	override update() {
		if (transientEntity.exists)
			for (callback : propertyUpdateCallbacks)
				callback.apply
		else
			transientEntity.closeEntityEditor
	}
	
	def close() {
		removeObserver(this)
		
		for (entity : createdContainedEntities)
			if (!entity.exists)
				entity.closeEntityEditor
	}
}
