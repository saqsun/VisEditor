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

package com.kotcrab.vis.runtime.scene;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.runtime.entity.Entity;
import com.kotcrab.vis.runtime.entity.TextEntity;

public class Scene implements Disposable {
	private AssetManager assetManager;
	private ShaderProgram distanceFieldShader;

	private OrthographicCamera camera;
	private Viewport viewport;

	private Array<Entity> entities;
	private Array<TextureAtlas> textureAtlases;

	public Scene (Array<Entity> entities, Array<TextureAtlas> textureAtlases, AssetManager assetsManager, SceneViewport viewportType, int width, int height) {
		this.entities = entities;
		this.textureAtlases = textureAtlases;
		this.assetManager = assetsManager;

		camera = new OrthographicCamera(width, height);
		camera.position.x = width / 2;
		camera.position.y = height / 2;
		camera.update();

		switch (viewportType) {
			case STRETCH:
				viewport = new StretchViewport(width, height, camera);
				break;
			case FIT:
				viewport = new FitViewport(width, height, camera);
				break;
			case FILL:
				viewport = new FillViewport(width, height, camera);
				break;
			case SCREEN:
				viewport = new ScreenViewport(camera);
				break;
			case EXTEND:
				viewport = new ExtendViewport(width, height, camera);
				break;
		}
	}

	public void onAfterLoad () {
		for (Entity entity : entities)
			entity.onAfterLoad();
	}

	public Array<Entity> getEntities () {
		return entities;
	}

	public Entity getById (String id) {
		for (Entity entity : entities)
			if (entity.getId() != null && entity.getId().equals(id)) return entity;

		throw new IllegalStateException("Entity with id: " + id + " not found");
	}

	public void render (SpriteBatch batch) {
		batch.setProjectionMatrix(camera.combined);

		boolean shader = false;

		batch.begin();

		for (Entity e : entities) {
			if (e instanceof TextEntity && ((TextEntity) e).isDistanceFieldShaderEnabled()) {
				shader = true;
				batch.setShader(distanceFieldShader);
			}

			e.render(batch);

			if (shader) batch.setShader(null);
		}

		batch.end();
	}

	public void resize (int width, int height) {
		viewport.update(width, height);
	}

	void getDistanceFieldShaderFromManager (FileHandle shader) {
		distanceFieldShader = (ShaderProgram) assetManager.get(new AssetDescriptor(shader, ShaderProgram.class));
	}

	@Override
	public void dispose () {
		for (Entity entity : entities) {
			if (entity instanceof Disposable) {
				Disposable disposable = (Disposable) entity;
				disposable.dispose();
			}
		}
	}
}
