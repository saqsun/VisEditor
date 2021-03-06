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

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.kotcrab.vis.editor.scene.EditorEntity;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.scene.MusicObject;
import com.kotcrab.vis.editor.scene.ParticleObject;
import com.kotcrab.vis.editor.scene.SpriteObject;
import com.kotcrab.vis.editor.scene.TextObject;
import com.kotcrab.vis.editor.serializer.MusicObjectSerializer;
import com.kotcrab.vis.editor.serializer.SpriteSerializer;
import com.kotcrab.vis.editor.util.Log;
import com.kotcrab.vis.editor.util.SpriteUtils;
import com.kotcrab.vis.runtime.scene.SceneViewport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@SuppressWarnings("rawtypes")
public class SceneIOModule extends ProjectModule {
	private Kryo kryo;

	private FileAccessModule fileAccessModule;

	private TextureCacheModule textureModule;
	private FontCacheModule fontModule;
	private ParticleCacheModule particleModule;

	private FileHandle assetsFolder;

	@Override
	public void init () {
		fileAccessModule = projectContainer.get(FileAccessModule.class);

		textureModule = projectContainer.get(TextureCacheModule.class);
		fontModule = projectContainer.get(FontCacheModule.class);
		particleModule = projectContainer.get(ParticleCacheModule.class);

		assetsFolder = fileAccessModule.getAssetsFolder();

		kryo = new Kryo();
		kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
		kryo.register(Array.class, 10);
		kryo.register(Rectangle.class, 11);
		kryo.register(TextBounds.class, 12);
		kryo.register(Matrix4.class, 13);
		kryo.register(Sprite.class, new SpriteSerializer(), 30);

		kryo.register(MusicObject.class, new MusicObjectSerializer(kryo, fileAccessModule));
	}

	public Kryo getKryo () {
		return kryo;
	}

	public EditorScene load (FileHandle fullPathFile) {
		EditorScene scene = deserializeScene(fullPathFile);

		prepareSceneAfterLoad(scene);

		return scene;
	}

	private EditorScene deserializeScene (FileHandle fullPathFile) {
		try {
			Input input = new Input(new FileInputStream(fullPathFile.file()));
			EditorScene scene = kryo.readObject(input, EditorScene.class);
			scene.path = fileAccessModule.relativizeToAssetsFolder(fullPathFile);
			input.close();

			return scene;
		} catch (FileNotFoundException e) {
			Log.exception(e);
		}

		throw new IllegalStateException("There was an error during scene deserializing");
	}

	private void prepareSceneAfterLoad (EditorScene scene) {
		for (EditorEntity entity : scene.entities) {
			if (entity instanceof SpriteObject) {
				SpriteObject spriteObject = (SpriteObject) entity;
				SpriteUtils.setRegion(spriteObject.getSprite(), textureModule.getRegion(spriteObject.getCacheRegionName()));
			}

			if (entity instanceof TextObject) {
				TextObject textObject = (TextObject) entity;
				EditorFont font = fontModule.get(fileAccessModule.getAssetsFolder().child(textObject.getRelativeFontPath()));
				textObject.afterDeserialize(font);
			}

			if (entity instanceof ParticleObject) {
				ParticleObject particle = (ParticleObject) entity;
				ParticleEffect effect = particleModule.get(fileAccessModule.getAssetsFolder().child(particle.getRelativeEffectPath()));
				particle.afterDeserialize(effect);
			}

			entity.afterDeserialize();
		}
	}

	public boolean save (EditorScene scene) {
		for (EditorEntity entity : scene.entities)
			entity.beforeSerialize();

		try {
			Output output = new Output(new FileOutputStream(getFileHandleForScene(scene).file()));
			kryo.writeObject(output, scene);
			output.close();
			return true;
		} catch (FileNotFoundException e) {
			Log.exception(e);
		}

		return false;
	}

	public void create (FileHandle relativeScenePath, SceneViewport viewport, int width, int height) {
		EditorScene scene = new EditorScene(relativeScenePath, viewport, width, height);
		save(scene);
	}

	public FileHandle getFileHandleForScene (EditorScene scene) {
		return assetsFolder.child(scene.path);
	}
}
