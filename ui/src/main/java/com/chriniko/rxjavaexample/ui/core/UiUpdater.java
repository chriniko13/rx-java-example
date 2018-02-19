package com.chriniko.rxjavaexample.ui.core;

import javafx.application.Platform;

public interface UiUpdater {

    default void update(Task task) {
        Platform.runLater(task::execute);
    }

}
