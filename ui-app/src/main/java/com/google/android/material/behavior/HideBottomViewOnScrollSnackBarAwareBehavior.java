package com.google.android.material.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar.SnackbarLayout;

class HideBottomViewOnScrollSnackBarAwareBehavior<V extends View> extends HideBottomViewOnScrollBehavior<V> {

    public HideBottomViewOnScrollSnackBarAwareBehavior() {
        super();
    }

    public HideBottomViewOnScrollSnackBarAwareBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull V child, @NonNull View dependency) {
        return dependency instanceof SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull V child,
            @NonNull View dependency) {
        float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
        child.setTranslationY(translationY);
        return true;
    }

    @Override
    public void onDependentViewRemoved(@NonNull CoordinatorLayout parent, @NonNull V child,
            @NonNull View dependency) {
        child.setTranslationY(0);
    }
}
