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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.event.Event;
import com.kotcrab.vis.editor.event.EventListener;
import com.kotcrab.vis.editor.event.ProjectStatusEvent;
import com.kotcrab.vis.editor.ui.tab.StartPageTab;
import com.kotcrab.vis.editor.ui.tabbedpane.MainContentTab;
import com.kotcrab.vis.editor.ui.tabbedpane.Tab;
import com.kotcrab.vis.editor.ui.tabbedpane.TabbedPane;
import com.kotcrab.vis.editor.ui.tabbedpane.TabbedPaneListener;

public class TabsModule extends EditorModule implements EventListener {
	private TabbedPane tabbedPane;
	private TabbedPaneListener listener;

	private StartPageTab startPageTab;

	public TabsModule (TabbedPaneListener listener) {
		this.listener = listener;
	}

	@Override
	public void init () {
		tabbedPane = new TabbedPane(listener);

		startPageTab = new StartPageTab();

		tabbedPane.add(startPageTab);
	}

	public void addTab (MainContentTab tab) {
		tabbedPane.add(tab);
	}

	public void removeTab (MainContentTab tab) {
		tabbedPane.remove(tab);
	}

	public void switchTab (MainContentTab tab) {
		tabbedPane.switchTab(tab);
	}

	public void addListener (TabbedPaneListener listener) {
		tabbedPane.addListener(listener);
	}

	public boolean removeListener (TabbedPaneListener listener) {
		return tabbedPane.removeListener(listener);
	}

	public Table getTable () {
		return tabbedPane.getTable();
	}

	@Override
	public void added () {
		App.eventBus.register(this);
	}

	@Override
	public void dispose () {
		App.eventBus.unregister(this);
	}

	@Override
	public boolean onEvent (Event e) {
		if (e instanceof ProjectStatusEvent) {
			ProjectStatusEvent event = (ProjectStatusEvent) e;
			if (event.status == ProjectStatusEvent.Status.Loaded)
				tabbedPane.remove(startPageTab);
			else {
				tabbedPane.removeAll();
				tabbedPane.add(startPageTab);
			}
		}

		return false;
	}

	public Array<Tab> getTabs () {
		return tabbedPane.getTabs();
	}

	public int getDirtyTabCount () {
		Array<Tab> tabs = getTabs();

		int count = 0;

		for (Tab tab : tabs)
			if (tab.isDirty()) count++;

		return count;
	}
}
