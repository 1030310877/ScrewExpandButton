package joe.com.screwbutton;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;

/**
 * Description
 * Created by chenqiao on 2016/10/19.
 */
public class ScrewExpandButton extends CircleButton implements View.OnClickListener {
    private int subBtnSize;
    private ViewGroup rootLayout;
    private ArrayList<CircleButton> buttons;
    private boolean isExpand = false;
    private FrameLayout container;
    private ArrayList<ScrewAnimator> animators;
    private ArrayList<ScrewDismissAnimator> dismissAnimators;
    private OnBtnClickListener btnClickListener;
    private boolean touchOutsideToDismiss = true;

    public ScrewExpandButton(Context context) {
        this(context, null);
    }

    public ScrewExpandButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrewExpandButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        buttons = new ArrayList<>();
        animators = new ArrayList<>();
        dismissAnimators = new ArrayList<>();
        if (context instanceof Activity) {
            rootLayout = (ViewGroup) ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        }
        container = new FrameLayout(context);
        setOnClickListener(this);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScrewExpandButton, defStyleAttr, 0);
        subBtnSize = a.getDimensionPixelSize(R.styleable.ScrewExpandButton_subbutton_size, -1);
        a.recycle();
    }

    public void setOnBtnClickListener(OnBtnClickListener btnClickListener) {
        this.btnClickListener = btnClickListener;
    }

    public void addButton(Drawable drawable, int btnId) {
        CircleButton btn = new CircleButton(getContext());
        btn.setImageDrawable(drawable);
        btn.setAlpha(0f);
        btn.setId(btnId);
        buttons.add(btn);
        btn.setOnClickListener(buttonsClickListener);
    }

    public void removeButton(int btnId) {
        for (CircleButton button : buttons) {
            if (button.getId() == btnId) {
                buttons.remove(button);
                button.setOnClickListener(null);
                break;
            }
        }
    }

    private OnClickListener buttonsClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (btnClickListener != null) {
                btnClickListener.onBtnClick(v.getId());
            }
        }
    };

    @Override
    public void onClick(View v) {
        anchor = getAbsoluteXAndY();
        collapseTimes = 0;
        clearAnimators();
        if (!isExpand) {
            expand();
        } else {
            collapse();
        }
    }

    private int collapseTimes = 0;

    public void collapse() {
        container.setOnClickListener(null);
        for (int i = buttons.size() - 1; i >= 0; i--) {
            CircleButton button = buttons.get(i);
            ScrewDismissAnimator dismissAnimator = new ScrewDismissAnimator(button, i, (FrameLayout.LayoutParams) button.getLayoutParams());
            dismissAnimator.start();
            dismissAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    collapseTimes++;
                    if (collapseTimes == buttons.size()) {
                        if (rootLayout != null) {
                            rootLayout.removeView(container);
                        }
                        container.removeAllViews();
                        isExpand = false;
                        collapseTimes = 0;
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    collapseTimes++;
                    if (collapseTimes == buttons.size()) {
                        if (rootLayout != null) {
                            rootLayout.removeView(container);
                        }
                        container.removeAllViews();
                        isExpand = false;
                        collapseTimes = 0;
                    }
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            dismissAnimators.add(dismissAnimator);
        }
    }

    int[] anchor;

    public void expand() {
        if (subBtnSize < 0) {
            subBtnSize = getWidth();
        }
        if (rootLayout != null) {
            rootLayout.addView(container, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        if (touchOutsideToDismiss) {
            container.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    collapse();
                }
            });
        } else {
            container.setOnClickListener(null);
        }
        for (int i = 0; i < buttons.size(); i++) {
            CircleButton btn = buttons.get(i);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(subBtnSize, subBtnSize);
            params.leftMargin = anchor[0];
            params.topMargin = anchor[1];
            container.addView(btn, params);
            ScrewAnimator animator = new ScrewAnimator(btn, i, params);
            animator.start();
            animators.add(animator);
        }
        isExpand = true;
    }

    private int[] getAbsoluteXAndY() {
        int[] result = new int[2];
        result[0] = (int) getX();
        result[1] = (int) getY();
        ViewParent parent = getParent();
        while (parent != null) {
            if (parent instanceof View) {
                if (((View) parent).getId() != android.R.id.content) {
                    result[0] += ((View) parent).getX();
                    result[1] += ((View) parent).getY();
                } else {
                    return result;
                }
            }
            parent = parent.getParent();
        }
        return result;
    }

    private void clearAnimators() {
        for (ScrewAnimator animator : animators) {
            animator.cancelAnimator();
        }
        for (ScrewDismissAnimator dismissAnimator : dismissAnimators) {
            dismissAnimator.cancelAnimator();
        }
        animators.clear();
        dismissAnimators.clear();
    }

    public void setTouchOutsideToDismiss(boolean touchOutsideToDismiss) {
        this.touchOutsideToDismiss = touchOutsideToDismiss;
    }

    public interface OnBtnClickListener {
        void onBtnClick(int btnId);
    }

    public class ScrewAnimator {
        private int index;
        private FrameLayout.LayoutParams params;
        private CircleButton target;
        private AnimatorSet set;

        ScrewAnimator(CircleButton target, int index, FrameLayout.LayoutParams params) {
            this.target = target;
            this.index = index;
            this.params = params;
            init();
        }

        private void init() {
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(target, "alpha", 0f, 1f);
            alphaAnimator.setDuration(150 + 80 * (index + 1));
            ValueAnimator animator = ValueAnimator.ofFloat(0f, index / 7f);
            animator.setTarget(target);
            animator.setDuration(150 + 80 * (index + 1));
            animator.setInterpolator(new DecelerateInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float t = (float) animation.getAnimatedValue();
                    float r = (getWidth() + 5) * (1 + t);
                    int x = (int) (r * Math.cos(Math.toRadians(t * 360)));
                    int y = (int) (r * Math.sin(Math.toRadians(t * 360)));
                    params.leftMargin = anchor[0] + x;
                    params.topMargin = anchor[1] + y;
                    if (target.getParent() == container) {
                        container.updateViewLayout(target, params);
                    } else {
                        animation.cancel();
                    }
                }
            });
            set = new AnimatorSet();
            set.play(alphaAnimator).with(animator);
        }

        boolean isAnimating() {
            if (set == null) {
                return false;
            }
            return set.isRunning();
        }

        void start() {
            if (set != null) {
                set.start();
            }
        }

        void cancelAnimator() {
            if (set != null) {
                set.cancel();
            }
        }
    }

    public class ScrewDismissAnimator {
        private int index;
        private FrameLayout.LayoutParams params;
        private CircleButton target;
        private AnimatorSet set;

        ScrewDismissAnimator(CircleButton target, int index, FrameLayout.LayoutParams params) {
            this.target = target;
            this.index = index;
            this.params = params;
            init();
        }

        private void init() {
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(target, "alpha", 1f, 0f);
            alphaAnimator.setDuration(150 + 80 * (index + 1));
            ValueAnimator animator = ValueAnimator.ofFloat(index / 7f, 0f);
            animator.setTarget(target);
            animator.setDuration(150 + 80 * (index + 1));
            animator.setInterpolator(new AccelerateInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float t = (float) animation.getAnimatedValue();
                    float r = (getWidth() + 5) * (1 + t);
                    int x = (int) (r * Math.cos(Math.toRadians(t * 360)));
                    int y = (int) (r * Math.sin(Math.toRadians(t * 360)));
                    params.leftMargin = anchor[0] + x;
                    params.topMargin = anchor[1] + y;

                    if (target.getParent() == container) {
                        container.updateViewLayout(target, params);
                    } else {
                        animation.cancel();
                    }
                }
            });
            set = new AnimatorSet();
            set.play(alphaAnimator).with(animator);
        }

        void start() {
            if (set != null) {
                set.start();
            }
        }

        boolean isAnimating() {
            if (set == null) {
                return false;
            }
            return set.isRunning();
        }

        void addListener(Animator.AnimatorListener listener) {
            if (set != null) {
                set.addListener(listener);
            }
        }

        void cancelAnimator() {
            if (set != null) {
                set.cancel();
            }
        }
    }
}