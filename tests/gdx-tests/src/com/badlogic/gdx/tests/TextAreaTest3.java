/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.StringBuilder;

// test for TextField#textHeight calculation change
public class TextAreaTest3 extends GdxTest {
	private Stage stage;
	private Skin skin;
	TextField textField;
	TextArea textArea;
	private TextField.TextFieldStyle styleDefault;
	private TextField.TextFieldStyle styleArial15;
	private TextField.TextFieldStyle styleArial32;
	private TextField.TextFieldStyle styleFont;

	@Override
	public void create () {
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		// default font in the skin has line height == text height, so its impossible to see updated selection/cursor rendering
		styleDefault = skin.get(TextField.TextFieldStyle.class);

		styleArial15 = new TextField.TextFieldStyle(styleDefault);
		styleArial15.font = new BitmapFont(Gdx.files.internal("data/arial-15.fnt"), Gdx.files.internal("data/arial-15_00.png"), false);

		styleArial32 = new TextField.TextFieldStyle(styleDefault);
		styleArial32.font = new BitmapFont(Gdx.files.internal("data/arial-32.fnt"), Gdx.files.internal("data/arial-32.png"), false);

		styleFont = new TextField.TextFieldStyle(styleDefault);
		styleFont.font = new BitmapFont(Gdx.files.internal("data/font.fnt"), Gdx.files.internal("data/font.png"), false);

		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		// easier to test this with proper layout
		Table root = new Table();
		root.setFillParent(true);
		stage.addActor(root);

		Table styleSelector = new Table();
		styleSelector.defaults().pad(10);
		root.add(styleSelector).row();
		styleSelector.add(newStyleButton("Default", styleDefault));
		styleSelector.add(newStyleButton("Arial 15", styleArial15));
		styleSelector.add(newStyleButton("Arial 32", styleArial32));
		styleSelector.add(newStyleButton("Font", styleFont));

		// | is the tallest char
		textField = new TextField("| Text field", styleArial32);
		root.add(textField).growX().pad(20, 100, 20, 100).row();

		StringBuilder sb = new StringBuilder("| Text Area\nEssentially, a text field\nwith\nmultiple\nlines.\n");
		// we need a bunch of lines to demonstrate that prefHeight is way too large
		for (int i = 0; i < 30; i++) {
			sb.append("It can even handle very loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong lines.\n");
		}
		textArea = new TextArea(sb.toString(), styleArial32);
		// we need a container that will allow the TextArea to be as tall as it wants
		// without the fix, text area height wont match text height depending on the fonts line height
		ScrollPane pane = new ScrollPane(textArea, skin);
		pane.setScrollingDisabled(true, false);
		pane.setScrollbarsVisible(true);
		pane.setFadeScrollBars(false);
		root.add(pane).grow().pad(20, 100, 20, 100);
	}

	private Button newStyleButton (final String label, final TextField.TextFieldStyle style) {
		TextButton button = new TextButton(label, skin);
		button.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				textField.setStyle(style);
				textArea.setStyle(style);
			}
		});
		return button;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
		// getLines() does not return correct count before first draw happens and there is no other way to trigger update
		textArea.setPrefRows(textArea.getLines());
		// change does not propagate on its own, number of lines does not change so calling this each frame is not necessary
		textArea.invalidateHierarchy();
	}

	@Override
	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void dispose () {
		stage.dispose();
		skin.dispose();
		styleArial15.font.getRegion().getTexture().dispose();
		styleArial32.font.getRegion().getTexture().dispose();
		styleFont.font.getRegion().getTexture().dispose();
	}
}
