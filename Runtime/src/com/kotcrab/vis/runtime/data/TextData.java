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

package com.kotcrab.vis.runtime.data;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.runtime.entity.TextEntity;

public class TextData extends EntityData<TextEntity> {
	public float x, y;
	public float originX, originY;
	public float rotation;
	public float scaleX = 1, scaleY = 1;
	public Color tint = Color.WHITE;

	public String text;
	public String relativeFontPath;
	/** Arbitrary font name used by assets manager to recognize different font sizes for single truetype font */
	public String arbitraryFontName;
	public int fontSize;
	public boolean autoSetOriginToCenter;

	public boolean isTrueType;
	public boolean isUsesDistanceField;

	@Override
	public void saveFrom (TextEntity entity) {
		x = entity.getX();
		y = entity.getY();

		originX = entity.getOriginX();
		originY = entity.getOriginY();

		rotation = entity.getRotation();

		scaleX = entity.getScaleX();
		scaleY = entity.getScaleY();

		tint = entity.getColor().cpy();

		text = entity.getText();
		relativeFontPath = entity.getRelativeFontPath().replace("\\", "/");
		fontSize = entity.getFontSize();
		autoSetOriginToCenter = entity.isAutoSetOriginToCenter();

		arbitraryFontName = String.valueOf(fontSize) + "." + relativeFontPath;

		isTrueType = entity.isTrueType();
		isUsesDistanceField = entity.isDistanceFieldShaderEnabled();
	}

	@Override
	public void loadTo (TextEntity text) {
		text.setPosition(x, y);
		text.setOrigin(originX, originY);
		text.setRotation(rotation);
		text.setScale(scaleX, scaleY);
		text.setColor(tint);

		text.setText(this.text);
		//text.setFontSize(fontSize); //font size must be handled manually from SceneLoader because it is not a public property for TextEntity
		text.setRelativeFontPathForSerialize(relativeFontPath);
		text.setAutoSetOriginToCenter(autoSetOriginToCenter);

		text.setDistanceFieldShaderEnabled(isUsesDistanceField);
	}
}
