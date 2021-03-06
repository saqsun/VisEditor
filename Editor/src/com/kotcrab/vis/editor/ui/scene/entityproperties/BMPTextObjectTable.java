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

package com.kotcrab.vis.editor.ui.scene.entityproperties;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.scene.EditorEntity;
import com.kotcrab.vis.editor.scene.TextObject;
import com.kotcrab.vis.editor.ui.IndeterminateCheckbox;
import com.kotcrab.vis.ui.widget.Tooltip;

class BMPTextObjectTable extends TextObjectTable {
	private IndeterminateCheckbox distanceFieldCheck;

	public BMPTextObjectTable (final EntityProperties properties) {
		super(properties);

		distanceFieldCheck = new IndeterminateCheckbox("Use DF");
		distanceFieldCheck.addListener(properties.getSharedCheckBoxChangeListener());

		fontPropertiesTable.add(distanceFieldCheck);

		new Tooltip(distanceFieldCheck, "Use distance field shader for this text");
	}

	@Override
	protected String getFontExtension () {
		return "fnt";
	}

	@Override
	protected FileHandle getFontFolder () {
		return properties.getFileAccessModule().getBMPFontFolder();
	}

	@Override
	public boolean isSupported (EditorEntity entity) {
		if (!(entity instanceof TextObject)) return false;
		TextObject obj = (TextObject) entity;
		return !obj.isTrueType();
	}

	@Override
	int getRelativeFontFolderLength () {
		return properties.getFileAccessModule().getBMPFontFolderRelative().length();
	}

	@Override
	public void updateUIValues () {
		super.updateUIValues();

		Utils.setCheckBoxState(properties.getEntities(), distanceFieldCheck, entity -> ((TextObject) entity).isDistanceFieldShaderEnabled());
	}

	@Override
	public void setValuesToEntities () {
		super.setValuesToEntities();

		Array<EditorEntity> entities = properties.getEntities();
		for (EditorEntity entity : entities) {
			TextObject obj = (TextObject) entity;

			if (distanceFieldCheck.isIndeterminate() == false)
				obj.setDistanceFieldShaderEnabled(distanceFieldCheck.isChecked());
		}
	}
}
