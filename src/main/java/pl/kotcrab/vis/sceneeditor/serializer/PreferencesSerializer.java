
package pl.kotcrab.vis.sceneeditor.serializer;

import java.util.ArrayList;

import pl.kotcrab.vis.sceneeditor.SceneEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/** Json based SceneSerializer which saved data using {@link Preferences}
 * 
 * @author Pawel Pastuszak */
public class PreferencesSerializer extends AbstractJsonSerializer {
	private static final String TAG = "VisSceneEditor:PreferencesSerializer";

	private Preferences prefs;
	private String keyName;

	/** Constructs PreferencesSerializer
	 * 
	 * @param editor {@link SceneEditor} instance
	 * @param prefs instance of {@link Preferences}
	 * @param keyName name of key wich will be used when saving to prefenrees */
	public PreferencesSerializer (SceneEditor editor, Preferences prefs, String keyName) {
		super(editor);
		this.prefs = prefs;
		this.keyName = keyName;
	}

	@Override
	public boolean saveJsonData (ArrayList<ObjectInfo> infos) {
		prefs.putString(keyName, getJson().toJson(infos));
		prefs.flush();
		Gdx.app.log(TAG, "Saved changes to preferences.");
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<ObjectInfo> loadJsonData () {
		return getJson().fromJson(new ArrayList<ObjectInfo>().getClass(), prefs.getString(keyName));
	}

	@Override
	public boolean isReadyToLoad () {
		return prefs.contains(keyName);
	}
}