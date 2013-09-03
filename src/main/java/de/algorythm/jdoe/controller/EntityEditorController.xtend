package de.algorythm.jdoe.controller

import com.google.inject.Injector
import de.algorythm.jdoe.JavaDbObjectEditorFacade
import de.algorythm.jdoe.bundle.Bundle
import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.model.dao.IObserver
import de.algorythm.jdoe.model.dao.ModelChange
import de.algorythm.jdoe.ui.jfx.cell.ReferringEntityCell
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue
import de.algorythm.jdoe.ui.jfx.taskQueue.FXTaskQueue
import java.util.LinkedList
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.geometry.VPos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.layout.GridPane
import javax.inject.Inject
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1

import static javafx.application.Platform.*

public class EntityEditorController implements IObserver<FXEntity, IFXPropertyValue<?>, FXEntityReference> {
	
	@Inject extension FXTaskQueue
	@Inject extension Injector
	@Inject extension IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao
	@Inject extension JavaDbObjectEditorFacade facade
	@Inject Bundle bundle
	@FXML GridPane gridPane
	@FXML ListView<FXEntityReference> referringEntities
	@FXML Button saveButton
	var FXEntity transientEntity
	val propertyUpdateCallbacks = new LinkedList<Procedure0>
	var Procedure1<FXEntity> saveCallback
	val createdContainedEntities = new LinkedList<FXEntityReference>
	val SimpleStringProperty editorTitle = new SimpleStringProperty
	val pristine = new SimpleBooleanProperty(true)
	var BooleanProperty busy
	
	def init(StringProperty titleProperty, BooleanProperty busy, FXEntityReference entityRef, Procedure1<FXEntity> saveCallback) {
		this.saveCallback = saveCallback
		this.busy = busy
		
		editorTitle.set(entityRef.labelProperty.value)
		titleProperty.bind(editorTitle)
		
		runTask('load-' + entityRef.id, '''«bundle.taskLoad»: «entityRef» («entityRef.type.label»)''') [|
			if (entityRef.exists)
				entityRef.find.initView
			else
				initView(entityRef as FXEntity)
		]
	}
	
	def ReadOnlyBooleanProperty pristineProperty() {
		pristine
	}
	
	def private initView(FXEntity entity) {
		transientEntity = entity
		
		runLater [|
			editorTitle.bind(transientEntity.labelProperty)
			pristine.bind(transientEntity.changedProperty.not)
			saveButton.disableProperty.bind(pristine)
			busy.value = true
			
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
			
			referringEntities.cellFactory = new ReferringEntityCell.Factory(facade)
			referringEntities.itemsProperty.value.all = entity.referringEntities
			
			addObserver(this)
		]
	}
	
	def save() {
		busy.value = true
		val saveEntity = transientEntity.copy
		transientEntity.changed = false
		
		runTask('save-entity-' + saveEntity.id, '''«bundle.taskSave»: «saveEntity» («saveEntity.type.label»)''') [|
			if (!transientEntity.type.embedded || transientEntity.exists) {
				runLater [|
					busy.value = true
				]
				
				try {
					transaction [
						save(saveEntity)
					]
				} catch(Exception e) {
					runLater [|
						transientEntity.changed = true
					]
					
					throw e
				} finally {
					runLater [|
						busy.value = false
					]
				}
			}
			
			applySaveCallback
		] 
	}
	
	def private applySaveCallback() {
		if (saveCallback != null) {
			val callback = saveCallback
			
			saveCallback = null
			
			runLater [|
				callback.apply(transientEntity)
			]
		}
	}
	
	def delete() {
		busy.value = true
		val deleteEntity = transientEntity.copy
		
		runTask('delete-entity-' + deleteEntity.id, '''«bundle.taskDelete»: «deleteEntity» («deleteEntity.type.label»)''') [|
			runLater [|
				busy.value = true
			]
			
			try {
				transaction [
					delete(deleteEntity)
				]
			} finally {
				runLater [|
					busy.value = false
				]
			}
		]
	}

	override update(ModelChange<FXEntity, IFXPropertyValue<?>, FXEntityReference> change) {
		if (change.deleted.contains(transientEntity))
			runLater [|
				transientEntity.closeEntityEditor
			]
		else {
			val visitor = new AssociationUpdateVisitor(change)
			
			runLater [|
				for (propertyValue : transientEntity.values)
					propertyValue.doWithValue(visitor)
			]
		}
	}
	
	def close() {
		removeObserver(this)
		
		for (entity : createdContainedEntities)
			if (!entity.exists)
				entity.closeEntityEditor
	}
}
