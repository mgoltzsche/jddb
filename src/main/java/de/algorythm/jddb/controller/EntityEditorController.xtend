package de.algorythm.jddb.controller

import com.google.inject.Injector
import de.algorythm.jddb.JddbFacade
import de.algorythm.jddb.bundle.Bundle
import de.algorythm.jddb.model.dao.IDAO
import de.algorythm.jddb.model.dao.IObserver
import de.algorythm.jddb.model.dao.ModelChange
import de.algorythm.jddb.taskQueue.ITaskPriority
import de.algorythm.jddb.ui.jfx.cell.EntityReferenceCell
import de.algorythm.jddb.ui.jfx.model.EditorStateModel
import de.algorythm.jddb.ui.jfx.model.FXEntity
import de.algorythm.jddb.ui.jfx.model.FXEntityReference
import de.algorythm.jddb.ui.jfx.model.IFXEntityChangeListener
import de.algorythm.jddb.ui.jfx.model.factory.AssociationRemovingVisitor
import de.algorythm.jddb.ui.jfx.model.propertyValue.IFXPropertyValue
import de.algorythm.jddb.ui.jfx.taskQueue.FXTaskQueue
import de.algorythm.jddb.ui.jfx.taskQueue.FXTransactionTask
import java.util.Collection
import java.util.Date
import java.util.HashMap
import java.util.LinkedList
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.geometry.VPos
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.SplitPane
import javafx.scene.layout.GridPane
import javax.inject.Inject
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1

import static javafx.application.Platform.*

public class EntityEditorController implements IObserver<FXEntity, IFXPropertyValue<?>, FXEntityReference>, IEntitySaveResult, IFXEntityChangeListener {
	
	@Inject extension FXTaskQueue
	@Inject extension Injector
	@Inject extension IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao
	@Inject extension JddbFacade facade
	@Inject Bundle bundle
	@FXML EditorStateModel editorState
	@FXML SplitPane editorPane
	@FXML GridPane gridPane
	@FXML ListView<FXEntityReference> referringEntities
	var FXEntityReference entityReference
	var FXEntity transientEntity
	val propertyUpdateCallbacks = new LinkedList<Procedure0>
	var Procedure1<IEntitySaveResult> saveCallback
	val saveContainmentTasks = new HashMap<FXEntityReference, Collection<FXTransactionTask>>
	val containedNewEntities = new LinkedList<FXEntityReference>
	
	def init(FXEntityReference entityRef, Procedure1<IEntitySaveResult> saveCallback) {
		this.entityReference = entityRef
		this.saveCallback = saveCallback
		
		editorState.title = entityRef.labelProperty.value
		editorState.typeName = entityRef.type.label
		
		runTask('load-' + entityRef.id, '''«bundle.taskLoad»: «entityRef» («entityRef.type.label»)''', ITaskPriority.FIRST) [|
			val isNew = !entityRef.exists
			val editEntity = if (isNew)
				entityRef as FXEntity
			else
				find(entityRef)
			
			runLater [|
				editorState.creating = isNew
				editEntity.initView
			]
		]
		
		editorState
	}
	
	def private initView(FXEntity entity) {
		transientEntity = entity.copy
		transientEntity.setChangeListener(this)
		
		runLater [|
			editorState.titleProperty.bind(transientEntity.labelProperty)
			editorState.pristine = true
			editorState.busy = false
			
			var i = 0
			
			for (IFXPropertyValue<?> value : transientEntity.values) {
				val label = new Label(value.property.label + ': ')
				
				GridPane.setValignment(label, VPos.TOP)
				GridPane.setMargin(label, new Insets(4, 0, 0, 0))
				
				gridPane.add(label, 0, i)
				
				val visitor = new PropertyValueEditorVisitor(gridPane, i, entityReference, saveContainmentTasks, containedNewEntities, propertyUpdateCallbacks, editorPane.visibleProperty)
				
				visitor.injectMembers
				
				value.visit(visitor)
				
				i = i + 1
			}
			
			referringEntities.cellFactory = new EntityReferenceCell.Factory(facade)
			referringEntities.itemsProperty.bind(entity.referringEntitiesProperty)
			
			addObserver(this)
		]
	}
	
	def getEditorState() {
		return editorState
	}
	
	def save() {
		if (!transientEntity.type.embedded || saveCallback == null) {
			editorState.busy = true
			val callback = saveCallback
			val saveTasks = saveLater
			
			// persist entity (with containments)
			for (saveTask : saveTasks)
				saveTask.onBefore
			
			runTask('''save-entity-«transientEntity.id»-«new Date().time»''', '''«bundle.taskSave»: «transientEntity» («transientEntity.type.label»)''', ITaskPriority.LAST) [|
				saveTasks.runSaveTasks
				
				// run save callback once
				if (callback != null)
					runLater [|
						callback.apply(this)
					]
			]
		} else if (saveCallback != null) {
			entityReference.assign(transientEntity)
			saveCallback.apply(this)
		}
	}
	
	override getEntityReference() {
		entityReference
	}
	
	override saveLater() {
		val saveTasks = new LinkedList<FXTransactionTask>
		val saveEntity = transientEntity.copy
		editorState.pristine = true
		
		saveTasks += saveContainmentTasks.values.flatten
		saveTasks += new FXTransactionTask(saveEntity) => [
			setOnBefore [|
				editorState.busy = true
			]
			setTask [
				save(saveEntity)
			]
			setOnFailed [|
				editorState.pristine = false
				editorState.busy = false
			]
			setOnSuccess [|
				saveCallback = null
				saveContainmentTasks.clear
				editorState.creating = false
				editorState.busy = false
			]
		]
		
		saveTasks
	}
	
	def private runSaveTasks(Iterable<FXTransactionTask> saveTasks) {
		try {
			transaction [
				for (saveTask : saveTasks)
					saveTask.run(it)
			]
		} catch(Exception e) {
			for (saveTask : saveTasks)
				saveTask.onFailed
			throw e
		}
		
		for (saveTask : saveTasks)
			saveTask.onSuccess
	}
	
	def delete() {
		editorState.busy = true
		editorState.deleting = true
		val deleteEntity = transientEntity.copy
		
		runTask('delete-entity-' + deleteEntity.id, '''«bundle.taskDelete»: «deleteEntity» («deleteEntity.type.label»)''', ITaskPriority.LAST) [|
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
					editorState.deleting = false
				]
			}
		]
	}

	override update(ModelChange<FXEntity, IFXPropertyValue<?>, FXEntityReference> change) {
		runLater [|
			if (change.deleted.contains(transientEntity)) {
				// close entity if deleted
				transientEntity.closeEntityEditor
			} else {
				// remove deleted associations
				for (deletedEntity : change.deleted)
					AssociationRemovingVisitor.removeAssociationsTo(deletedEntity, transientEntity)
			}
		]
	}
	
	def close() {
		removeObserver(this)
		
		runTask('close-unsaved-containment-editors-' + entityReference.id, bundle.taskCloseTransientContainmentEditors, ITaskPriority.FIRST) [|
			for (entity : containedNewEntities.filter[e|!e.exists])
				entity.closeEntityEditor
			
			// gc workaround
			transientEntity = null
			entityReference = null
			saveContainmentTasks.clear
			containedNewEntities.clear
		]
	}
	
	override changed() {
		this.editorState.pristine = false
	}
	
}
