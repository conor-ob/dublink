package ie.dublinmapper.util;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.bluelinelabs.conductor.changehandler.AnimatorChangeHandler;
import ie.dublinmapper.MvpBaseController;

/**
 * An {@link AnimatorChangeHandler} that will perform a circular reveal
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class SearchControllerChangeHandler extends MvpBaseControllerChangeHandler {

    private static final String KEY_CX = "SearchControllerChangeHandler.cx";
    private static final String KEY_CY = "SearchControllerChangeHandler.cy";

    private int cx;
    private int cy;

    public SearchControllerChangeHandler() { }

    /**
     * Constructor that will create a circular reveal from the center of the fromView parameter.
     * @param fromView The view from which the circular reveal should originate
     * @param containerView The view that hosts fromView
     */
    public SearchControllerChangeHandler(@NonNull View fromView, @NonNull View containerView, @NonNull MvpBaseController controller) {
        super(controller, 500L, true);

        int[] fromLocation = new int[2];
        fromView.getLocationInWindow(fromLocation);

        int[] containerLocation = new int[2];
        containerView.getLocationInWindow(containerLocation);

        int relativeLeft = fromLocation[0] - containerLocation[0];
        int relativeTop  = fromLocation[1] - containerLocation[1];

        cx = fromView.getWidth() / 2 + relativeLeft;
        cy = fromView.getHeight() / 2 + relativeTop;
    }

    @Override @NonNull
    protected Animator getAnimator(@NonNull ViewGroup container, View from, View to, boolean isPush, boolean toAddedToContainer) {
        final float radius = (float) Math.hypot(cx, cy);
        Animator animator = null;
        if (isPush && to != null) {
            animator = ViewAnimationUtils.createCircularReveal(to, cx, cy, 0, radius);
        } else if (!isPush && from != null) {
            animator = ViewAnimationUtils.createCircularReveal(from, cx, cy, radius, 0);
        }
        return addAnimationListener(isPush, animator);
    }

    @Override
    protected void resetFromView(@NonNull View from) { }

    @Override
    public void saveToBundle(@NonNull Bundle bundle) {
        super.saveToBundle(bundle);
        bundle.putInt(KEY_CX, cx);
        bundle.putInt(KEY_CY, cy);
    }

    @Override
    public void restoreFromBundle(@NonNull Bundle bundle) {
        super.restoreFromBundle(bundle);
        cx = bundle.getInt(KEY_CX);
        cy = bundle.getInt(KEY_CY);
    }

}
