/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.ui.dialog;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.util.DialogUtils;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

public class SelectFontDialog extends VisWindow {

	private final String extension;
	private final FileHandle fontFolder;
	private FontDialogListener listener;

	private ObjectMap<String, FileHandle> fontsMap = new ObjectMap<>();

	private VisList<String> fontList;

	public SelectFontDialog (String extension, FileHandle fontFolder, FontDialogListener listener) {
		super("Select New Font");
		this.extension = extension;
		this.fontFolder = fontFolder;
		this.listener = listener;

		setModal(true);
		addCloseButton();
		closeOnEscape();

		fontList = new VisList<>();

		VisTextButton cancelButton;
		VisTextButton okButton;

		TableUtils.setSpaceDefaults(this);
		defaults().left();

		VisTable buttonsTable = new VisTable(true);
		buttonsTable.add(cancelButton = new VisTextButton("Cancel"));
		buttonsTable.add(okButton = new VisTextButton("OK"));

		add(fontList).expand().fill().row();
		add(buttonsTable).right();

		cancelButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				fadeOut();
			}
		});

		okButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				finishSelection();
			}
		});

		fontList.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				if (getTapCount() == 2 && event.getButton() == Buttons.LEFT) finishSelection();
			}
		});

		rebuildFontList();
	}

	private void packAndCenter () {
		pack();
		setSize(getWidth() + 50, getHeight());
		centerWindow();
	}

	public void rebuildFontList () {
		fontList.clearItems();
		fontsMap.clear();

		buildFontList(fontFolder);
		packAndCenter();
	}

	private void finishSelection () {
		FileHandle file = fontsMap.get(fontList.getSelected());

		if (file == null) {
			DialogUtils.showErrorDialog(getStage(), "You must select font!");
			return;
		}

		listener.selected(file);
		fadeOut();
	}

	private void buildFontList (FileHandle fontDirectory) {
		for (FileHandle file : fontDirectory.list()) {
			if (file.isDirectory()) buildFontList(fontDirectory);

			if (file.extension().equals(extension))
				fontsMap.put(file.path().substring(fontFolder.path().length() + 1), file);
		}

		fontList.setItems(fontsMap.keys().toArray());
	}

	public interface FontDialogListener {
		public void selected (FileHandle file);
	}
}
