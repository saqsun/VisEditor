/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kotcrab.vis.ui.widget;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisUI;

/** @author Kotcrab */
public class VisWindow extends Window {
	public static float FADE_TIME = 0.3f;

	private boolean centerOnAdd;

	public VisWindow (String title) {
		this(title, true);
	}

	public VisWindow (String title, boolean showWindowBorder) {
		super(title, VisUI.getSkin(), showWindowBorder ? "default" : "noborder");
		setTitleAlignment(VisUI.getDefaultTitleAlign());
	}

	@Override
	public void setPosition (float x, float y) {
		super.setPosition((int) x, (int) y);
	}

	/**
	 * Centers this window, if it has parent it will be done instantly, if it does not have parent it will be centered when it will
	 * be added to stage
	 * @return true when window was centered, false when window will be centered when added to stage
	 */
	public boolean centerWindow () {
		Group parent = getParent();
		if (parent == null) {
			centerOnAdd = true;
			return false;
		} else {
			moveToCenter();
			return true;
		}
	}

	@Override
	protected void setStage (Stage stage) {
		super.setStage(stage);

		if (centerOnAdd) {
			centerOnAdd = false;
			moveToCenter();
		}
	}

	private void moveToCenter () {
		Stage parent = getStage();
		if (parent != null) setPosition((parent.getWidth() - getWidth()) / 2, (parent.getHeight() - getHeight()) / 2);
	}

	/** Fade outs this window, when fade out animation is completed, window is removed from Stage */
	public void fadeOut (float time) {
		addAction(Actions.sequence(Actions.fadeOut(time, Interpolation.fade), Actions.removeActor()));
	}

	/** @return this window for the purpose of chaining methods eg. stage.addActor(new MyWindow(stage).fadeIn(0.3f)); */
	public VisWindow fadeIn (float time) {
		setColor(1, 1, 1, 0);
		addAction(Actions.fadeIn(time, Interpolation.fade));
		return this;
	}

	/** Fade outs this window, when fade out animation is completed, window is removed from Stage */
	public void fadeOut () {
		fadeOut(FADE_TIME);
	}

	/** @return this window for the purpose of chaining methods eg. stage.addActor(new MyWindow(stage).fadeIn()); */
	public VisWindow fadeIn () {
		return fadeIn(FADE_TIME);
	}

	/**
	 * Called by window when close button was pressed (added using {@link #addCloseButton()})
	 * or escape key was pressed (for close on escape {@link #closeOnEscape()} have to be called).
	 * Default close behaviour is to fade out window, this can be changed by overriding this function.
	 */
	protected void close () {
		fadeOut();
	}

	/**
	 * Deprecated because this method for VisWindow and VisDialog returns completely different things (title table for VisWindow, and
	 * buttons table for VisDialog) Since this method is from Window it cannot be removed and was deprecated to avoid confusion.
	 * If you want to get title table from VisWindow use {@link #getTitleTable}
	 * If you want to get buttons table form VisDialog use {@link VisDialog#getButtonsTable()}
	 */
	@Override
	@Deprecated
	public Table getButtonTable () {
		return super.getButtonTable();
	}

	public Table getTitleTable () {
		return super.getButtonTable();
	}

	/** Adds close button to window, next to window title. After pressing that button, {@link #close()} is called. */
	public void addCloseButton () {
		VisImageButton closeButton = new VisImageButton("close-window");
		getTitleTable().add(closeButton).padRight(1).padBottom(1);
		closeButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				close();
			}
		});
	}

	/** Will make this window close when escape key was pressed. After pressing escape {@link #close()} is called. */
	public void closeOnEscape () {
		addListener(new InputListener() {
			@Override
			public boolean keyDown (InputEvent event, int keycode) {
				if (keycode == Keys.ESCAPE) {
					close();
					return true;
				}

				return false;
			}
		});
	}
}
