package com.swerly.mywifiheatmap;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by sethw on 8/29/2016.
 */
public class AnimatorHelper {
    Activity context;
    View content;

    public AnimatorHelper(Activity context, View content){
        this.context = context;
        this.content = content;
    }
    public AnimatorHelper(View content){
        this(null, content);
    }

    public Animator createRevealAnimator(boolean reversed, int x, int y, int radius, int ms) {
        float hypot = radius == -1 ? (float) Math.hypot(content.getHeight(), content.getWidth()) : radius;

        float startRadius = reversed ? hypot : 0;
        float endRadius = reversed ? 0 : hypot;

        Animator animator = ViewAnimationUtils.createCircularReveal(
                content, x, y,
                startRadius,
                endRadius);
        animator.setDuration(ms);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        if (reversed)
            animator.addListener(animatorListener);
        return animator;
    }

    public Animator createRevealAnimator(boolean reversed, int x, int y) {
        return createRevealAnimator(reversed, x, y, -1, 800);
    }

    private Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            content.setVisibility(View.INVISIBLE);

            if (context != null)
                context.finish();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };
}
