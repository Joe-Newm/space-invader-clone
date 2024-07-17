package com.space.invaders.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.space.invaders.SpaceInvaders;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                // Resizable application, uses available space in browser
                return new GwtApplicationConfiguration(true);
                // Fixed size application:
                //return new GwtApplicationConfiguration(480, 320);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new SpaceInvaders();
        }

//        @Override
//        public void onModuleLoad () {
//                FreetypeInjector.inject(new OnCompletion() {
//                        public void run () {
//                                // Replace HtmlLauncher with the class name
//                                // If your class is called FooBar.java than the line should be FooBar.super.onModuleLoad();
//                                HtmlLauncher.super.onModuleLoad();
//                        }
//                });
//        }
}