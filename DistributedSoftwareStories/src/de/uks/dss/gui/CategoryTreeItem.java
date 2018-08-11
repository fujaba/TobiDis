package de.uks.dss.gui;

import static de.uks.dss.model.PredefinedDocumentDataConstants.FALSE;
import static de.uks.dss.model.PredefinedDocumentDataConstants.TRUE;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.sdmlib.models.modelsets.SDMSet;

import de.uks.dss.app.DocumentDataCheckManApp;
import de.uks.dss.model.DocumentData;
import de.uks.dss.model.DocumentDataType;
import de.uniks.networkparser.interfaces.SendableEntity;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.util.Callback;

public class CategoryTreeItem extends TreeItem<Object> {
	private Callback<String, SDMSet> childrenCallback;
	private String text;
	private OwnTreeItem parentTreeItem;
	private LinkedList<MenuItem> menuItems = new LinkedList<>();
	private String tagAddition;

	public String getText() {
		return text;
	}

	public OwnTreeItem getParentTreeItem() {
		return parentTreeItem;
	}

	public Callback<String, SDMSet> getChildrenCallback() {
		return childrenCallback;
	}

	public LinkedList<MenuItem> getMenuItems() {
		return menuItems;
	}

	public CategoryTreeItem(String tagAddition, String value, @SuppressWarnings("rawtypes") Callback<String, SDMSet> func,
			OwnTreeItem parentTreeItem, MenuItem... MenuItems) {
		super(value);
		this.tagAddition = tagAddition;
		text = value;
		this.parentTreeItem = parentTreeItem;
		for (MenuItem menuItem : MenuItems) {
			menuItems.add(menuItem);
		}
		setValue(this);
		this.childrenCallback = func;

		// Add listener to Expanded State
		this.expandedProperty().addListener((t, oldVal, newVal) -> {
			if (newVal == true) {
				update();
			}
			;
		});

		this.setExpanded(TRUE.equals(getLayoutData().getValue()));
		this.expandedProperty().addListener((t, oldVal, newVal) -> getLayoutData().setValue(newVal.toString()));
	}

	HashMap<Object, OwnTreeItem> treeItems = new LinkedHashMap<>();

	public void update() {
		SDMSet call = childrenCallback.call("DontKnowWhatToSay");
		LinkedList<TreeItem<Object>> treeItemList = new LinkedList<>();
//		SortedList objects = new SortedList<>();
//		objects.withComparator(new Comparator<SendableEntity>() {
//			@Override
//			public int compare(SendableEntity o1, SendableEntity o2) {
//				String o1ComparableValue = "";
//				String o2ComparableValue = "";
//
//				if (o1 instanceof Person || o1 instanceof Task) {
//					o1ComparableValue = NameParser.getName(o1);
//					if(o1ComparableValue == null){
//						o1ComparableValue = "b";
//					}
//				} else {
//					o1ComparableValue = "a";
//				}
//				if (o2 instanceof Person || o2 instanceof Task) {
//					o2ComparableValue = NameParser.getName(o2);
//					if(o2ComparableValue == null){
//						o2ComparableValue = "b";
//					}
//				} else {
//					o2ComparableValue = "b";
//				}
//
//				return o1ComparableValue.compareToIgnoreCase(o2ComparableValue);
//			}
//		});
//		objects.addAll(call);
		for (Object object : call) {
			OwnTreeItem ownTreeItem = treeItems.get(object);
			if (ownTreeItem == null) {
				ownTreeItem = new OwnTreeItem(this.tagAddition, (SendableEntity) object, this);
				treeItems.put(object, ownTreeItem);
			} else {
				ownTreeItem.update();
			}
			treeItemList.add(ownTreeItem);
		}
		MultipleSelectionModel selectionModel = DocumentDataCheckManApp.getTreeView().getSelectionModel();
		ObservableList selectedItems = selectionModel.getSelectedItems();
		Object selectedItem = selectionModel.getSelectedItem();
		this.getChildren().setAll(treeItemList);
		if (!selectedItems.contains(selectedItem)) {
			// the currently selected item will be removed..
			selectionModel.clearSelection();
		}
	}

	private String layoutTagNameCache = null;

	public String getLayoutTagName() {
		if (layoutTagNameCache == null) {
			TreeItem<Object> parent = this.parentTreeItem;
			StringBuilder res = new StringBuilder();
			res.append(this.tagAddition);
			if (parent != null) {
				if (parent instanceof OwnTreeItem) {
					res.append(((OwnTreeItem) parent).getLayoutTagName() + ".");
				} else {
					// there shouldn't be a CategoryTreeItem as Parent...
				}
			}

			res.append(this.getText());
			layoutTagNameCache = res.toString();
		}
		return layoutTagNameCache;
	}

	private DocumentData layoutData = null;

	public DocumentData getLayoutData() {
		if (layoutData == null) {
			DocumentData user = DocumentDataCheckManApp.getUser();
			user.getOrCreateDocDataValue(getLayoutTagName(), DocumentDataType.BOOLEAN.toString(), FALSE);
			layoutData = user.getSubData(getLayoutTagName());
		}
		return layoutData;
	}

	@Override
	public String toString() {
		return this.getText();
	}

}
